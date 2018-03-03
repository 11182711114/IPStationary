package w02.u2_1_1.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import log.Log;
import w02.u2_1_1.server.Server;

public class Connection implements Runnable {
	private static final int DEFAULT_INTERVAL = 1000 / 10;

	private static final String DELIMITER = "<END>";

	private ClientInfo client;
	private Socket socket;
	Server server;

	private Queue<String> in;
	private BlockingQueue<String> out;

	private boolean running;

	private Log log;

	public Connection(Socket socket) {
		this.socket = socket;
		this.in = new LinkedList<String>();
		this.out = new LinkedBlockingQueue<String>(1024);
		this.client = ClientInfo.create(socket.getInetAddress(), socket.getPort());
		this.log = Log.getLogger(this.getClass().getSimpleName() + ":" + client.toString());
	}

	public boolean send(String msg) {
		log.trace("Sending: " + msg);
		return out.offer(msg);
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
		log.debug("Connection starting");
		running = true;
		long lastRun = System.currentTimeMillis();
		while(running) {
			// Lets not use 100% cpu
			long timeDiff = System.currentTimeMillis() - lastRun;
			if (timeDiff < DEFAULT_INTERVAL)
				try {
					Thread.sleep(DEFAULT_INTERVAL - timeDiff);
				} catch (InterruptedException e) {
					log.exception(e);
				}
			lastRun = System.currentTimeMillis();
			if (socket.isClosed()) {
				running = false;
				return;
			}
			read();
			flushIn();			
			write();
		}
	}
	
	private void flushIn() {
		for (String sendToRest = in.poll(); !in.isEmpty(); in.poll()) {
			sendToRest(sendToRest);
		}
	}

	private void write() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			log.error("Error getting OutputStream from socket\n" + e.getMessage());
		}
		if (pw != null) {
			while (!out.isEmpty()) {
				pw.write(out.poll());
			}
		}
	}
	
	private void read() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			log.error("Error getting InputStream from socket\n" + e.getMessage());
		}

		if (reader != null) {
			try {
				while (reader.ready()) {

					StringBuilder sb = new StringBuilder();
					for (String s = reader.readLine(); reader.ready(); reader.readLine()) {
						log.trace("Read: " + s);
						if (s != DELIMITER) {
							log.trace("Adding "+ s + " to sequence");
							sb.append(s);
						} else {
							log.trace(s + " is delimiter, breaking current sequence");
							break;
						}
					}
					log.trace("Adding sequence " + sb.toString() + " to in");
					in.offer(sb.toString());
					out.offer(sb.toString());
				}
			} catch (IOException e) {
				log.exception(e);
			}
		}

	}

	public void stop() {
		running = false;
	}

}
