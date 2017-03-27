package fulltextsearch.appdaemon;

import java.util.ArrayList;
import java.util.List;

import org.quartz.SchedulerException;

import fulltextsearch.data.FtpAndSecret;
import fulltextsearch.databasemanage.DatabaseManage;
import fulltextsearch.getjobs.GetJobs;
import fulltextsearch.indexmanage.IndexManage;
import fulltextsearch.processdoc.ProcessDoc;

public class App 
{
	public final static String GETJOBS = "GetJobs";
	public final static String INDEXMANAGE = "IndexManage";
	public final static String DATABASEMANAGE = "DatabaseManage";
	public final static String PROCESSDOC = "ProcessDoc";
	public final static String FIRSTLEVELDAEMON = "FirstLevelDaemon";
	
    public static void main( String[] args ) throws SchedulerException
    {	
    	List<Runnable> lstRunners = new ArrayList<Runnable>();
    	Runnable getJobs = new GetJobs();
    	Runnable indexManage = new IndexManage();
    	Runnable databaseManage = new DatabaseManage();
    	Runnable processDoc = new ProcessDoc();
    	
    	lstRunners.add(getJobs);
    	lstRunners.add(indexManage);
    	lstRunners.add(databaseManage);
    	lstRunners.add(processDoc);
    	
    	int i = 0;
        List<Thread> runningThread = new ArrayList<Thread>();
    	for(Runnable lstRunner : lstRunners) {
    		String runnerName;
    		if(i==0) {
    			runnerName = GETJOBS;
    		} else if( i==1 ) {
    			runnerName = INDEXMANAGE;
    		} else if( i== 2) {
    			runnerName = DATABASEMANAGE;
    		} else {
    			runnerName = PROCESSDOC; 
    		}
    		i++;
    		
    		Thread thread = new Thread(lstRunner, runnerName);
    		thread.setPriority(Thread.NORM_PRIORITY);
            thread.start();
            runningThread.add(thread);
    	}
        
        ThreadDaemon threadDaemon = new ThreadDaemon();
        
        // level 1 children thread daemon thread
        Thread threadDaemonThread = new Thread(threadDaemon, FIRSTLEVELDAEMON); 
        threadDaemon.setRunningThread(runningThread);
        
        threadDaemonThread.setPriority(Thread.NORM_PRIORITY);
        threadDaemonThread.start();
		
        
        FtpAndSecret ftpClient = new FtpAndSecret();
        ftpClient.getRawFtpFile("11", "10");
        
        
        
        try {
			threadDaemonThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
       
        System.out.println("App over!!!");
    }
}
