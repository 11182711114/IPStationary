package w02.u2_1_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
	
	Socket listeningSocket;
	InputStream in;
	OutputStream out;
	
	public Server(String ip, int port) throws UnknownHostException, IOException {
		listeningSocket = new Socket(ip, port);
		in = listeningSocket.getInputStream();
		out = listeningSocket.getOutputStream();
	}
	
	

}

class ClientInfo {
	InetAddress ip;
	int port;
	
	private ClientInfo(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public static ClientInfo create(InetAddress ip, int port) {
		return new ClientInfo(ip, port);
	}
	
	public static ClientInfo create(String ip, int port) throws UnknownHostException {
		return new ClientInfo(InetAddress.getByName(ip), port);
	}
	
	
	
}
