package fulltextsearch.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fulltextsearch.appdaemon.AppConfig;

public class DBHelper {
	
	Connection connection = null;
	
	private boolean GetConnection() {
		if(connection != null) {
			return true;
		}
		
		String url = AppConfig.getConnectionUrl() + ";databaseName=" + AppConfig.getDatabaseName();
		try {
			connection = DriverManager.getConnection(url,
					AppConfig.getUserName(),
					AppConfig.getPassword());
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	protected  void CloseConnection() {
		try {
			if(connection != null && !connection.isClosed()) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected ResultSet doQuery(String query) {
		
		ResultSet resultSet = null;
		
		if(GetConnection()) {
			
			Statement statement;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//CloseConnection();
		}
		
		return resultSet;
	}
	
	protected void doUpdate(String query) {
		
		if(GetConnection()) {
			Statement statement;
			try {
				statement = connection.createStatement();
				statement.executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			CloseConnection();
		}
	}
}
