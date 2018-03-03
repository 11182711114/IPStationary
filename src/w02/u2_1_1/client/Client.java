package w02.u2_1_1.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import log.Log;

public class Client {
	private static final int DEFAULT_INTERVAL = 1000 / 10;
	
	private static final int DEFAULT_PORT = 2000;
	private static final String DEFAULT_HOST = "127.0.0.1";
	
	private static final int DEFAULT_SERVER_PORT = 7645;
	private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
	
	private static final String DEFAULT_LOG_FILE = "logs/w02u2_1_1/client.log";
	private static final boolean DEFAULT_LOG_APPEND = true;

	
	private String host = DEFAULT_HOST;
	private int port = DEFAULT_PORT;
	
	private String serverHost = DEFAULT_SERVER_HOST;
	private int serverPort = DEFAULT_SERVER_PORT;
	
	private Socket socket;
	
	
	private File logFile = new File(DEFAULT_LOG_FILE);
	private boolean logAppend = DEFAULT_LOG_APPEND;
	private Log log;
	
	private Queue<String> consoleInput;
	private Queue<String> socketInput;
	
	private boolean running;
	
	public Client(String host, int port) {
		this.host = host;
		this.port = port;
		
		this.consoleInput = new LinkedList<String>();
	}
	public Client(String host) {
		this(host, DEFAULT_PORT);
	}
	public Client() {
		this(DEFAULT_HOST);
	}
	
	public boolean initialize() {
		Log.startLog(logFile, logAppend);
		log = Log.getLogger(this.getClass().getSimpleName());
		try {
			socket = new Socket(serverHost, serverPort, InetAddress.getByName(host), port);
//			socket.bind(new InetSocketAddress(host, port));
		} catch (IOException e) {
			log.exception(e);
			log.error("Failed to create Socket with remote: " + serverHost + ":" + serverPort + " local: "+ host + ":" + port);
			log.stop();
			System.out.println("Could not connect to server, shutting down");
			return false;
		}
		return true;
	}
	
	public void start() {
		log.info("Starting client: " + host + ":" + port);
		running = true;
		run();
	}
	
	public void run() {
		try ( 
			PrintWriter outSocket = new PrintWriter(socket.getOutputStream());
			BufferedReader inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outConsole = new PrintWriter(System.out);
			BufferedReader inConsole = new BufferedReader(new InputStreamReader(System.in));
		) {
			long lastRun = System.currentTimeMillis();
			while(running) {
				// Lets not use 100% cpu
				long timeDiff = System.currentTimeMillis() - lastRun;
				if (timeDiff < DEFAULT_INTERVAL)
					Thread.sleep(DEFAULT_INTERVAL - timeDiff);
				lastRun = System.currentTimeMillis();
				
				if (socket.isClosed())
					return;
				
				readFromServer(inSocket);
				readFromConsole(inConsole);
				sendToSever(outSocket);
				printFromServer(outConsole);
			}
		} catch (IOException e) {
			log.exception(e);
			log.debug("Client failed to establish read or write to socket or console");
		} catch (InterruptedException e) {
			log.exception(e);
		}
	}
	
	private void printFromServer(PrintWriter outConsole) {
		while(!socketInput.isEmpty()) {
			String line = socketInput.poll();
			outConsole.println(line);
		}
	}
	
	private void readFromServer(BufferedReader inSocket) {
		try {
			while(inSocket.ready()) {
				StringBuilder sb = new StringBuilder();
				for (String token = inSocket.readLine(); token != "<END>"; token = inSocket.readLine()) {
					inSocket.readLine();
					sb.append(token);
				}
				inSocket.read(); // Read the <END> token
				socketInput.add(sb.toString());
			}
		} catch (IOException e) {
			log.exception(e);
		}
	}
	
	private void sendToSever(PrintWriter outSocket) {
		log.trace("Checking if input is empty");
		
		if (outSocket == null) {
			log.trace("PrintWriter outSocket is null");
			return;
		}
		
		while(!consoleInput.isEmpty()) {
			log.debug("Sending to server");
			
			String line = consoleInput.poll();
			outSocket.println(line + " <END>");
			
			
		}
	}
	
	public void readFromConsole(BufferedReader inConsole) {
		try {
			log.trace("Checking if console is ready for reading: " + inConsole.ready());
			while(inConsole.ready()) {
				String in = inConsole.readLine();
				log.trace("Reading from console: " + in);
				consoleInput.offer(in);
			}
		} catch (IOException e) {
			log.exception(e);
		}
	}
	
	public static void main(String[] args) {
		Client client = null;
		if (args.length == 2)
			client = new Client(args[0], Integer.parseInt(args[1]));
		else if (args.length == 1) 
			client = new Client(args[0]);
		else 
			client = new Client();
		
		if (client.initialize())
			client.start();
		
		
	}
	

}