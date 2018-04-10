package com.phuongdtran.stock;

import static com.phuongdtran.stock.StockController.symbols;

import java.lang.invoke.MethodHandles;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phuongdtran.util.ConnectionManager;
import com.phuongdtran.util.StatementAndResultSet;
import com.phuongdtran.util.ThreadPool;
public class StockDao extends StatementAndResultSet {


	private Connection conn = null;
	private static String mutualIpo = "";
	private String mutualDelisting = "";
	public static final int NOT_FOUND = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public StockDao() throws SQLException{
		if(conn == null){
			conn = ConnectionManager.getInstance().getConnection();
			if (conn == null){
				throw new SQLException("Could not make a connection to database");
			}
		}
	}

	/**
	 * It will do queries getting data from datatabase.<br>
	 * If requested data is unavailable, it will get unavailable data from Quandl.<br> 
	 * @param symbol
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Map<String,List<Stock>> getData(String symbol, String startDate, String endDate){
		try{
			updateStockCache(startDate, endDate);

			// first element is mutualIPO date, second one is mutualDelisting date
			List<String> ipoDelisting = getMutualIpoDelisting();
			//update mututalIpo and mutualDelisting
			if(ipoDelisting != null){
				if(!Objects.equals(mutualIpo, ipoDelisting.get(0))){
					mutualIpo = ipoDelisting.get(0);
				}
				if(!Objects.equals(mutualDelisting, ipoDelisting.get(1))){
					mutualDelisting = ipoDelisting.get(1);
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			//return data of multiple symbols
			//return data of all symbols from ipo date to delisting date if start date and end date are both out of range
			if(sdf.parse(startDate).before(sdf.parse(mutualIpo)) 
					&& sdf.parse(endDate).after(sdf.parse(mutualDelisting))) {
				return queryStockData(symbols, mutualIpo, mutualDelisting);

				//return data of all symbols from ipo date to end date if start date is before ipo date
			}else if (sdf.parse(startDate).before(sdf.parse(mutualIpo))) {
				return queryStockData(symbols, mutualIpo, endDate);

				//return data of all symbols from ipo date to end date if start date is before ipo date
			}else if(sdf.parse(endDate).after(sdf.parse(mutualDelisting))){
				return queryStockData(symbols, startDate, mutualDelisting);
			}

			//return data of one symbol
			if(getSymbolId(symbol) != NOT_FOUND){
				return queryStockData(new HashSet<>(Arrays.asList(symbol)), startDate, endDate);
			}
		}catch(ParseException ex){
			logger.error("queryStockData() failed: error on using SimpleDateFormat." + ex.getMessage());
		}
		return null;
	}
	
	/**
	 * @param startDate
	 * @param endDate
	 */
	private void updateStockCache(String startDate, String endDate) {
		try{
			List<CacheCallable> callables = new ArrayList<CacheCallable>();
			for(String s : symbols){
				callables.add(new CacheCallable(s, startDate, endDate, conn));
			}
			ThreadPool.getInstance().invokeAll(callables);
		}catch( InterruptedException ex) {
			logger.error("updateStockCache() failed." + ex.getMessage());
		}catch( NullPointerException ex){
			logger.error("updateStockCache() failed." + ex.getMessage());
		}
	}

	//data return structure: { "date": "symbol1":[price,split] , "symbol2":[price,split] }
	// e.g. { "2010-1-1": "MSFT":[200,1.0] , "AAPL":[200,1.0] }
	//		{ "2010-1-2": "MSFT":[300,2.0] , "AAPL":[300,2.0] }
	private Map<String,List<Stock>> queryStockData(Set<String> symbols, String startDate, String endDate) {
		Map<String,List<Stock>> data = new TreeMap<String,List<Stock>>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			for(String symbol : symbols) {
				/*if(getSymbolId(symbol) == NOT_FOUND){
					insertData(symbol, startDate, endDate);
				}*/
				String sql = "{CALL QUERY_DATA(?, ?, ?)}";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, symbol);
				pstmt.setString(2, startDate);
				pstmt.setString(3,endDate);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					String date = rs.getString("date_as_id"); 
					double price = rs.getDouble("price");
					double split = rs.getDouble("split_ratio");
					Stock stock = new Stock(symbol, price, split);
					if (data.containsKey(date)){
						List<Stock> stockList = data.get(date);
						stockList.add(stock);
					}else {
						List<Stock> stockList = new ArrayList<Stock>();
						stockList.add(stock);
						data.put(date, stockList);
					}
				}
			}
			return data;
		}catch(SQLException ex){
			logger.error("queryStockData() failed." + ex.getMessage());
		}finally{
			release(pstmt,rs);
		}
		return null;
	}

	private List<String> getMutualIpoDelisting(){
		String symbolsStr = "'";
		for( String s : symbols){
			symbolsStr += s;
			symbolsStr += "','";
		}
		//cut off trailing ( ,' )
		symbolsStr = symbolsStr.substring(0, symbolsStr.length()-2);
		//must be this way to have the symbolsStr in double quotes. e.g "'MSFT','GOOGL'"
		String sql = String.format(" CALL GET_MUTUAL_IPO_DELISTING_DATE(\"%s\")",symbolsStr);
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try{
			cstmt = conn.prepareCall(sql);
			rs = cstmt.executeQuery();
			if(rs.next()){
				List<String> lst = new ArrayList<String>();
				lst.add(rs.getString("@mutualIpo"));
				lst.add(rs.getString("@mutualDelisting"));
				return lst;
			}
		}catch(SQLException ex){
			logger.error("SQL exception:" + ex.getMessage());
		}finally{
			release(cstmt,rs);
		}
		return null;
	}

	//return symbol_id of the symbol in SYMBOLS table
	private  int getSymbolId(String symbol) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			String sql = "SELECT symbol_id FROM Symbols WHERE symbol=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, symbol);
			rs = pstmt.executeQuery();
			if(rs.next()){
				int symbolId = rs.getInt("symbol_id");
				return symbolId;
			}
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}finally{
			release(pstmt,rs);
		}
		return NOT_FOUND;
	}


	public void close(){
		ConnectionManager.getInstance().releaseConnection(conn);
	}
}
