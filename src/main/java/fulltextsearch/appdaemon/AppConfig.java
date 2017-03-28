package fulltextsearch.appdaemon;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import fulltextsearch.dao.TableContentDAO;
import fulltextsearch.dao.TableContentDAOKeyTableImpl;
import fulltextsearch.pojos.InterItem;
import fulltextsearch.pojos.KeyTable;

public class AppConfig {
	
	private static String connectionUrl = "jdbc:sqlserver://10.115.0.161:1433";
	
	private static String userName = "sa";
	
	private static String password = "1";
	
	private static String databaseName = "2017";
	
	private static SessionFactory sessionFactory;
	
	// query database once return max count 
	private final static int maxResultCount = 100000;
	
	// last got item id, should stored in configuration file
	private static Long lastIndex = 102L;
	
	// worker thread sleep time
	private final static Long workerSleepDuration = 10000L;
	
	// checker thread sleep time
	private final static Long checkerSleepDuration = 30000L; 
	
	// process doc info initial amount
	private final static int docProcessorAmount = 100;
	
	// FTP client user name
	private final static String ftpUsername = "admin";
	
	// FTP client user password
	private final static String ftpPassword = "admin";
	
	// FTP server address
	private static String ftpAddress = "10.115.0.161";
	
	// FTP server port
	private static int ftpAddressPort = 21;
	
	// FTP documents directory path
	private static String ftpDocumentsDir = "/DE_DOCUMENTS/";
	
	// Encrypt and decrypt key for FTP file
	private static String ftpPrivateKey = null;
	
	
	public static String getFtpPrivateKey() {
		if(ftpPrivateKey == null) {
			TableContentDAO<KeyTable> keyTableDao = new TableContentDAOKeyTableImpl<KeyTable>();
			KeyTable keyTable = keyTableDao.getTableContent().get(0);
			if(keyTable != null) {
				ftpPrivateKey = keyTable.getKey();
			}
		}
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
	
	public static void loadConfiguration() {
		Properties properties = new Properties();  
	    String propFileName = "indexdaemon.properties";
	    InputStream inputStream = AppConfig.class.getClassLoader()
	    		.getResourceAsStream(propFileName);
	    
	    try {
	    	properties.load(inputStream);
			connectionUrl = properties.getProperty("connection.url");
			userName = properties.getProperty("username");
			password = properties.getProperty("password");
			databaseName = properties.getProperty("database.name");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
