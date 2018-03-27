package app.util;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StatementAndResultSet {
	
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public void release(Statement stmt) {
		try{
			if( stmt != null){ 
				stmt.close();
			}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}finally{	
			DbUtils.closeQuietly(stmt);
		}
	}

	public void release(Statement stmt, ResultSet rs){
		try{
			if( stmt != null){ 
				stmt.close();
			}
			if (rs != null){
				rs.close();
			}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}finally{	
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(rs);
		}
	}

}
