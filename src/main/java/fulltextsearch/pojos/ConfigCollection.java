package fulltextsearch.pojos;

public class ConfigCollection {
	
	// Allow Start Index Process To Start
	private boolean startFulltext;
	
	// Allow Doc Process to Start
	private boolean startDocProcess;

	public boolean isStartFulltext() {
		return startFulltext;
	}

	public void setStartFulltext(boolean startFulltext) {
		this.startFulltext = startFulltext;
	}

	public boolean isStartDocProcess() {
		return startDocProcess;
	}

	public void setStartDocProcess(boolean startDocProcess) {
		this.startDocProcess = startDocProcess;
	}
}
