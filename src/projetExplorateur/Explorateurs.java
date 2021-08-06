package projetExplorateur;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.net.SocketAddress;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


public class Explorateurs {

	public final Charset charset = Charset.forName("UTF-8");
	boolean isConnected = false;
	boolean isJoin =false;
	public boolean isIsjoin() {
		return isJoin;
	}

	public void setIsjoin(boolean isjoin) {
		this.isJoin = isjoin;
	}

	SocketChannel socket;

	public boolean getConnected() {
		return isConnected;
	}
	
	public void setConnected(boolean is) {
		isConnected=is;
	}
	public Explorateurs(String url, int port) throws IOException, InterruptedException {
		SocketAddress sa = new InetSocketAddress(url, port);
		socket = SocketChannel.open();
		socket.configureBlocking(true);
		socket.connect(sa);
		isConnected = true;

	}	

	public static void main(String[] args) throws IOException, InterruptedException {
		Explorateurs c = new Explorateurs("localhost",5487);
		System.out.println("Connecting to Server on port 5487 ...");
		ExploreURL rn=new ExploreURL(c);
		Thread t1 = new Thread(rn);
		t1.start();
	}


}
