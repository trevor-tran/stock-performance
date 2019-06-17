package com.phuongdtran.util;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Release {
	
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/**
	 * release Statement or any sub-interface such as PreparedStatment, Callable Statement
	 * @param stmt
	 */
	public static void release(Statement stmt) {
		try{
			if( stmt != null){ 
				stmt.close();
			}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}finally{	
			DbUtils.closeQuietly(stmt);
		}
	}

	/**
	 * release Statement or any sub-interface such as PreparedStatment, Callable Statement.
	 * also release ResultSet
	 * @param stmt
	 * @param rs
	 */
	public static void release(Statement stmt, ResultSet rs){
		try{
			release(stmt);
			if (rs != null){
				rs.close();
			}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}finally{	
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(rs);
		}
	}
}
