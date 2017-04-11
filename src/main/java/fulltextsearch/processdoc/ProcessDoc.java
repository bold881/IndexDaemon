package fulltextsearch.processdoc;

import java.util.ArrayList;
import java.util.List;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.MultiThreadData;

public class ProcessDoc implements Runnable {
	
	private List<ProcessDocWorker> lstWorkers = null;

	public void run() {
		if(lstWorkers == null) {
			lstWorkers = new ArrayList<ProcessDocWorker>();
		}
		
		for(int i=0; i<AppConfig.getDocProcessorAmount(); i++) {
			ProcessDocWorker docWorker = new ProcessDocWorker();
			lstWorkers.add(docWorker);
		}
		
		while(true) {
			
			List<ProcessDocWorker> badThreads = new ArrayList<ProcessDocWorker>();
			for(ProcessDocWorker worker : lstWorkers) {
				if(!worker.isAlive()) {
					badThreads.add(worker);
					System.out.println("ProcessDoc: " + worker.getName() + " stopped.");
				} else {
					System.out.println("ProcessDoc: " + worker.getName() + " is running.");
				}
			}
			
			// remove bad thread from list
			for(ProcessDocWorker badThread : badThreads) {
				lstWorkers.remove(badThread);
				System.out.println("ProcessDoc: " + badThread.getName() + " removed.");
				
				ProcessDocWorker workerNew = new ProcessDocWorker();
				lstWorkers.add(workerNew);
				System.out.println("ProcessDoc: " + workerNew.getName() + " added.");
			}
			
			// wake up thread on condintion of new tasks
			if(MultiThreadData.getRawItemQueue().size() > 0) {
				for(ProcessDocWorker worker : lstWorkers) {
					if(!worker.isHasTasks()) {
						worker.workerResume();
					}
				}
			}
			
			
			System.out.println("Clean Queue Size: " + MultiThreadData.getItemQueue().size());
			System.out.println("Raw Queue Size: " + MultiThreadData.getRawItemQueue().size());
					
			try {
				Thread.sleep(AppConfig.getCheckerSleepDuration());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
