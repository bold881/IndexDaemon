package fulltextsearch.processdoc;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;

public class ProcessDocWorker implements Runnable {

	public void run() {
		while (true) {
			InterItem interItem = MultiThreadData.dequeueRawItem();
			if(interItem !=null) {
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MultiThreadData.addItem(interItem);
				
			} else {
				try {
					Thread.sleep(AppConfig.getWorkerSleepDuration());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
