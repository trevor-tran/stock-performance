package app.util;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {

	private static ConnectionManager instance = null;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ConnectionManager(){		
	}

	public static ConnectionManager getInstance() {
		if(instance == null){
			synchronized (ConnectionManager.class) {
				if(instance == null)
					instance = new ConnectionManager();
			}
		}
		return instance;
	}

	public Connection getConnection(){
		try{
			return  DatabaseConnection.getConnection();
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return null;
	}

	public void releaseConnection(Connection conn){
		try{
			if (conn != null){
				conn.close();
			}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}finally{
			DbUtils.closeQuietly(conn);
		}
	}

}
