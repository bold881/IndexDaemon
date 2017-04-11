package fulltextsearch.processdoc;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.FtpAndSecret;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;

public class ProcessDocWorker implements Runnable {
	
	private Thread thread;
	
	private boolean hasTasks;
	
	public ProcessDocWorker() {
		
		hasTasks = true;
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public void run() {
		while (true) {
			InterItem interItem = MultiThreadData.dequeueRawItem();
			if(interItem !=null) {
				String docFormat = interItem.getDocformat();
				if(docFormat != null 
						&& !docFormat.isEmpty()
						&& AppConfig.isStartDocAttachProcess()) {
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
				// suspend when dequeued null
				hasTasks = false;
				synchronized (this) {
					while(!hasTasks) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public boolean isAlive() {
		if(this.thread == null) {
			return false;
		}
		return this.thread.isAlive();
	}
	
	public String getName() {
		return this.thread.getName();
	}

	public boolean isHasTasks() {
		return hasTasks;
	}

	public void setHasTasks(boolean hasTasks) {
		this.hasTasks = hasTasks;
	}
	
	public void workerResume() {
		this.hasTasks = true;
		this.notify();
	}
}
