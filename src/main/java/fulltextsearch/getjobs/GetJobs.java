package fulltextsearch.getjobs;

import java.util.ArrayList;
import java.util.List;

import fulltextsearch.appdaemon.AppConfig;

public class GetJobs implements Runnable {
	
	// get jobs thread does not support multithreading currently
	private final int threadMount = 1;
	
	private List<Thread> lstWorkerThreads = null;
	
	public void run() {
		
		if(lstWorkerThreads == null) {
			lstWorkerThreads = new ArrayList<Thread>();
		}
		
		// get item from table is not suitable for multithreading
		GetJobsWorker getJobsWorker = new GetJobsWorker();
		for (int i = 0; i < threadMount; i++) {
			Thread worker = new Thread(getJobsWorker);
			worker.setPriority(Thread.MIN_PRIORITY);
			worker.start();
			lstWorkerThreads.add(worker);
		}
		
		
		while(true) {
			for(Thread worker : lstWorkerThreads) {
				if(!worker.isAlive()) {
					Thread workerNew = new Thread(getJobsWorker);
					workerNew.setPriority(Thread.MIN_PRIORITY);
					workerNew.start();
					lstWorkerThreads.add(workerNew);
					lstWorkerThreads.remove(worker);
					System.out.println("ProcessDoc: " + worker.getName() + " stopped and restarted.");
					break;
				} else {
					System.out.println("ProcessDoc: " + worker.getName() + " is running.");
				}
			}
			
			try {
				Thread.sleep(AppConfig.getCheckerSleepDuration());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
