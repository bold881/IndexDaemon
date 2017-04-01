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
						// using Elasticsearch to extract file content need string Base64 encoded
						// String encodedObjectInfo = ftpHelper.getFtpFileEncodeBase64(interItem);
						// using Tika extract file content locally
						byte[] originFile = ftpHelper.getFtpFile(interItem);
						if(originFile!=null) {
							TikaParser parser = new TikaParser();
							String content = parser.autoParse(originFile);
							if(content != null) {
								interItem.setObjectInfo(content);
								System.out.println("File processed--- ID:" 
										+ interItem.getIdPdm() + " Version:" +interItem.getVerPdm());
							}
						}
					}
				}
				
				MultiThreadData.addItem(interItem);
				try {
					Thread.sleep(AppConfig.getWorkerminisleepduration());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
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
