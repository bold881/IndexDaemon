package fulltextsearch.indexmanage;

import java.util.ArrayList;
import java.util.List;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.MultiThreadData;

public class IndexManage implements Runnable {
	
	private List<Thread> lstWorkers = null;

	public void run() {
		
		if(lstWorkers == null) {
			lstWorkers = new ArrayList<Thread>();
		}
		
		IndexManageWorker indexManageWorker = new IndexManageWorker();
		
		for(int i=0; i<AppConfig.getIndexmanageamout(); i++) {
			Thread thread = new Thread(indexManageWorker);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
			lstWorkers.add(thread);
		}
		
		while(true) {
			List<Thread> badThreads = new ArrayList<Thread>();
			for(Thread worker : lstWorkers) {
				if(!worker.isAlive()) {
					badThreads.add(worker);
					System.out.println(
							"IndexManage: " + worker.getName() + " stopped.");
				} else {
					System.out.println(
							"IndexManage: " + worker.getName() + " is running.");
				}
			}
			
			for(Thread badThread : badThreads) {
				lstWorkers.remove(badThread);
				System.out.println("IndexManage: " + badThread.getName() + " removed.");
				
				Thread workerNew = new Thread(indexManageWorker);
				workerNew.setPriority(Thread.MIN_PRIORITY);
				workerNew.start();
				lstWorkers.add(workerNew);
				System.out.println("IndexManage: " + workerNew.getName() + " added.");
			}
			
			System.out.println("Clean Queue Size: " + MultiThreadData.getItemQueue().size());
			
			
			try {
				Thread.sleep(AppConfig.getCheckerSleepDuration());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
