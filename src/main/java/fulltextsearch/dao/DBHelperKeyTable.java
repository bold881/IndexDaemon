package fulltextsearch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import fulltextsearch.appdaemon.AppConfig;

public class DBHelperKeyTable extends DBHelper{
	
	public String getAESPrivateKey() {
		
		String ftpPrivateKey = AppConfig.getFtpPrivateKey();
		if(ftpPrivateKey == null) {
			String querySQL = "SELECT CKEYSTR FROM SYS_009 WHERE ALGORITHM = 'AES'";
			ResultSet rs = doQuery(querySQL);
			
			try {
				rs.next();
				ftpPrivateKey = rs.getString(1);
				CloseConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ftpPrivateKey;
	} 
}
