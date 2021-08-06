package projetExplorateur;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import parser.ParserUrl;


public class ExploreURL implements Runnable{

	Explorateurs explo;
	SerializerBuffer serializerBuffer = new SerializerBuffer(ByteBuffer.allocateDirect(50000024));
	Charset charset = Charset.forName("UTF-8");
	CharsetEncoder encoder = charset.newEncoder();
	boolean askJoin=false;
	boolean askUrl=false;
	String urlRecu=null;
	int i=0;

	public ExploreURL(Explorateurs explorateur) {
		this.explo=explorateur;
	}

	public String getSouLien(String url) {
		int nombreDeSlah=0;
		String ret="";
		for (int i = 0; i < url.length(); i++) {
			if(url.charAt(i)=='/') {
				nombreDeSlah++;
			}
			if(nombreDeSlah>=3) {
				i=url.length();

			}else {
				ret=ret+url.charAt(i);
			}
		}
		return ret;

	}
	public String listToString(List<String> ListDurl,String url){
		StringBuilder builder=new StringBuilder();
		String  soulien=getSouLien(url);
		for (String urlName : ListDurl) {
			if(urlName!=null) {
				if(urlName.startsWith(soulien)) {
					builder.append(urlName).append("\r\n");
				}else if(!urlName.startsWith("http")) {
					builder.append(soulien+urlName).append("\r\n");
				}
			}

		}
		return builder.toString();		
	}
	public String wordToString(List<String> ListDurl){
		StringBuilder builder=new StringBuilder();
		for (String urlName : ListDurl) {
			builder.append(urlName).append("\r\n");
		}
		return builder.toString();		
	}

	public String getContentType(String s) throws IOException {
		URL u = null;
		try {
			u = new URL(s);

		} catch (MalformedURLException e) {
			return "url inccoret";
		}
		return u.openConnection().getContentType();
	}	

	public void closeConnexion(Explorateurs explo) throws IOException {
		System.out.println("Deconnection -1");
		explo.socket.close();
		explo.setConnected(false);
	}

	public void sendUrlToServer(String url)  {
		String contentType;
		try {
			contentType = getContentType(url);
			if(contentType.startsWith("text/html")) {
				ParserUrl u =new ParserUrl();
				u.parseurl(url);
				sendDataToServer("URL"+"\r\n"+url+"\r\n"+listToString(u.getListUrl(),url));
				sendDataToServer("WORD"+"\r\n"+url+"\r\n"+wordToString(u.getListMot()));

			}else {
				sendDataToServer("URL INCORRECT"+"\r\n"+url);
			}
		} catch (IOException |NullPointerException e) {
			sendDataToServer("URL INCORRECT"+"\r\n"+url);
		}

	}

	public void sendDataToServer(String information)  {
		try {
			serializerBuffer.byteBuffer.clear();
			Message msg1 = new Message(information);
			serializerBuffer.writeMySerialisable(msg1);
			serializerBuffer.byteBuffer.flip();
			explo.socket.write(serializerBuffer.byteBuffer);
			serializerBuffer.byteBuffer.clear();
		} catch (IOException e) {
			try {
				closeConnexion(explo);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} 
	}

	public Message readData() throws IOException {
		int nbread= explo.socket.read(serializerBuffer.byteBuffer);
		if(nbread<0) {
			closeConnexion(explo);
			return null;
		}
		serializerBuffer.byteBuffer.flip();
		try {
			Message msg = serializerBuffer.readMySerialisable(Message.CREATOR);
			serializerBuffer.byteBuffer.mark();
			serializerBuffer.byteBuffer.clear();
			return msg;
		} catch (BufferUnderflowException e) {
			serializerBuffer.byteBuffer.reset();
			serializerBuffer.byteBuffer.compact();
		}
		return null;
	}

	public void sendJoinExploration() throws IOException {
		if(!askJoin) {
			explo.socket.write(encoder.encode(CharBuffer.wrap("JOIN EXPLORATION"+"\r\n")));
			askJoin=true;
		}else {
			explo.setIsjoin(true);
		}

	}

	@Override
	public  void run() {
		try {
			while(explo.getConnected()) {
				if(explo.isJoin) {
					Message msg=readData();
					if(msg!=null) {
						String[] lines = msg.toString().split("\r\n");
						switch (lines[0]) {
						case "URL":
							sendUrlToServer(lines[1]);
							break;
						default:
							System.out.println("valeur de line explorateur: "+lines[0]);
						}
					}

				}else {
					sendJoinExploration();
				}
			}
		} catch (IOException e) {
			System.out.println("Deconnection Exception "+e);
			explo.setConnected(false);
		}
	}

}

