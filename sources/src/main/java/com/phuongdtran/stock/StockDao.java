package com.phuongdtran.stock;

import static com.phuongdtran.util.Release.release;
import static com.phuongdtran.util.ThreadPool.awaitTerminationAfterShutdown;

import java.lang.invoke.MethodHandles;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phuongdtran.util.ConnectionManager;
import com.phuongdtran.util.ThreadPool;
public class StockDao {


	private static Connection conn = null;
	private static String mutualIpo = "";
	private static String mutualDelisting = "";
	private static Set<String> prevSymbols;
	private static String prevEndDate;
	private static String prevStartDate;
	public static final int NOT_FOUND = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * It will do queries getting data from database.<br>
	 * If requested data is unavailable, it will get that unavailable data from Quandl<br>
	 * and insert into database before selecting.
	 * @param symbol
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Map<String,List<Stock>> getData(Set<String> symbols, String startDate, String endDate) throws SQLException {
		//if(conn == null){ //TODO: why get error "connection closed" with this "if"
			conn = ConnectionManager.getInstance().getConnection();
			if (conn == null){
				throw new SQLException("Could not make a connection to database");
			//}
		}
		prevSymbols = symbols;
		prevStartDate = startDate;
		prevEndDate = endDate;
		
		try{
			updateStockCache();

			// first element is mutualIPO date, second one is mutualDelisting date
			String[] ipoDelisting = getMutualIpoDelisting();
			//update mututalIpo and mutualDelisting
			if(ipoDelisting != null){
				if(!Objects.equals(mutualIpo, ipoDelisting[0])){
					mutualIpo = ipoDelisting[0];
				}
				if(!Objects.equals(mutualDelisting, ipoDelisting[1])){
					mutualDelisting = ipoDelisting[1];
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			//return data of multiple symbols
			//return data of all symbols from ipo date to delisting date if start date and end date are both out of range
			if(sdf.parse(startDate).before(sdf.parse(mutualIpo)) 
					&& sdf.parse(endDate).after(sdf.parse(mutualDelisting))) {
				return queryStockData(mutualIpo, mutualDelisting);

				//return data of all symbols from ipo date to end date if start date is before ipo date
			}else if (sdf.parse(startDate).before(sdf.parse(mutualIpo))) {
				return queryStockData(mutualIpo, endDate);

				//return data of all symbols from ipo date to end date if end date is after delisting date
			}else if(sdf.parse(endDate).after(sdf.parse(mutualDelisting))){
				return queryStockData(startDate, mutualDelisting);
			}

			//return data of one symbol
			//if(getSymbolId(symbol) != NOT_FOUND){
				return queryStockData(startDate, endDate);
			//}
		}catch(ParseException ex){
			logger.error("queryStockData() failed: error on using SimpleDateFormat." + ex.getMessage());
		}
		return null;
	}

	/**
	 * @param startDate
	 * @param endDate<br>
	 * Referenced at <a href="http://www.baeldung.com/java-executor-wait-for-threads">http://www.baeldung.com</a>
	 */
	private static void updateStockCache() {
		ThreadPoolExecutor executor = ThreadPool.getInstance();
		List<CacheCallable> callables = new ArrayList<CacheCallable>();
		try{
			for(String s : prevSymbols){
				callables.add(new CacheCallable(s, prevStartDate, prevEndDate, conn));
			}
			executor.invokeAll(callables);
		}catch( InterruptedException ex) {
			logger.error("updateStockCache() failed." + ex.getMessage());
		}catch( NullPointerException ex){
			logger.error("updateStockCache() failed." + ex.getMessage());
		}
		//awaitTerminationAfterShutdown(executor);
	}

	

	//data return structure: { "date": "symbol1":[price,split] , "symbol2":[price,split] }
	// e.g. { "2010-1-1": "MSFT":[200,1.0] , "AAPL":[200,1.0] }
	//		{ "2010-1-2": "MSFT":[300,2.0] , "AAPL":[300,2.0] }
	private static Map<String,List<Stock>> queryStockData(String startDate, String endDate) {
		Map<String,List<Stock>> data = new TreeMap<String,List<Stock>>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			for(String symbol : prevSymbols) {
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

	private static String[] getMutualIpoDelisting(){
		String symbolsStr = "'";
		for( String s : prevSymbols){
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
				String[] lst = new String[2];
				lst[0] = rs.getString("@mutualIpo");
				lst[1] = rs.getString("@mutualDelisting");
				return lst;
			}
		}catch(SQLException ex){
			logger.error("SQL exception:" + ex.getMessage());
		}finally{
			release(cstmt,rs);
		}
		return null;
	}

	public static void close(){
		ConnectionManager.getInstance().releaseConnection(conn);
	}
}
