package fulltextsearch.appdaemon;

import java.util.List;

import fulltextsearch.databasemanage.DatabaseManage;
import fulltextsearch.getjobs.GetJobs;
import fulltextsearch.indexmanage.IndexManage;
import fulltextsearch.processdoc.ProcessDoc;

public class ThreadDaemon implements Runnable {

	List<Thread> lstThread;
	
	public List<Thread> getRunningThread() {
		return lstThread;
	}

	public void setRunningThread(List<Thread> runningThread2) {
		this.lstThread = runningThread2;
	}
	
	public void run() {
		if(lstThread.isEmpty()) {
			return;
		}
		
		while(true) {
			for(Thread runnable: lstThread) {
				if(!runnable.isAlive()) {
					System.out.println(runnable.getName() + " stopped ...");
					Runnable rerun;
					String threadName;
					if(runnable.getName() == App.GETJOBS) {
						rerun = new GetJobs();
						threadName = App.GETJOBS;
					} else if(runnable.getName() == App.INDEXMANAGE) {
						rerun = new IndexManage();
						threadName = App.INDEXMANAGE;
					} else if(runnable.getName() == App.DATABASEMANAGE) {
						rerun = new DatabaseManage();
						threadName = App.DATABASEMANAGE;
					} else {
						rerun = new ProcessDoc();
						threadName = App.PROCESSDOC;
					}
					
					
					Thread rerunThread = new Thread(rerun);
					rerunThread.setName(threadName);
					rerunThread.setPriority(Thread.NORM_PRIORITY);
					
					try {
						rerunThread.start();
						lstThread.remove(lstThread.indexOf(runnable));
						lstThread.add(rerunThread);
						//throw new InterruptedException();
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}	
				} else {
					System.out.println(runnable.getName() + " is running ...");
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
