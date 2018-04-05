package com.phuongdtran.investment;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phuongdtran.util.ConnectionManager;
import com.phuongdtran.util.StatementAndResultSet;

public class InvestmentDao extends StatementAndResultSet {

	private Connection conn = null;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public InvestmentDao() throws SQLException{
		if(conn == null){
			conn = ConnectionManager.getInstance().getConnection();
			if (conn == null){
				throw new SQLException("Could not make a connection to database");
			}
		}
	}
	
	public Investment getInvestment( int userId){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			String sql = "SELECT usr.budget, usr.start_date, usr.end_date, inv.symbol FROM UserInfo AS usr "
					+ "INNER JOIN UserInvestment AS inv "
					+ "WHERE usr.user_id = inv.user_id AND usr.user_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			rs = pstmt.executeQuery(); 
			long budget=0;
			String startDate = "";
			String endDate ="";
			Set<String> symbols = new HashSet<String>();
			while(rs.next()) {
				budget = rs.getLong("budget");
				startDate = rs.getString("start_date");
				endDate = rs.getString("end_date");
				symbols.add(rs.getString("symbol"));
			}
			Investment investment = new Investment(budget, startDate, endDate, symbols);
			return investment;
		}catch (SQLException ex) {
			logger.error("getInvestment() failed. " + ex.getMessage());
		}finally {
			release(pstmt, rs);
		}	
		return null;
	}
	
	public void update( int userId, long budget, String startDate, String endDate){
		PreparedStatement pstmt = null;
		String sql = "UPDATE UserInfo SET budget=?, startDate=?,endDate=? WHERE user_id=?";
		try{
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, budget);
			pstmt.setString(2, startDate);
			pstmt.setString(3, endDate);
			pstmt.setInt(4, userId);
			
			pstmt.executeUpdate();
		}catch(SQLException ex) {
			logger.error("update() failed" + ex.getMessage());
		}finally{
			release(pstmt);
		}
	}
	
	public void addSymbol( int userId, String symbol){
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO UserInvestment(user_id,symbol) VALUES(?,?)";
		try{
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			pstmt.setString(2, symbol);
			pstmt.executeUpdate();
		}catch(SQLException ex){
			logger.error("addSymbol() failed." + ex.getMessage());
		}finally{
			release(pstmt);
		}
	}

	public void close(){
		ConnectionManager.getInstance().releaseConnection(conn);		
	}
}
