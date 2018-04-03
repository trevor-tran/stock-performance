package app.investment;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.ConnectionManager;

public class InvestmentDao implements AutoCloseable {

	private Connection conn = null;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public InvestmentDao() {
		if(conn == null){
			conn = ConnectionManager.getInstance().getConnection();
		}
	}
	public Investment getInvestment( int userId){
	try{
		String sql = "SELECT usr.budget, usr.start_date, usr.end_date, inv.symbol FROM UserInfo AS usr "
					+ "INNER JOIN UserInvestment AS inv"
					+ "WHERE usr.user_id = inv.user_id AND usr.user_id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, userId);
		ResultSet rs = pstmt.executeQuery(); 
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
		logger.error("Database exception: " + ex.getMessage());
	}	
	return null;
}

	@Override
	public void close() throws Exception {
		ConnectionManager.getInstance().releaseConnection(conn);		
	}
}
