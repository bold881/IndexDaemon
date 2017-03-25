package testquartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MyJob implements Job {
	
	public MyJob() {
		
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Hello MyJob is executing.");
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
