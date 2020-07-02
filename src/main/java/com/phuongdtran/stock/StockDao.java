package com.phuongdtran.stock;

import com.google.gson.Gson;
import com.phuongdtran.executor.IExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;

import static org.neo4j.helpers.collection.MapUtil.map;

public class StockDao implements IStockDao {


//	private static Connection conn = null;
//	private static String mutualIpo = "";
//	private static String mutualDelisting = "";
//	private static Set<String> prevSymbols = new HashSet<String>();
//	private static String prevEndDate;
//	private static String prevStartDate;

	final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private IExecutor executor;
	private boolean isOpen;
	private Gson gson;

	public StockDao(IExecutor executor) {
		this.executor = executor;
		isOpen = false;
		gson = new Gson();
	}

	@Override
	public void open() throws SQLException {
		executor.open();
		isOpen = true;
	}

	@Override
	public void close() {
		executor.close();
		isOpen = false;
	}

	@Override
	public boolean add(Stock stock) throws SQLException {
		connectionOpened();
		if (!existsSymbolNode(stock.getSymbol())) {
			addSymbolNode(stock.getSymbol());
		}
		addDateNode(stock.getSymbol(), stock.getDate(), stock.getPrice(), stock.getDividend(), stock.getSplit());
//		addRelationship(stock.getSymbol(), stock.getDate());

		return true;
	}

	private void setSymbolInfo(String symbol, String date) throws SQLException {
		connectionOpened();
		String query = " MATCH (s:Symbol {name: $symbol}) " +
						" SET " +
						" (CASE WHEN date($d) < s.ipo THEN s END).ipo = date($d), " +
						" (CASE WHEN date($d) > s.delisting THEN s END).delisting = date($d)";
		executor.create(query, map("symbol", symbol, "d", date));
	}

	/**
	 * return null if there is no info for the symbol
	 * @param symbol
	 * @return
	 * @throws SQLException
	 */
	@Override
	public Ticker getSymbolInfo(String symbol) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		if (!existsSymbolNode(symbol)) {
			return null;
		}
		String query = "MATCH (s:Symbol) WHERE toUpper(s.name) = $symbol RETURN s.ipo AS ipo, s.delisting AS delisting";
		Iterator<Map<String, Object>> iterator = executor.query(query, map("symbol", symbol));
		if (iterator.hasNext()) {
			Map<String, Object> tickerInfo = map(iterator.next());
			return new Ticker(symbol, tickerInfo.get("ipo").toString(), tickerInfo.get("delisting").toString());
		}
		return null;
	}

	@Override
	public List<Stock> get(String symbol, String startDate, String endDate) throws SQLException{
		connectionOpened();
		symbol = symbol.toUpperCase();
		String query = "MATCH (s:Symbol {name: $symbol})-[:HAS]->(d:Date) " +
				"WHERE date($startdate) <= d.date AND d.date <= date($enddate) " +
				"RETURN d.symbol AS symbol, d.date AS date, d.price AS price, d.dividend AS dividend, d.split AS split";
		Iterator<Map<String,Object>> iterator = executor.query(query,
				map("symbol", symbol, "startdate", startDate, "enddate", endDate));
		List<Stock> result = new ArrayList<>();
		while (iterator.hasNext()) {
			Map<String, Object> singleDay = map(iterator.next());
			Stock stock = new Stock(symbol,
					singleDay.get("date").toString(),
					Double.parseDouble(singleDay.get("price").toString()),
					Double.parseDouble(singleDay.get("split").toString()),
					Double.parseDouble(singleDay.get("dividend").toString()));
			result.add(stock);
		}
//		result.sort(Comparator.comparing(Stock::getDate));
		return result;
	}

	private boolean addRelationship(String symbol, String date) throws SQLException {
		connectionOpened();
		String query = "MATCH (s:Symbol), (d:Date) " +
				"WHERE s.symbol=d.symbol AND d=date($date) " +
				"CREATE (s)-[r:HAS]->(d)";
		return executor.create(query, map("symbol", symbol, "date", date));

	}

	private boolean addDateNode(String symbol, String date, double price, double dividend, double split) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		setSymbolInfo(symbol ,date);
		String query = "MATCH (s:Symbol) WHERE s.name = $symbol " +
					" MERGE (d:Date {date: date($date), symbol:$symbol, price: $price, dividend: $dividend, split: $split}) <-[:HAS]- (s)";
		return executor.create(query, map("symbol", symbol, "date", date, "price", price, "dividend", dividend, "split", split));
	}

	private boolean addSymbolNode(String symbol) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		String query = "CREATE (s:Symbol {name:$symbol, ipo: date(), delisting: date('1792-05-17')} )";
		return executor.create(query, map("symbol", symbol));
	}

	private boolean existsSymbolNode(String symbol) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		String query = "MATCH (s:Symbol) WHERE s.name=$symbol RETURN s";
		Iterator<Map<String, Object>> iterator = executor.query(query, map("symbol", symbol));
		return iterator.hasNext();
	}



	/**
	 * Check whether open() has been called. If not, throw an exception.
	 * @throws SQLException
	 */
	private void connectionOpened() throws SQLException {
		if (!isOpen) {
			throw new SQLException("Not yet open a connection. Please call open()");
		}
	}

//	/**
//	 * It will do queries getting data from database.<br>
//	 * If requested data is unavailable, it will get that unavailable data from Quandl<br>
//	 * and insert into database before selecting.
//	 * @param symbols
//	 * @param startDate
//	 * @param endDate
//	 * @return
//	 */
//	public static Map<String,List<Stock>> getData(Set<String> symbols, String startDate, String endDate) {
//		try{
//			getConnection();
////			String symbol = "";//holding a symbol in Set in case only need query data for one symbol
////			if(prevSymbols.isEmpty()){
////				prevSymbols.addAll(symbols);
////			} else{
////				symbols.removeAll(prevSymbols);
////				if(symbols.iterator().hasNext()){
////					symbol = symbols.iterator().next();
////					prevSymbols.add(symbol);
////				}
////			}
////			//TODO: why should I have following two lines?
////			prevStartDate = startDate;
////			prevEndDate = endDate;
//			String symbol = null;
//			updateStockCache(symbols, startDate, endDate);
//			String[] ipoDelisting = getMutualIpoDelisting(prevSymbols);
//			if ( !Objects.equals(mutualIpo, ipoDelisting[0]) || !Objects.equals(mutualIpo, ipoDelisting[1])){
//				mutualIpo = ipoDelisting[0];
//				mutualDelisting = ipoDelisting[1];
//				return queryMutilpleSymbols(prevSymbols,startDate, endDate);
//			}
//			//return data of multiple symbols
//			if(!Objects.equals(prevStartDate, startDate) || !Objects.equals(prevEndDate, endDate) || symbols.size()>1) {
//				prevStartDate = startDate;
//				prevEndDate = endDate;
//				return queryMutilpleSymbols(prevSymbols,startDate, endDate);
//			}
//			//return data of one symbol
//			if(getSymbolId(symbol) != NOT_FOUND){
//				return queryStockData(new HashSet<String>(Arrays.asList(symbol)),startDate, endDate);
//			}else if( symbol != ""){
//				prevSymbols.remove(symbol);
//			}
//		}catch(SQLException ex){
//			logger.error("queryStockData():SQLException.", ex.getMessage());
//		}
//		return null;
//	}
//
//	public static void remove(String symbol) {
//		prevSymbols.remove(symbol);
//	}
//
//	private static Map<String, List<Stock>> queryMutilpleSymbols(Set<String> prevSymbols, String startDate, String endDate) {
//		try{
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
//			if(sdf.parse(startDate).before(sdf.parse(mutualIpo)) && sdf.parse(endDate).after(sdf.parse(mutualDelisting))) {
//				return queryStockData(prevSymbols,mutualIpo, mutualDelisting);
//
//				//return data of all symbols from ipo date to end date if start date is before ipo date
//			}else if (sdf.parse(startDate).before(sdf.parse(mutualIpo))) {
//				return queryStockData(prevSymbols,mutualIpo, endDate);
//
//				//return data of all symbols from ipo date to end date if end date is after delisting date
//			}else if(sdf.parse(endDate).after(sdf.parse(mutualDelisting))){
//				return queryStockData(prevSymbols,startDate, mutualDelisting);
//			}
//			return queryStockData(prevSymbols,startDate, endDate);
//		}catch(ParseException ex){
//			logger.error("queryStockData():ParseException. Error on using SimpleDateFormat." + ex.getMessage());
//		}
//		return null;
//	}
//
//	/**
//	 * @throws SQLException
//	 */
//	private static void getConnection() throws SQLException {
//		conn = ConnectionManager.getInstance().getConnection();
//		if (conn == null){
//			throw new SQLException("Could not make a connection to database");
//		}
//	}
//
//	/**
//	 * Referenced at <a href="http://www.baeldung.com/java-executor-wait-for-threads">http://www.baeldung.com</a>
//	 */
//	private static void updateStockCache(Set<String> symbols, String startDate, String endDate) {
//		ThreadPoolExecutor executor = ThreadPool.getInstance();
//		List<CacheCallable> updateDBCalls = new ArrayList<CacheCallable>();
//		try{
//			for(String symbol : symbols){
////				updateDBCalls.add(new CacheCallable(symbol, conn));
//			}
//			executor.invokeAll(updateDBCalls);
//		}catch( InterruptedException ex) {
//			logger.error("updateStockCache() failed." + ex.getMessage());
//		}catch( NullPointerException ex){
//			logger.error("updateStockCache() failed." + ex.getMessage());
//		}
//		//awaitTerminationAfterShutdown(executor);
//	}
//
//	//data return structure: { "date": "symbol1":[price,split] , "symbol2":[price,split] }
//	// e.g. { "2010-1-1": "MSFT":[200,1.0] , "AAPL":[200,1.0] }
//	//		{ "2010-1-2": "MSFT":[300,2.0] , "AAPL":[300,2.0] }
//	private static Map<String,List<Stock>> queryStockData(Set<String> symbols, String startDate, String endDate) {
//		Map<String,List<Stock>> data = new TreeMap<String,List<Stock>>();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try{
//			for(String symbol : symbols) {
//				String sql = "{call query_data(?, ?, ?)}";
//				pstmt = conn.prepareStatement(sql);
//				pstmt.setString(1, symbol);
//				pstmt.setString(2, startDate);
//				pstmt.setString(3,endDate);
//				rs = pstmt.executeQuery();
//
//				while (rs.next()) {
//					String date = rs.getString("date_as_id");
//					double price = rs.getDouble("price");
//					double split = rs.getDouble("split_ratio");
//					Stock stock = new Stock(symbol, price, split);
//					if (data.containsKey(date)){
//						List<Stock> stockList = data.get(date);
//						stockList.add(stock);
//					}else {
//						List<Stock> stockList = new ArrayList<Stock>();
//						stockList.add(stock);
//						data.put(date, stockList);
//					}
//				}
//			}
//			return data;
//		}catch(SQLException ex){
//			logger.error("queryStockData() failed." + ex.getMessage());
//		}finally{
//			release(pstmt,rs);
//		}
//		return null;
//	}
//
//	/**
//	 * get shared ipo date and delisting date of symbols
//	 * @param symbols a Set collection
//	 * @return an array with 2 elements.
//	 * First element is mutualIPO date, second one is mutualDelisting date
//	 */
//	private static String[] getMutualIpoDelisting(Set<String> symbols){
//		String symbolsStr = "'";
//		for( String s : symbols){
//			symbolsStr += s;
//			symbolsStr += "','";
//		}
//		//cut off trailing ( ,' )
//		symbolsStr = symbolsStr.substring(0, symbolsStr.length()-2);
//		//must be this way to have the symbolsStr in double quotes. e.g "'MSFT','GOOGL'"
//		String sql = String.format("call get_mutual_ipo_delisting_date(\"%s\")",symbolsStr);
//		CallableStatement cstmt = null;
//		ResultSet rs = null;
//		try{
//			cstmt = conn.prepareCall(sql);
//			rs = cstmt.executeQuery();
//			if(rs.next()){
//				String[] lst = new String[2];
//				lst[0] = rs.getString("@mutualIpo");
//				lst[1] = rs.getString("@mutualDelisting");
//				return lst;
//			}
//		}catch(SQLException ex){
//			logger.error("SQL exception:" + ex.getMessage());
//		}finally{
//			release(cstmt,rs);
//		}
//		return null;
//	}
//
//	private static int getSymbolId(String symbol) {
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try{
//			String sql = "SELECT symbol_id FROM symbols WHERE symbol=?";
//			pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, symbol);
//			rs = pstmt.executeQuery();
//			if(rs.next()){
//				int symbolId = rs.getInt("symbol_id");
//				return symbolId;
//			}
//		}catch(SQLException ex){
//			logger.error(ex.getMessage());
//		}finally{
//			release(pstmt,rs);
//		}
//		return NOT_FOUND;
//	}
//
//	public static void close(){
//		ConnectionManager.getInstance().releaseConnection(conn);
//	}
}
