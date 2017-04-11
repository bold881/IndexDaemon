package fulltextsearch.appdaemon;

public class RunnableThread implements Runnable {
	
	private Thread thread;
	
	private boolean hasTasks;
	
	public RunnableThread() {
		hasTasks = true;
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public void run() {
		
	}

	public boolean isAlive() {
		if(this.thread == null) {
			return false;
		}
		return this.thread.isAlive();
	}
	
	public String getName() {
		return this.thread.getName();
	}

	public boolean isHasTasks() {
		return hasTasks;
	}

	public void setHasTasks(boolean hasTasks) {
		this.hasTasks = hasTasks;
	}
	
	public void workerResume() {
		this.hasTasks = true;
		this.notify();
	}
}
