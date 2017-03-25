package fulltextsearch.processdoc;

import java.util.ArrayList;
import java.util.List;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.MultiThreadData;

public class ProcessDoc implements Runnable {
	
	private List<Thread> lstWorkers = null;

	public void run() {
		if(lstWorkers == null) {
			lstWorkers = new ArrayList<Thread>();
		}
		
		ProcessDocWorker docWorker = new ProcessDocWorker();
		
		for(int i=0; i<AppConfig.getDocProcessorAmount(); i++) {
			Thread thread = new Thread(docWorker);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
			lstWorkers.add(thread);
		}
		
		while(true) {
			
			List<Thread> badThreads = new ArrayList<Thread>();
			for(Thread worker : lstWorkers) {
				if(!worker.isAlive()) {
					badThreads.add(worker);
					System.out.println("ProcessDoc: " + worker.getName() + " stopped.");
				} else {
					System.out.println("ProcessDoc: " + worker.getName() + " is running.");
				}
			}
			
			// remove bad thread from list
			for(Thread badThread : badThreads) {
				lstWorkers.remove(badThread);
				System.out.println("ProcessDoc: " + badThread.getName() + " removed.");
				
				Thread workerNew = new Thread(docWorker);
				workerNew.setPriority(Thread.MIN_PRIORITY);
				workerNew.start();
				lstWorkers.add(workerNew);
				System.out.println("ProcessDoc: " + workerNew.getName() + " added.");
			}
			
			
			System.out.println("Clean Queue Size: " + MultiThreadData.getItemQueue().size());
			System.out.println("Raw Queue Size: " + MultiThreadData.getRawItemQueue().size());
			// need to check whether add or remove worker from list
			
			
			try {
				Thread.sleep(AppConfig.getCheckerSleepDuration());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
