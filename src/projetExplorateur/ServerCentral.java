package projetExplorateur;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import java.net.SocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class ServerCentral {
	ServerSocketChannel ssc;
	Selector selector;
	SerializerBuffer buffer ;
	final static Charset charset = Charset.forName("UTF-8");
	private HashSet<String> urlToCrawl=new HashSet<String>();
	private HashSet<String> urlAlreadyCrawled=new HashSet<String>();
	private HashSet<String> urlACrawledthisTime=new HashSet<String>();
	private HashSet<String> urLBeingCrawled=new HashSet<String>();
	ByteBuffer buffer2 = ByteBuffer.allocateDirect(200048);
	private Map<String, List<String>> mapIndex=new TreeMap<String,List<String>>();
	private Map<String, HashSet<String>> urlIndex=new TreeMap<String,HashSet<String>>();
	private String urlPricipale=null;
	long startTime ;
	long endTime;
	WebServer session=null;
	boolean start=false;
	boolean end=false;
	boolean endexploration=false;
	static int nbMaxUrlAexplore=10;
	int nbexplo=0;
	SocketChannel clientsock=null;
	public ServerCentral(int port) throws IOException {
		try {
			loadUrlAlreadyCrawled();
			loadMapIndexInFile();
			loadUrlIndexFile();
			System.out.println("chargement de données des l'application reussi ");
		} catch (IOException e) {
			urlIndex.clear();
			mapIndex.clear();
			urlAlreadyCrawled.clear();
		}
		ssc = ServerSocketChannel.open();
		selector = Selector.open();
		SocketAddress sa = new InetSocketAddress(port);
		ssc.bind(sa);
		ssc.configureBlocking(false);
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		buffer =  new SerializerBuffer(ByteBuffer.allocateDirect(500000024));
	}

	public static String readLine(FileInputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int b;
		while ((b = in.read()) >= 0) {
			if (b == '\n') break;
			if (b != '\r') sb.append((char) b);
		}
		if (b == -1 && sb.length() == 0) return null;
		return sb.toString();
	}

	public void loadUrlAlreadyCrawled() throws IOException {
		File f= new File("urlAlreadyCrawled.text");
		FileInputStream fi = new FileInputStream(f);
		String s=readLine(fi);
		while(s!=null) {
			urlAlreadyCrawled.add(s);
			s=readLine(fi);		
		}
		fi.close();
	}

	public void saveURLInFile() throws IOException{
		File f= new File("urlAlreadyCrawled.text");
		FileOutputStream  os = new  FileOutputStream (f);
		for (String string : urlAlreadyCrawled) {
			os.write((string+'\n').getBytes());
		}
		os.close(); 
	}
	public void saveUrlIndexInFile() throws IOException{
		File f= new File("urlIndexsave.text");
		FileOutputStream  os = new  FileOutputStream (f);
		for(Entry<String, HashSet<String>> entry : urlIndex.entrySet()) {
			String key = entry.getKey();
			os.write((key+'\n').getBytes());
			HashSet<String> value = entry.getValue();
			os.write(("debutValeur"+'\n').getBytes());
			for (String val : value) {
				os.write((val+'\n').getBytes());
			}
			os.write(("finValeur"+'\n').getBytes());
		}
		os.close(); 
	}
	public void saveMapIndexInFile() throws IOException{
		File f= new File("mapIndexsave.text");
		FileOutputStream  os = new  FileOutputStream (f);
		for(Map.Entry<String, List<String>> entry : mapIndex.entrySet()) {
			String key = entry.getKey();
			os.write((key+'\n').getBytes());
			List<String> value = entry.getValue();
			os.write(("debutValeur"+'\n').getBytes());
			for (String val : value) {
				os.write((val+'\n').getBytes());
			}
			os.write(("finValeur"+'\n').getBytes());
		}
		os.close(); 
	}
	public void loadMapIndexInFile() throws IOException {
		File f= new File("mapIndexsave.text");
		FileInputStream fi = new FileInputStream(f);
		String s=readLine(fi);

		while(s!=null) {
			String key=s;
			s=readLine(fi);
			if(s.equals("debutValeur")) {
				List<String> value=new ArrayList<>();
				while((!s.equals("finValeur")&&(s!=null))) {
					value.add(s);
					s=readLine(fi);	
				}		
				mapIndex.put(key, value);
			}
			s=readLine(fi);		
		}
		fi.close();
	}

	public void loadUrlIndexFile() throws IOException {
		File f= new File("urlIndexsave.text");
		FileInputStream fi = new FileInputStream(f);
		String s=readLine(fi);
		while(s!=null) {
			String key=s;
			s=readLine(fi);
			if(s.equals("debutValeur")) {
				HashSet<String> value=new HashSet<String>();
				while((!s.equals("finValeur")&&(s!=null))) {
					value.add(s);
					s=readLine(fi);	
				}
				urlIndex.put(key, value);
			}
			s=readLine(fi);		
		}
		fi.close();
	}

	void accept(SelectionKey sk) throws IOException {
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
	}


	public static String decodeValue(String value) {
		try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}

	public Message readData(SelectionKey sk) throws IOException {
		try {
			SocketChannel sc = (SocketChannel)sk.channel();
			int nread = sc.read(buffer.byteBuffer);
			if(nread<0) {
				sc.close();
				return null;
			}
			buffer.byteBuffer.flip();

			Message t = buffer.readMySerialisable(Message.CREATOR);
			buffer.byteBuffer.mark();
			buffer.byteBuffer.clear();
			return t;
		} catch (BufferUnderflowException e) {
			buffer.byteBuffer.reset();
			buffer.byteBuffer.compact();
		} catch (IOException e) {
			sk.channel().close();
		}
		return null;
	}

	public String getUrlFromHttpResquet(String lines) {
		String url=null;
		for (int i = 0; i < lines.length(); i++) {
			if(lines.charAt(i)=='u') {
				String s=""+lines.charAt(i)+lines.charAt(i+1)+lines.charAt(i+2);
				if(s.equals("url")) {
					i=i+4;
					url="";
					while(lines.charAt(i)!=' ') {
						url=url+lines.charAt(i);
						i++;
					}
				}
			}
		}
		return url;
	}

	public String getMotFromHttpResquet(String lines) {
		String mot=null;
		for (int i = 0; i < lines.length(); i++) {
			if(lines.charAt(i)=='m') {
				String s=""+lines.charAt(i)+lines.charAt(i+1)+lines.charAt(i+2);
				if(s.equals("mot")) {
					i=i+4;
					mot="";
					while(lines.charAt(i)!=' ') {
						mot=mot+lines.charAt(i);
						i++;
					}
				}
			}
		}
		return mot;
	}

	public  synchronized void doRead2(SelectionKey sk) throws IOException {
		String msg=readData2(sk);
		if(msg!=null) {
			String[] lines = msg.toString().split("\r\n");
			SocketChannel sc = (SocketChannel)sk.channel();
			if(lines[0].startsWith("GET")) {
				String url=getUrlFromHttpResquet(lines[0]);
				if(url!=null) {
					if(nbexplo==0) {
						System.out.println("veille connect un explorateur");
					}
					sk.attach("web");
					String value=decodeValue(url);
					if(urlAlreadyCrawled.contains(value)) {
						urlACrawledthisTime.clear();
						session = new WebServer(sc);
						session.sendResponse(session.handle4(urlIndex.get(url)));
						urlACrawledthisTime.clear();
						sc.close();
					}else {
						urlACrawledthisTime.clear();
						urLBeingCrawled.clear();
						urlToCrawl.clear();
						System.out.println("exploration en cour vieille patiente ");
						clientsock=(SocketChannel)sk.channel();
						start=true;
						end=false;
						urlPricipale=url;
						urlToCrawl.add(decodeValue(url));
						startTime=System.currentTimeMillis();
					}
				}else {
					String mot=getMotFromHttpResquet(lines[0]);
					if(mot!=null) {
						session = new WebServer(sc);
						List<String> list=urlQuiContientLememeMot(mapIndex,mot);
						list.add(mot);
						session.sendResponse(session.handle3(list));
					}else {
						session = new WebServer(sc);
						session.sendResponse(session.handle());
						sc.close();
					}
				}

			}else if (lines[0].equals("JOIN EXPLORATION")) {
				sk.interestOps(SelectionKey.OP_WRITE);
				sk.attach("EXPLORATEUR");
				nbexplo++;
			}
		}
	}


	public String readData2(SelectionKey sk) throws IOException {
		try {
			SocketChannel channel = (SocketChannel)sk.channel();
			buffer2.limit(buffer2.capacity());
			int read = channel.read(buffer2);
			if (read == -1) {
				channel.close();
				return null;
			}
			buffer2.flip();
			String s = charset.decode(buffer2).toString();
			buffer2.mark();
			buffer2.clear();
			return s;

		} catch (BufferUnderflowException e) {
			buffer.byteBuffer.reset();
			buffer.byteBuffer.compact();

		} catch (IOException e) {
			sk.channel().close();
		}
		return null;
	}


	public  synchronized void doRead(SelectionKey sk) throws IOException {
		Message msg=readData(sk);
		if(msg!=null) {
			String[] lines = msg.toString().split("\r\n");
			switch (lines[0]) {
			case "URL":
				addUrlToList(lines);
				break;
			case "URL INCORRECT":
				urLBeingCrawled.remove(lines[1]);
				giveUrlToExploerer(sk);
				break;
			case "WORD":
				addwordToList(lines);
				giveUrlToExploerer(sk);
				break;
			default:
				sk.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	private synchronized void doWrite(SelectionKey sk,String url) throws IOException{
		SocketChannel sc = (SocketChannel)sk.channel();
		try {
			Message msg1 = new Message(url);
			buffer.writeMySerialisable(msg1);
			buffer.byteBuffer.flip();
			sc.write(buffer.byteBuffer);
			buffer.byteBuffer.clear();
			sk.interestOps(SelectionKey.OP_READ);
		} catch (IOException e) {
			sk.channel().close();
		}

	}

	public synchronized void giveUrlToExploerer(SelectionKey sk) throws IOException {
		if(urlToCrawl.size()>0) {
			java.util.Iterator<String> iter = urlToCrawl.iterator();
			String url=iter.next();
			urLBeingCrawled.add(url);
			iter.remove();
			sk.interestOps(SelectionKey.OP_WRITE);
			doWrite(sk,"URL"+"\r\n"+url+"\r\n");
			sk.interestOps(SelectionKey.OP_READ);
		}else{
			sk.interestOps(SelectionKey.OP_WRITE);
		}
	}

	public  synchronized void addUrlToList(String[] lines) {
		if(urLBeingCrawled.contains(lines[1])){
			urlAlreadyCrawled.add(lines[1]);
			urlACrawledthisTime.add(lines[1]);
		}
		urLBeingCrawled.remove(lines[1]);
		for (int i =2; i < lines.length; i++) {
			if(!urlAlreadyCrawled.contains(lines[i])) {
				urlToCrawl.add(lines[i]);
			}
		}
		if(urlACrawledthisTime.size()>=nbMaxUrlAexplore) {
			end=true;
		}
	}


	public  synchronized void addwordToList(String[] lines) {
		List<String> list=new ArrayList<>();
		for (int i =1; i < lines.length; i++) {
			insertWordIntoFile(lines[i],list);
		}
		mapIndex.put(lines[1], list);
	}

	public void insertWordIntoFile(String chaine,List<String> list){
		String [] mot=new String[chaine.length()];
		mot=chaine.split(" ");
		for(int i=0;i<mot.length;i++) {				
			if(!mot[i].equals(" ")) {
				if(mot[i].length()>1) {
					if(mot[i].charAt(mot[i].length()-1)==',' ||
							mot[i].charAt(mot[i].length()-1)=='.'|| 
							mot[i].charAt(mot[i].length()-1)==';'|| mot[i].charAt(mot[i].length()-1)==':') {
						String sousChaine1 = mot[i].substring(0, mot[i].length()-1);
						list.add(sousChaine1+'\n');
					}else {
						list.add( mot[i]+'\n');
					}
				}				
			}	
		}
	}

	public List<String> urlQuiContientLememeMot(Map<String, List<String>> mapIndex, String motAcherche){
		List<String> listDurl=new ArrayList<String>();
		for (Map.Entry<String,List<String>> mapentry : mapIndex.entrySet()) {
			if(mapentry.getValue().toString().contains(motAcherche)) {
				listDurl.add(mapentry.getKey());
			}
		}
		return listDurl;
	}

	public void run() throws IOException {
		boolean stop=false;
		while(!stop) {
			selector.select();

			for(SelectionKey sk : selector.selectedKeys()) {
				String type=(String) sk.attachment();
				if(sk.isAcceptable()) {
					accept(sk);	
				}else if(sk.isReadable()) {
					if(type==null) {
						doRead2(sk);
					}else if(type.equals("EXPLORATEUR")) {
						doRead(sk);
					}
				}else if(sk.isWritable()&&start) {
					giveUrlToExploerer(sk);
				}

			}
			if(start&&(startTime-System.currentTimeMillis()>=60000)) {
				if(urlToCrawl.size()<=0) {
					end=true;
				}
			}
			if(end) {
				start=false;
				if(clientsock!=null) {
					end=false;
					addUrlIndex(urlACrawledthisTime);
					session = new WebServer(clientsock);
					try {
						saveURLInFile();
						saveMapIndexInFile();
						saveUrlIndexInFile();
					} catch (IOException e) {
						System.out.println("echec du sauvegarde");
					}
					session.sendResponse(session.handle2(urlACrawledthisTime));
					clientsock.close();
					clientsock=null;
					urlACrawledthisTime.clear();
					urlToCrawl.clear();
				}
			}
			selector.selectedKeys().clear();
		}
		selector.selectedKeys().clear();
		selector.close();

	}
	public static String encodeValue(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}
	private void addUrlIndex(HashSet<String> urlACrawledthisTime2) {
		if(urlPricipale!=null) {
			HashSet<String> temp=new HashSet<String>();
			for (String string : urlACrawledthisTime2) {
				temp.add(string);
			}
			for (String string : temp) {
				urlIndex.put(encodeValue(string), temp);
			}


		}
	}


	public static void main(String[] args) throws IOException {
		int n = 5487;
		try {
			n = Integer.parseInt(args[1]);
		}catch(Exception IO) {
			System.out.println("Use default port 5487");
		}
		ServerCentral s = new ServerCentral(n);
		s.run();

	}

}


