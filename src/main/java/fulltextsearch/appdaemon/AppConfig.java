package fulltextsearch.appdaemon;

import java.io.FileInputStream;
import java.io.IOException;
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

import fulltextsearch.dao.DBHelperConfigTable;
import fulltextsearch.dao.DBHelperKeyTable;
import fulltextsearch.pojos.ConfigCollection;
import fulltextsearch.pojos.InterItem;
import fulltextsearch.pojos.KeyTable;

public class AppConfig {
	
	private static final String propertiesFile = "src/main/resources/plmfulltextservice.properties";
	
	private static String connectionUrl = "jdbc:sqlserver://10.115.0.161:1433";
	
	private static String userName = "sa";
	
	private static String password = "1";
	
	private static String databaseName = "2017";
	
	// query database once return max count 
	private static int maxResultCount = 100000;
	
	// last got item id, should stored in configuration file
	private static Long lastIndex = 102L;
	
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
	private static String ftpUsername = "admin";
	
	// FTP client user password
	private static String ftpPassword = "admin";
	
	// FTP server address
	private static String ftpAddress = "10.115.0.161";
	
	// FTP server port
	private static int ftpAddressPort = 21;
	
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
			userName = properties.getProperty("userName");
			password = properties.getProperty("password");
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
			ftpUsername = properties.getProperty("ftpUsername");
			ftpPassword = properties.getProperty("ftpPassword");
			ftpAddress = properties.getProperty("ftpAddress");
			ftpAddressPort = Integer.parseInt(properties.getProperty("ftpAddressPort"));
			ftpDocumentsDir = properties.getProperty("ftpDocumentsDir");
			esClusterName = properties.getProperty("esClusterName");
			esServerIPAddr = properties.getProperty("esServerIPAddr");
			esServerClientPort = 
					Integer.parseInt(properties.getProperty("esServerClientPort"));
			esIndexName = properties.getProperty("esIndexName");
			indexBulkSize = 
					Integer.parseInt(properties.getProperty("indexBulkSize"));
			
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

}
