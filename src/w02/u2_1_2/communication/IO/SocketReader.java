package w02.u2_1_2.communication.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import log.Log;

/**
 * @author Fredrik
 *
 * @param <T>
 */
//TODO: Should this be parameterized for extending Seriablizable or just handling Serializables?
public class SocketReader<T extends Serializable> implements Runnable {
//	private static final int DEFAULT_INTERVAL = 1000 / 1000;
	
	private InputStream input;
	
	private BlockingQueue<T> inputBuffer;
	
	private boolean running;
	Log log;
	
	public SocketReader(InputStream input) {
		this.input = input;
		log = Log.getLogger(this.getClass().getSimpleName()+ "@" +Thread.currentThread().getName());
		inputBuffer = new LinkedBlockingQueue<T>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		log.debug("Reader starting");
		running = true;
		ObjectInputStream reader = null;
		try {
			reader = new ObjectInputStream(input);
		} catch (IOException e1) {
			log.exception(e1);
		}
		
		while(running) {
			try {
				log.trace("Waiting for data to read");
				Object read = reader.readObject();
				if (read != null) { // can be null if the thread is interrupted while waiting
					log.trace("Data to read found");
					if (read instanceof Serializable) {
						log.trace("Data is Serializable");
						inputBuffer.put((T) read); //FIXME This is bad? Should be bad
					}
				}
				
				
			} catch (IOException e) {
				log.exception(e);
			} catch (ClassNotFoundException e) {
				log.exception(e);
			} catch (InterruptedException e) {
				log.exception(e);
			}
		}
	}
	
	/** Bridge to {@link BlockingQueue#poll()}
	 * @return {@link T} - if available
	 */
	public T poll() {
		return inputBuffer.poll();
	}
	
	/** Checks if there are objects in the buffer
	 * @return true if there are objects, false otherwise
	 */
	public boolean available() {
		return !inputBuffer.isEmpty();
	}

	/** Stops the SocketReader
	 * 
	 */
	public void stop() {
		running = false;
	}
	
}
