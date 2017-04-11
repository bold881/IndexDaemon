package fulltextsearch.databasemanage;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import fulltextsearch.appdaemon.AppConfig;



public class DatabaseManage implements Runnable {
	
	private static final String GROUPTABLEMANAGE = "TableManage";
	
	private static final String DELETEANDIDCHECK = "DeleteAndIDCheck";
	
	private static final String TRIGGERTABLEMANAGE = "TriggerTableManage";
	
	public void addTableManageJob() {
		
		Scheduler scheduler = AppConfig.getScheduler();
		
		JobDetail jobDetail = newJob(TableManageJob.class)
				.withIdentity(DELETEANDIDCHECK, GROUPTABLEMANAGE)
				.build();
		
		CronTrigger trigger = newTrigger()
				.withIdentity(TRIGGERTABLEMANAGE, GROUPTABLEMANAGE)
				.withSchedule(cronSchedule("0 30 2 * * ?"))
				.forJob(jobDetail)
				.build();
		
		try {
			scheduler.scheduleJob(jobDetail ,trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		
		addTableManageJob();
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
