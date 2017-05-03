package fulltextsearch.getjobs;

import java.util.ArrayList;
import fulltextsearch.appdaemon.AppConfig;

public class GetJobs implements Runnable {
	
	// get jobs thread does not support multithreading currently
	private final int threadMount = 1;
	
	public void run() {
		
		if(AppConfig.getJobsWorkerThreads == null) {
			AppConfig.getJobsWorkerThreads = new ArrayList<GetJobsWorker>();
		}
		
		// get item from table is not suitable for multithreading
		
		for (int i = 0; i < threadMount; i++) {
			GetJobsWorker getJobsWorker = new GetJobsWorker();
			AppConfig.getJobsWorkerThreads.add(getJobsWorker);
		}
		
		
		while(true) {
			for(GetJobsWorker worker : AppConfig.getJobsWorkerThreads) {
				if(!worker.isAlive()) {
					GetJobsWorker workerNew = new GetJobsWorker();
					AppConfig.getJobsWorkerThreads.add(workerNew);
					AppConfig.getJobsWorkerThreads.remove(worker);
					System.out.println("GetJobs: " + worker.getName() + " stopped and restarted.");
					break;
				} else {
					//System.out.println("GetJobs: " + worker.getName() + " is running.");
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
