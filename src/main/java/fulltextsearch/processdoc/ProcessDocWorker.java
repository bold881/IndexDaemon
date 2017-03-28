package fulltextsearch.processdoc;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.FtpAndSecret;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;

public class ProcessDocWorker implements Runnable {

	public void run() {
		while (true) {
			InterItem interItem = MultiThreadData.dequeueRawItem();
			if(interItem !=null) {
				String docFormat = interItem.getDocformat();
				if(docFormat != null && !docFormat.isEmpty()) {
					if(AppConfig.getLstValidDocFormat().contains(docFormat)) {
						FtpAndSecret ftpHelper = new FtpAndSecret();
						interItem.setObjectInfo(
								ftpHelper.getFtpFileEncodeBase64(interItem));
						System.out.print("File processed: " + interItem.getObjectInfo());
					}
				}
				
				MultiThreadData.addItem(interItem);
				
			} else {
				try {
					Thread.sleep(AppConfig.getWorkerSleepDuration());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
