package fulltextsearch.processdoc;

import java.util.List;

import fulltextsearch.appdaemon.AppConfig;
import fulltextsearch.data.FtpAndSecret;
import fulltextsearch.data.MultiThreadData;
import fulltextsearch.pojos.InterItem;
import fulltextsearch.pojos.ObjectItem;

public class ProcessDocWorker implements Runnable {
	
	private Thread thread;
	
	private boolean hasTasks;
	
	private static final String ECR = "ECR";
	
	public ProcessDocWorker() {
		
		hasTasks = true;
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	private void doTikaParse(byte[] originFile,
			InterItem interItem) {
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
	

	public void run() {
		while (true) {
			InterItem interItem = MultiThreadData.dequeueRawItem();
			if(interItem !=null) {
				if(AppConfig.isStartDocAttachProcess()) {
					
					// normal doc
					String docFormat = interItem.getDocformat();
					if(docFormat != null && !docFormat.isEmpty()) {
						if(AppConfig.getLstValidDocFormat().contains(docFormat)) {
							FtpAndSecret ftpHelper = new FtpAndSecret();
							// using Tika extract file content locally
							byte[] originFile = ftpHelper.getFtpFile(interItem);
							doTikaParse(originFile, interItem);
						}
					}
					
					// ECR
					if(interItem.getItemType() == ECR) {
						String objectInfo = interItem.getObject();
						List<ObjectItem> objects = ObjectInfo.getObjects(objectInfo);
						if(objects!=null && !objects.isEmpty()) {
							FtpAndSecret ftpHelper = new FtpAndSecret();
							for(ObjectItem item : objects) {	
								byte[] originFile = ftpHelper.getFtpFile(item);
								doTikaParse(originFile, interItem);
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
				this.thread.suspend();
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
		this.thread.resume();
	}
}
