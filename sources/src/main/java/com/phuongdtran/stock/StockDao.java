package com.phuongdtran.stock;

import static com.phuongdtran.util.Release.release;

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
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phuongdtran.util.ConnectionManager;
import com.phuongdtran.util.ThreadPool;
public class StockDao {


	private static Connection conn = null;
	private static String mutualIpo = "";
	private static String mutualDelisting = "";
	private static Set<String> prevSymbols = new HashSet<String>();
	private static String prevEndDate;
	private static String prevStartDate;
	public static final int NOT_FOUND = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * It will do queries getting data from database.<br>
	 * If requested data is unavailable, it will get that unavailable data from Quandl<br>
	 * and insert into database before selecting.
	 * @param symbols
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Map<String,List<Stock>> getData(Set<String> symbols, String startDate, String endDate) {
		try{
			getConnection();
			String symbol = "";//holding a symbol in Set in case only need query data for one symbol
			if(prevSymbols.isEmpty()){
				prevSymbols.addAll(symbols);
			} else{
				symbols.removeAll(prevSymbols);
				if(symbols.iterator().hasNext()){ 
					symbol = symbols.iterator().next();
					prevSymbols.add(symbol);
				}
			}
			updateStockCache();
			String[] ipoDelisting = getMutualIpoDelisting(prevSymbols);
			if ( !Objects.equals(mutualIpo, ipoDelisting[0]) || !Objects.equals(mutualIpo, ipoDelisting[1])){
				mutualIpo = ipoDelisting[0];
				mutualDelisting = ipoDelisting[1];
				return queryMutilpleSymbols(prevSymbols,startDate, endDate);
			}
			//return data of multiple symbols
			if(!Objects.equals(prevStartDate, startDate) || !Objects.equals(prevEndDate, endDate) || symbols.size()>1) {
				prevStartDate = startDate;
				prevEndDate = endDate;
				return queryMutilpleSymbols(prevSymbols,startDate, endDate);
			}
			//return data of one symbol
			if(getSymbolId(symbol) != NOT_FOUND){
				return queryStockData(new HashSet<String>(Arrays.asList(symbol)),startDate, endDate);
			}else if( symbol != ""){
				prevSymbols.remove(symbol);
			}
		}catch(SQLException ex){
			logger.error("queryStockData():SQLException.", ex.getMessage());
		}
		return null;
	}

	public static void remove(String symbol) {
		prevSymbols.remove(symbol);
	}
	
	private static Map<String, List<Stock>> queryMutilpleSymbols(Set<String> prevSymbols, String startDate, String endDate) {
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			if(sdf.parse(startDate).before(sdf.parse(mutualIpo)) && sdf.parse(endDate).after(sdf.parse(mutualDelisting))) {
				return queryStockData(prevSymbols,mutualIpo, mutualDelisting);

				//return data of all symbols from ipo date to end date if start date is before ipo date
			}else if (sdf.parse(startDate).before(sdf.parse(mutualIpo))) {
				return queryStockData(prevSymbols,mutualIpo, endDate);

				//return data of all symbols from ipo date to end date if end date is after delisting date
			}else if(sdf.parse(endDate).after(sdf.parse(mutualDelisting))){
				return queryStockData(prevSymbols,startDate, mutualDelisting);
			}
			return queryStockData(prevSymbols,startDate, endDate);
		}catch(ParseException ex){
			logger.error("queryStockData():ParseException. Error on using SimpleDateFormat." + ex.getMessage());
		}
		return null;
	}

	/**
	 * @throws SQLException
	 */
	private static void getConnection() throws SQLException {
		//if(conn == null){ //TODO: why get error "connection closed" with this "if"
		conn = ConnectionManager.getInstance().getConnection();
		if (conn == null){
			throw new SQLException("Could not make a connection to database");
			//}
		}
	}

	/**
	 * @param startDate
	 * @param endDate<br>
	 * Referenced at <a href="http://www.baeldung.com/java-executor-wait-for-threads">http://www.baeldung.com</a>
	 */
	private static void updateStockCache() { //TODO: tables not updated when date changed
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
	private static Map<String,List<Stock>> queryStockData(Set<String> symbols, String startDate, String endDate) {
		Map<String,List<Stock>> data = new TreeMap<String,List<Stock>>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			for(String symbol : symbols) {
				String sql = "{call query_data(?, ?, ?)}";
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

	/**
	 * get shared ipo date and delisting date of symbols 
	 * @param symbols a Set collection
	 * @return an array with 2 elements.
	 * First element is mutualIPO date, second one is mutualDelisting date
	 */
	private static String[] getMutualIpoDelisting(Set<String> symbols){
		String symbolsStr = "'";
		for( String s : symbols){
			symbolsStr += s;
			symbolsStr += "','";
		}
		//cut off trailing ( ,' )
		symbolsStr = symbolsStr.substring(0, symbolsStr.length()-2);
		//must be this way to have the symbolsStr in double quotes. e.g "'MSFT','GOOGL'"
		String sql = String.format("call get_mutual_ipo_delisting_date(\"%s\")",symbolsStr);
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

	private static int getSymbolId(String symbol) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			String sql = "SELECT symbol_id FROM symbols WHERE symbol=?";
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

	public static void close(){
		ConnectionManager.getInstance().releaseConnection(conn);
	}
}
