package fulltextsearch.getjobs;

import java.util.List;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.dao.InterItemDAO;
import fulltextsearch.dao.InterItemDAOImpl;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;



public class GetJobsWorker implements Runnable {
	
	private InterItemDAO interItemDao = null;
	

	public void run() {
		if(interItemDao == null) {
			interItemDao = new InterItemDAOImpl();
		}
		while(true) {
			List<InterItem> lstItems = interItemDao.getLatestInterItem(AppConfig.getLastIndex());
			if(lstItems != null && !lstItems.isEmpty()) {
			
				AppConfig.setLastIndex(lstItems.get(lstItems.size()-1).getId());
				
				synchronized (MultiThreadData.getRawItemQueue()) {
					MultiThreadData.getRawItemQueue().addAll(lstItems);
				}
			
				System.out.println("Raw Queue Size: " + MultiThreadData.getRawItemQueue().size());
			
			}
			
			try {
				Thread.sleep(AppConfig.getWorkerSleepDuration());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
