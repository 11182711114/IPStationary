package w01.u1_2;

public class ThreadTester {
	
	public void start() throws InterruptedException {
		T1 t1 = new T1();
		t1.start();
		Thread.sleep(5000);
		
		T2 t2 = new T2();
		Thread t2Thread = new Thread(t2);
		t2Thread.start();
		Thread.sleep(5000);
		
		t1.running = false;
		Thread.sleep(5000);
		
		t2.running = false;		
	}
	
	public static void main() {
		ThreadTester main = new ThreadTester();
		try {
			main.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

class T1 extends Thread {
	boolean running = false;
	
	@Override
	public void run() {
		running = true;
		while(running) {
			System.out.println("Tråd 1");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}

class T2 implements Runnable {
	boolean running = false;
	
	@Override
	public void run() {
		running = true;
		while(running) {
			System.out.println("Tråd 1");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}