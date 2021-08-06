package projetExplorateur;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Server {
	List<String> listDeMot=new ArrayList<String>();
	private ServerSocketChannel ssc;
	private Selector selector;
	private ByteBuffer bb;
	private HashSet<String> listDurl=new HashSet<String>();
	private Map<String, List<String>> mapIndex=new TreeMap<String,List<String>>();

	private HashSet<String> listDurlExplorer=new HashSet<String>();
	private String s;
	private String urlName;
	private int i=0;
	private int numeroExplorateur=0;
	private String encoursdexploration="";


	public Server() throws IOException {
		new File("sauvegarde.txt");
		ssc = ServerSocketChannel.open();
		selector = Selector.open();
		bb = ByteBuffer.allocateDirect(3024);	
		SocketAddress sa = new InetSocketAddress(5486);
		ssc.bind(sa);
		ssc.configureBlocking(false);
		ssc.register(selector, SelectionKey.OP_ACCEPT);
	}

	void accept() throws IOException {
		SocketChannel sc = ssc.accept();
		System.out.println("Nouvelle connection "+sc.getLocalAddress());
		sc.configureBlocking(false);
		sc.register(selector,SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	}
	public synchronized void run() throws IOException, InterruptedException {

		while(true) {	
			selector.select();			
			for(SelectionKey sk : selector.selectedKeys()) {
				if(sk.isAcceptable()) {
					accept();
					numeroExplorateur++;
					System.out.println("Explorateur "+ numeroExplorateur + " Accepted");					
				}else if(sk.isWritable()){
					if(listDurl.isEmpty()) {
						encoursdexploration=readKeyboard();					    	
					}else {
						if(!encoursdexploration.equals("") && !(listDeMot.isEmpty())) {
							// Insertion de l'url explorer et sa liste de mot dans l'index
							insert_Url_And_ListofWord_IntoIndex(encoursdexploration, listDeMot);	
						}					    	
						encoursdexploration=UrlAExplorer(listDurlExplorer, listDurl);
						System.out.println("Url Encours d'exploration "+ encoursdexploration+"\n");
						System.out.println("Il reste  "+ listDurl.size()+" a explorer"+"\n");					    	
					}	
					while(isUrlInFile(encoursdexploration)) {
						System.out.println("Cette Url a deja ete explorer ");
						encoursdexploration=readKeyboard();
					}

					sendMessageToExplorateur(encoursdexploration, sk);

					listDurlExplorer.add(encoursdexploration);
					afficheIndex(mapIndex);						
					System.out.println("taille de l'index :"+mapIndex.size());
					sk.interestOps(SelectionKey.OP_READ);


				}else if (sk.isReadable()) {							
					receiveMessageFromExplorateur(sk);			
					Thread.sleep(5000);		                
				}				
			}
			selector.selectedKeys().clear();
		}
	}
	// Methode qui permet d'envoyer une Url à un explorateur connecté
	public  void sendMessageToExplorateur( String msg, SelectionKey sk) throws IOException {
		SocketChannel Clientsock = (SocketChannel) sk.channel();
		try {
			byte[] message = new String(msg).getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(message);
			if(msg.equals("")) {
				System.out.println("Aucune Url a Explorer ");
				listDurl.clear();
			}else System.out.println("j'envoie  au client cette Url : "+msg+"\n");
			Clientsock.write(buffer);
			buffer.clear();
		}catch (IOException e) {
			System.out.println("Explorateur has Leave ");
			Clientsock.close();
		}
	}
	// reception des informations qui provient du client
	public  void receiveMessageFromExplorateur(SelectionKey sk) throws IOException {
		SerializerBuffer bb = new SerializerBuffer(ByteBuffer.allocateDirect(1000024));
		SocketChannel Clientsock = (SocketChannel) sk.channel();
		try {
			Clientsock.read(bb.byteBuffer);
			bb.byteBuffer.flip();
			Message message = bb.readMySerialisable(Message.CREATOR);
			bb.byteBuffer.clear();	

			System.out.println("Message received From Client: " + message.toString());

			recupererList_durl_et_de_Mot(message.toString(), listDurl, listDeMot);
			sauvegarde_du_type_et_taille_du_fichier(message.toString());

			if(message.toString().equals("Fin")) {			
				System.out.println("le client a fini d'envoye les url et Mots \n");	
				writeintoFile("sauvegarde.txt", encoursdexploration+'\n');

				sk.interestOps(SelectionKey.OP_WRITE);	
			}else if(message.toString().equals("Findexploration")){
				System.out.println("Vous avez atteint le nombre d'url à Explorer");
				listDurl.clear();
				sk.interestOps(SelectionKey.OP_WRITE);
			}else if(split2(message.toString()).equals("ExitSize")){
				sk.interestOps(SelectionKey.OP_WRITE);
				writeintoFile("sauvegarde.txt", encoursdexploration+'\n');
			}

		} catch (IOException e) {
			System.out.println("Explorateur has Leave ");
			Clientsock.close();
		}
	}
	// Methode qui renvoie une Url à explorer
	public synchronized String UrlAExplorer(HashSet<String> listDurlExplorer,HashSet<String> listDurl) {
		for (String urlName : listDurl) {
			if(!(this.listDurlExplorer.contains(urlName)) && !(isUrlInFile(urlName))) {
				System.out.println("Url A Explorer "+urlName+"\n");
				removetUrlInlistDurl(listDurl, urlName);
				return urlName;			
			}
		}
		return "";
	}
	//Methode qui permet de supprimer une url dans la liste des Url
	public synchronized void removetUrlInlistDurl(HashSet<String> listDurl, String url) {
		listDurl.remove(url);
	}
	// Methode qui renvoie les url qui contient le meme mot
	public List<String> urlQuiContientLememeMot(Map<String, HashSet<String>> mapIndex, String motAcherche){
		List<String> listDurl=new ArrayList<String>();
		for (Map.Entry<String,HashSet<String>> mapentry : mapIndex.entrySet()) {
			if(mapentry.getValue().toString().contains(motAcherche)) {
				listDurl.add( mapentry.getKey());
			}
		}
		return listDurl;
	}
	//methode qui affiche le contenu de la map
	public void afficheIndex(Map<String, List<String>> mapIndex) {
		for (Map.Entry<String,List<String>> mapentry : mapIndex.entrySet()) {
			System.out.println("clé: "+mapentry.getKey() 
			+ " | valeur: " + mapentry.getValue().toString());
		}
	}
	// Permet de decouper une chaine pour pouvoir recuperer les url ou mots envoyé par l'exporateur
	public void recupererList_durl_et_de_Mot(String chaine,HashSet<String> lisdurl, List<String> listdeMot){
		String [] mot=new String[chaine.length()];
		mot=chaine.split(" ");
		if(mot[mot.length-1].equals("ExitUrl")) {
			for(int i=0;i<mot.length;i++) {

				String sousChaine = mot[i].substring(0, 4);
				//verifie si c'est une Url
				if(sousChaine.equals("http")) {
					lisdurl.add(mot[i]);
				}
			}

		}else if(mot[mot.length-1].equals("ExitMot")) {

			listdeMot.clear();
			for(int i=0;i<mot.length;i++) {				
				listdeMot.add(mot[i]);
			}	
		}

	}
	public void sauvegarde_du_type_et_taille_du_fichier(String chaine){

		String [] mot=new String[chaine.length()];
		mot=chaine.split(" ");
		if(mot[mot.length-1].equals("ExitSize")) {
			writeintoFile("sauvegarde.txt", mot[0]+'\n');
		}else if(mot[mot.length-1].equals("ExitContent")) {
			writeintoFile("sauvegarde.txt", mot[0]+'\n');
		}
	}
	// Methode qui permet d'ajout l'url explorer est sa liste de mot dans la map
	public void insert_Url_And_ListofWord_IntoIndex(String url, List<String> listdemots) {
		List<String> list=new ArrayList<>();
		list.addAll(listdemots);
		mapIndex.put(url, list);
	}
	//permet de decouper une chaine
	public String split2(String chaine) {
		String [] mot=chaine.split(" ");
		return mot[mot.length-1];
	}
	// methode qui permet d'ecrire dans un fichier
	public void writeintoFile(String file, String url) {
		Path path = Paths.get(file);
		try {
			byte[] bs = url.getBytes();
			Path writtenFilePath = Files.write(path, bs, StandardOpenOption.APPEND );
			//System.out.println("Written content in file:\n"+ new String(Files.readAllBytes(writtenFilePath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// methode qui permet de lire sur l'entrée standard
	public String readKeyboard() {
		System.out.println("Veuillez Saisir l'Url");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			s = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	// methode qui permet de verifie si une url existe dans le fichier de sauvegarde
	public boolean isUrlInFile(String str) {
		List<String> lignes = null;
		try {
			lignes = Files.readAllLines(  
					FileSystems.getDefault().getPath("sauvegarde.txt"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if(lignes.contains(str)) {
			return true;
		}
		return false;
	}

	public void setEncoursdexploration(String encoursdexploration) {
		this.encoursdexploration = encoursdexploration;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("i'm a server and i'm waiting for new connection and buffer select...");
		Server s = new Server();
		s.run();

	}
}



