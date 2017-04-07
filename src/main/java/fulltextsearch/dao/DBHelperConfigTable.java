package fulltextsearch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import fulltextsearch.pojos.ConfigCollection;

public class DBHelperConfigTable extends DBHelper {
	
	private static final String FULLTEXT_SEARCH = "FULLTEXT_SEARCH";
	private static final String FULLTEXT_SEARCH_PARSE_OBJECT =
			"FULLTEXT_SEARCH_PARSE_OBJECT";
	
	
	public ConfigCollection getFromConfigTable() {
		// BOM_048
		String querySQL = "SELECT propertyfield, value FROM BOM_048 "
				+ "WHERE propertyfield = 'FULLTEXT_SEARCH' or propertyfield"
				+ " = 'FULLTEXT_SEARCH_PARSE_OBJECT'";
		ResultSet rs = doQuery(querySQL);
		
		ConfigCollection config = null;
		try {
			config = new ConfigCollection();
			while(rs.next()) {
				if(rs.getString(1).equalsIgnoreCase(FULLTEXT_SEARCH)) {
					if(rs.getString(2).equalsIgnoreCase("TRUE")) {
						config.setStartFulltext(true);
					} else {
						config.setStartFulltext(false);
					}
				}
				
				if(rs.getString(1).equalsIgnoreCase(FULLTEXT_SEARCH_PARSE_OBJECT)) {
					if(rs.getString(2).equalsIgnoreCase("TRUE")) {
						config.setStartDocProcess(true);
					} else {
						config.setStartDocProcess(false);
					}
				}
			}
			CloseConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return config;
	} 
}
