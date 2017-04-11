package fulltextsearch.databasemanage;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.dao.DBHelperInterItem;
import fulltextsearch.dao.InterItemDAO;
import fulltextsearch.dao.InterItemDAOImpl;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;

public class TableManageJob implements Job {
	
	private InterItemDAO interItemDao = null;
	
	private DBHelperInterItem dbHelperInterItem = null;
	
	public TableManageJob() {
		
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if(interItemDao == null) {
			interItemDao = new InterItemDAOImpl();
		}
		
		int runningTimes = 0;
		while(runningTimes < 3) {
			if(MultiThreadData.getRawItemQueue().isEmpty()
					&& MultiThreadData.getItemQueue().isEmpty()) {
				// suspend all get jobs threads
				AppConfig.suspendGetJobsThreads();
				
				// all work done, then delete
				System.out.println(interItemDao.deleteAllItems() + " item deleted");
				
				// check identity size
				if(AppConfig.dbIDCheck()) {
					List<InterItem> lstItems = interItemDao.getLatestInterItem(0L);
					if(lstItems == null || lstItems.isEmpty()) {
						if(dbHelperInterItem == null) {
							dbHelperInterItem = new DBHelperInterItem();
						}
						dbHelperInterItem.resetInterIdentity();
						AppConfig.setLastIndex(0L);
						AppConfig.setLastIndexed(0L);
						AppConfig.saveLastIndexedtoFile();
					}
				}
				
				// resume all get jobs threads
				AppConfig.resumeGetJobsThreads();
				break;
			} else {
				try {
					System.out.println("wait for queue be clean");
					Thread.sleep(600000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
