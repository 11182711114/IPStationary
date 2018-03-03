package w02.u2_1_2.communication;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import log.Log;
import util.Throttler;
import w02.u2_1_2.Server;
import w02.u2_1_2.communication.IO.SocketReader;
import w02.u2_1_2.communication.IO.SocketWriter;

public class Connection implements Runnable {
	private static final int DEFAULT_INTERVAL = 1000 / 10;

	private ClientInfo client;
	private Socket socket;
	private SocketWriter<String> writer;
	private Thread writerThread;
	private SocketReader<String> reader;
	private Thread readerThread;
	private Server server;

	private Queue<String> out;
	
	private boolean running;

	private Log log;

	public Connection(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		this.out = new LinkedList<String>();
//		this.out = new LinkedBlockingQueue<String>(1024);
		this.client = ClientInfo.create(socket.getInetAddress(), socket.getPort());
		this.log = Log.getLogger(this.getClass().getSimpleName() + ":" + client.toString());
	}

	public void start() throws IOException {
		this.writer = new SocketWriter<String>(socket.getOutputStream());
		this.reader = new SocketReader<String>(socket.getInputStream());
		writerThread = new Thread(writer);
		readerThread = new Thread(reader);
		writerThread.start();
		readerThread.start();
		
	}
	
	public boolean send(String msg) {
		log.trace("Sending: " + msg);
		return writer.add(msg);
	}

	private void sendToRest(String msg) {
		List<Connection> connections = server.getConnections();
		connections.forEach(conn -> {
			if (conn != this)
				conn.send(msg);
		});
	}

	@Override
	public void run() {
		try {
			start();
		} catch (IOException e1) {
			log.exception(e1);
		}
		log.debug("Connection starting");
		running = true;
		long lastRun = System.currentTimeMillis();
		while(running) {
			try {
				lastRun = Throttler.waitIfNecessary(lastRun, DEFAULT_INTERVAL);
			} catch (InterruptedException e) {
				log.exception(e);
			}
			if (socket.isClosed()) {
				running = false;
				return;
			}
//			read();
			readReader();			
			write();
		}
		reader.stop();
		writer.stop();
		readerThread.interrupt();
		writerThread.interrupt();
	}
	
	private void readReader() {
		while (reader.available()) {
			sendToRest(client + ": " + reader.poll());
		}
	}

	private void write() {
		
		while(!out.isEmpty())
			writer.add(out.poll());
		
//		DataOutputStream pw = null;
//		try {
//			pw = new DataOutputStream(socket.getOutputStream());
//			while (!out.isEmpty()) {
//				pw.writeUTF(out.poll());
//			}
//		} catch (IOException e) {
//			log.error("Error getting OutputStream from socket\n" + e.getMessage());
//		}
	}
	
	private void read() {
		
		while (reader.available())
			out.add(reader.poll());
		
//		DataInputStream reader = null;
//		try {
////			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			reader = new DataInputStream(socket.getInputStream());
//		} catch (IOException e) {
//			log.error("Error getting InputStream from socket\n" + e.getMessage());
//		}
//
//		if (reader != null) {
//			try {
//				while(reader.available() > 0) {
//					String line = reader.readUTF();
//					in.offer(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "::" + line);
////					out.offer(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "::" + line);
//				}
//			} catch (IOException e) {
//				log.exception(e);
//			}
//		}

	}

	public void stop() {
		running = false;
	}

}
