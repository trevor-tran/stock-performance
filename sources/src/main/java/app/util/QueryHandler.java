package app.util;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
public class QueryHandler {

		private Connection connection;
		@Getter private Statement statement;
		@Getter private ResultSet resultSet;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public QueryHandler(){
		try{
			connection = DatabaseConnection.getConnection();
			statement = connection.createStatement();
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}
	
	public void executeUpdate(String sql){
		try{
			statement.executeUpdate(sql);
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}
	
	public void executeQuery(String sql){
		try{
			resultSet = statement.executeQuery(sql);
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}	
	}

	/**
	 * close <i>Connection,Statement, ResultSet</i>
	 * @throws SQLException
	 */
	public void close(){
		try{
			if(resultSet != null){
				resultSet.close();
			}
			if( statement != null){ 
				statement.close();
			}
			if (connection != null){
				connection.close();
			}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}finally{
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(resultSet);
		}
	}

}
