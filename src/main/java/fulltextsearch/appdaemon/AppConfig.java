package fulltextsearch.appdaemon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import fulltextsearch.dao.DBHelperConfigTable;
import fulltextsearch.dao.DBHelperKeyTable;
import fulltextsearch.data.AESProgram;
import fulltextsearch.getjobs.GetJobsWorker;
import fulltextsearch.pojos.ConfigCollection;
import fulltextsearch.pojos.InterItem;
import fulltextsearch.pojos.KeyTable;

public class AppConfig {
	
	private static String propertiesFile = "src/main/resources/plmfulltextservice.properties";
	
	private static String connectionUrl = "jdbc:sqlserver://10.115.0.161:1433";
	
	private static String userName = "sa";
	
	private static String password = "1";
	
	private static String databaseName = "2017";
	
	// query database once return max count 
	private static int maxResultCount = 100000;
	
	// last got item id, should stored in configuration file
	private static Long lastIndex = 102L;
	
	// last processed index ID
	private static Long lastIndexed = -1L;
	
	// worker thread sleep time
	private static Long workerSleepDuration = 10000L;
	
	// worker thread minimal sleep time
	private static Long workerMiniSleepDuration = 0L;

	// checker thread sleep time
	private static Long checkerSleepDuration = 30000L; 
	
	// process doc info initial amount
	private static int docProcessorAmount = 30;
	
	// index manage worker initial amount
	private static int indexManageAmout = 1;
	
	// FTP client user name
	public static String ftpUsername = "admin";
	
	// FTP client user password
	public static String ftpPassword = "admin";
	
	// FTP server address
	public static String ftpAddress = "10.115.0.161";
	
	// FTP server port
	public static int ftpAddressPort = 21;
	
	// FTP documents directory path
	private static String ftpDocumentsDir = "/DE_DOCUMENTS/";
	
	// elasticsearch cluster name
	private static String esClusterName = "yonyouplm";
	
	// elasticsearch server ip address
	private static String esServerIPAddr = "0.0.0.0";
	
	// elasticsearch 
	private static int esServerClientPort = 9300;
	
	// elasticsearch index name
	private static String esIndexName = "plmfulltext";
	
	// Index bulk size
	private static int indexBulkSize = 100;
	
	// max db table identity
	private static Long maxDbId = 1000000000L;
	
	// Start full text process
	private static boolean startFullText = true;
	
	// Start doc attachment process 
	private static boolean startDocAttachProcess = true;
	
	private static SessionFactory sessionFactory;
	
	// Encrypt and decrypt key for FTP file
	private volatile static String ftpPrivateKey = null;
	
	// supported doc format
	private static List<String> lstValidDocFormat = null;
	
	// elasticsearch client 
	private static TransportClient esClient = null;
	
	// Quartz scheduler
	private static Scheduler scheduler = null;
	
	// get jobs worker
	public static List<GetJobsWorker> getJobsWorkerThreads = null;
	
	
	
	
	public static void setPropertiesFile(String propertiesFile) {
		AppConfig.propertiesFile = propertiesFile;
	}

	public static Long getMaxDbId() {
		return maxDbId;
	}

	public static Scheduler getScheduler() {
		
		if(scheduler == null) {
			SchedulerFactory sFactory = new StdSchedulerFactory();
			try {
				scheduler = sFactory.getScheduler();
				scheduler.start();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		return scheduler;
	}

	public static Long getLastIndexed() {
		return lastIndexed;
	}

	public static void setLastIndexed(Long lastIndexed) {
		AppConfig.lastIndexed = lastIndexed;
	}

	public static boolean isStartFullText() {
		return startFullText;
	}

	public static boolean isStartDocAttachProcess() {
		return startDocAttachProcess;
	}

	public static int getIndexbulksize() {
		return indexBulkSize;
	}

	public static synchronized int getEsserverclientport() {
		return esServerClientPort;
	}

	public static synchronized String getEsindexname() {
		return esIndexName;
	}

	public static synchronized String getEsclustername() {
		return esClusterName;
	}

	public static synchronized String getEsserveripaddr() {
		return esServerIPAddr;
	}

	public static synchronized TransportClient getEsClient() {
		if(esClient == null) {
			esClientInit();
		}
		return esClient;
	}

	public static int getIndexmanageamout() {
		return indexManageAmout;
	}

	public static Long getWorkerminisleepduration() {
		return workerMiniSleepDuration;
	}

	public static List<String> getLstValidDocFormat() {
		return lstValidDocFormat;
	}

	public static String getConnectionUrl() {
		return connectionUrl;
	}

	public static String getUserName() {
		return userName;
	}

	public static String getPassword() {
		return password;
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public synchronized static String getFtpPrivateKey() {	
		return ftpPrivateKey;
	}

	public static String getFtpDocumentsDir() {
		return ftpDocumentsDir;
	}

	public static String getFtpAddress() {
		return ftpAddress;
	}

	public static int getFtpAddressPort() {
		return ftpAddressPort;
	}

	public static String getFtpusername() {
		return ftpUsername;
	}

	public static String getFtppassword() {
		return ftpPassword;
	}

	public static int getDocProcessorAmount() {
		return docProcessorAmount;
	}

	public static Long getCheckerSleepDuration() {
		return checkerSleepDuration;
	}

	public static Long getWorkerSleepDuration() {
		return workerSleepDuration;
	}

	public static Long getLastIndex() {
		return lastIndex;
	}

	public static void setLastIndex(Long lastIndex) {
		AppConfig.lastIndex = lastIndex;
	}

	public static int getMaxResultCount() {
		return maxResultCount;
	}
	
	public static SessionFactory getSessionFactory() {
		
		if(sessionFactory == null) {
			Configuration cfg = createHibernateConfiguration();
			cfg.addAnnotatedClass(InterItem.class);
			cfg.addAnnotatedClass(KeyTable.class);
			sessionFactory = cfg.buildSessionFactory();
		}
		
		return sessionFactory;
	}
	
	private static Configuration createHibernateConfiguration() {
		String url = connectionUrl + ";databaseName=" + databaseName;
        Configuration cfg = new Configuration()
				.setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
				.setProperty("hibernate.connection.url", url)
				.setProperty("hibernate.connection.username", userName)
				.setProperty("hibernate.connection.password", password)
				.setProperty("hibernate.connection.autocommit", "true")
				.setProperty("hibernate.show_sql", "false");

		// Tell Hibernate to use the 'SQL Server' dialect when dynamically
		// generating SQL queries
		//cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServer2008Dialect");

		// Tell Hibernate to show the generated T-SQL
		cfg.setProperty("hibernate.show_sql", "false");

		// This is ok during development, but not recommended in production
		// See: http://stackoverflow.com/questions/221379/hibernate-hbm2ddl-auto-update-in-production
		cfg.setProperty("hibernate.hbm2ddl.auto", "none");
		return cfg;
	}
	
	public static void init() {
		
		// ftp encrypt decrypt key
		if(ftpPrivateKey == null) {
			DBHelperKeyTable dbHelperKeytable = new DBHelperKeyTable();
			ftpPrivateKey = dbHelperKeytable.getAESPrivateKey();
		}
		
		DBHelperConfigTable config = new DBHelperConfigTable();
		ConfigCollection configCollection = config.getFromConfigTable();
		if( configCollection != null) {
			startFullText = configCollection.isStartFulltext();
			startDocAttachProcess = configCollection.isStartDocProcess(); 
		}
		
		// get FTP access 
		config.getFtpConfiguration();
		
		loadConfiguration();
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	private static void esClientInit() {
		if(esClient != null) {
			return;
		}
		Settings settings = Settings.builder()
				.put("cluster.name", getEsclustername()).build();
				
		try {
			esClient = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(
							InetAddress.getByName(getEsserveripaddr()), 
							getEsserverclientport()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void shutdown() {

		// quartz shutting down
		if(scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		
		
		// close ES client
		if(esClient != null) {
			esClient.close();
		}
	}
	
	public static void loadConfiguration() {
		Properties properties = new Properties();
		try {
			FileInputStream fileInputStream = new FileInputStream(propertiesFile);
			properties.load(fileInputStream);
			fileInputStream.close();
			
			connectionUrl = properties.getProperty("connectionUrl");
			connectionUrl = "jdbc:sqlserver://"+connectionUrl;
			
			userName = properties.getProperty("userName");
			
			password = properties.getProperty("password");
			password = AESProgram.strDecrypt(password);
			
			databaseName = properties.getProperty("databaseName");
			maxResultCount = 
					Integer.parseInt(properties.getProperty("maxResultCount"));
			lastIndex = Long.parseLong(properties.getProperty("lastIndex"));
			workerSleepDuration = 
					Long.parseLong(properties.getProperty("workerSleepDuration"));
			workerMiniSleepDuration = 
					Long.parseLong(properties.getProperty("workerMiniSleepDuration"));
			checkerSleepDuration = 
					Long.parseLong(properties.getProperty("checkerSleepDuration"));
			docProcessorAmount = 
					Integer.parseInt(properties.getProperty("docProcessorAmount"));
			indexManageAmout = 
					Integer.parseInt(properties.getProperty("indexManageAmout"));
			ftpDocumentsDir = properties.getProperty("ftpDocumentsDir");
			esClusterName = properties.getProperty("esClusterName");
			esServerIPAddr = properties.getProperty("esServerIPAddr");
			esServerClientPort = 
					Integer.parseInt(properties.getProperty("esServerClientPort"));
			esIndexName = properties.getProperty("esIndexName");
			indexBulkSize = 
					Integer.parseInt(properties.getProperty("indexBulkSize"));
			maxDbId = Long.parseLong(properties.getProperty("maxDbId"));
			
			String validDocFormat = properties.getProperty("docformat");
			if(lstValidDocFormat == null) {
				lstValidDocFormat = new ArrayList<String>();
			}
			if(validDocFormat!=null) {
				String[] formats = validDocFormat.split(",");
				for(String format: formats) {
					lstValidDocFormat.add(format);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// save configuration to properties file
	public static boolean saveConfigtoFile(String key, String value) {
		
		try {
			InputStream inputStream = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			OutputStream outputStream  = new FileOutputStream(propertiesFile);
			properties.setProperty(key, value);
			properties.store(outputStream, null);
			outputStream.close();
			System.out.println("Svaing configuration: " + key + " " + value);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// save last indexed to file
	public static boolean saveLastIndexedtoFile() {
		if(lastIndexed == -1) {
			return true;
		}
		return saveConfigtoFile("lastIndex", lastIndexed.toString());
	}

	// to check whether index exceded the maxDbId
	public static boolean dbIDCheck() {
		if(lastIndex > maxDbId || lastIndexed > maxDbId) {
			return true;
		}
		return false;
	}
	
	public static void suspendGetJobsThreads() {
		for(GetJobsWorker worker: getJobsWorkerThreads) {
			worker.setHasTasks(false);
		}
	}
	
	public static void resumeGetJobsThreads() {
		for(GetJobsWorker worker: getJobsWorkerThreads) {
			worker.workerResume();
		}
	}
	
}
