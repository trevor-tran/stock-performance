package app.util;

import java.lang.invoke.MethodHandles;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
public class QueryExecutionHandler implements AutoCloseable {
		private CallableStatement cstmt;
		private PreparedStatement pstmt;
		private Statement stmt;
		private Connection connection;
		
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public QueryExecutionHandler(){
		try{
			connection = DatabaseConnection.getConnection();
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}
	
	public PreparedStatement prepareStatement(String sql){
		try{
			pstmt = connection.prepareStatement(sql);
			return pstmt;
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return null;
	}
	
	public CallableStatement prepareCall(String sql){
		try{
			cstmt = connection.prepareCall(sql);
			return cstmt;
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return null;
	}
	
	public ResultSet executeQueryViaStatement(String sql){
		try{
			stmt = connection.createStatement();
			return stmt.executeQuery(sql);
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return null;
	}
	
	public void executeUpdateViaStatement(String sql){
		try{
			stmt = connection.createStatement();
			 stmt.executeUpdate(sql);
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}

	@Override
	public void close(){
		try{
			if( stmt != null){ 
				stmt.close();
			}
			if( pstmt != null){ 
				pstmt.close();
			}
			if( cstmt != null){ 
				cstmt.close();
			}
			if (connection != null){
				connection.close();
			}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}finally{
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(pstmt);
			DbUtils.closeQuietly(cstmt);
		}
	}

}
