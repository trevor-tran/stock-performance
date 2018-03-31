package app.stock;

import static app.stock.StockController.symbolsSet;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.util.ConnectionManager;
import app.util.StatementAndResultSet;
public class StockDao extends StatementAndResultSet implements AutoCloseable{

	//quandl api key
	private static final String apiKey = "LSHfJJyvzYHUyU9jHpn6";
	private Connection conn = null;
	private static String mutualIpo = "";
	private String mutualDelisting = "";
	public static final int NOT_FOUND = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public StockDao() {
		conn = ConnectionManager.getInstance().getConnection();
	}

	public Map<String,List<Stock>> getData(String symbol, String startDate, String endDate){
		try{
			if(getSymbolId(symbol) == NOT_FOUND){
				insertData(symbol, startDate, endDate);
			}else{
				mayUpdateTable(symbol, startDate, endDate);
			}
			// first element is mutualIPO date, second one is mutualDelisting date
			List<String> ipoDelisting = getIpoDelisting();
			if(ipoDelisting != null){
				if(!Objects.equals(mutualIpo, ipoDelisting.get(0) )){
					mutualIpo = ipoDelisting.get(0);
				}
				if(!Objects.equals(mutualDelisting, ipoDelisting.get(1) )){
					mutualDelisting = ipoDelisting.get(1);
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			if(sdf.parse(startDate).before(sdf.parse(mutualIpo))) {
				startDate = mutualIpo;
			}
			if(sdf.parse(endDate).after(sdf.parse(mutualDelisting))) {
				endDate = mutualDelisting;
			}
			return querySingleStockData(symbol, startDate, endDate);
		}catch(Exception ex){
			logger.error("queryStockData() failed." + ex.getMessage());
		}
		return null;
	}

	//structure: { "date": "symbol1":[price,split] , "symbol2":[price,split] }
	// e.g. { "2010-1-1": "MSFT":[200,1.0] , "AAPL":[200,1.0] }
	//		{ "2010-1-2": "MSFT":[300,2.0] , "AAPL":[300,2.0] }
	private Map<String,List<Stock>> querySingleStockData(String symbol, String startDate, String endDate) {
		try{
			Map<String,List<Stock>> data = new TreeMap<String,List<Stock>>();
			String sql = "{CALL QUERY_DATA(?, ?, ?)}";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, symbol);
			pstmt.setString(2, startDate);
			pstmt.setString(3,endDate);
			ResultSet rs = pstmt.executeQuery();

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
			release(pstmt,rs);
			return data;
		}catch(SQLException ex){
			logger.error("SQL Exception:" + ex.getMessage());
		}
		return null;
	}

	private List<String> getIpoDelisting(){
		try{
			String symbolsStr = "'";
			for( String s : symbolsSet){
				symbolsStr += s;
				symbolsStr += "','";
			}
			//cut off trailing ( ,' )
			symbolsStr = symbolsStr.substring(0, symbolsStr.length()-2);

			//must be this way to have the symbolsStr in double quotes. e.g "'MSFT','GOOGL'"
			String sql = String.format(" CALL GET_MUTUAL_IPO_DELISTING_DATE(\"%s\")",symbolsStr);
			CallableStatement cstmt = conn.prepareCall(sql);
			ResultSet rs = cstmt.executeQuery();
			if(rs.next()){
				List<String> lst = new ArrayList<String>();
				lst.add(rs.getString("@mutualIpo"));
				lst.add(rs.getString("@mutualDelisting"));
				return lst;
			}
		}catch(SQLException ex){
			logger.error("SQL exception:" + ex.getMessage());
		}
		return null;
	}

	private void mayUpdateTable (String tableName, String startDate,String endDate) {
		try{
			//e.g. front-end request from "2017-5-1" to "2017-10-1"
			//available data in mysql from "2017-8-1" to "2017-10-1"
			//only request Quandl data from "2017-5-1" to "2017-7-31"
			String sql = "{CALL PREVIOUS_TO_FIRST_DATE(?,?)}";
			CallableStatement cstmt = conn.prepareCall(sql);

			cstmt.setString(1, tableName);
			cstmt.setString(2, startDate);
			ResultSet rs = cstmt.executeQuery();

			if(rs.next()){
				String newEndDate = rs.getString("@previousDate");
				if(newEndDate != null){
					insertData( tableName, startDate, newEndDate);
				}
			}
			release(cstmt, rs);
			//e.g. front-end request from "2017-5-1" to "2017-10-1"
			//available data in mysql from "2017-5-1" to "2017-8-1"
			//only request Quandl data from "2017-8-2" to "2017-10-1"
			sql = "{ CALL NEXT_TO_LAST_DATE(?, ?) }";
			cstmt = conn.prepareCall(sql);
			cstmt.setString(1, tableName);
			cstmt.setString(2, endDate);
			rs = cstmt.executeQuery();

			if(rs.next()){
				String newStartDate = rs.getString("@nextDate");
				if(newStartDate != null){
					insertData( tableName, newStartDate, endDate);
				}
			}
			release(cstmt, rs);
		}catch(SQLException ex){
			logger.error("mayUpdateTable() failed."+ex.getMessage());
		}
	}

	private void insertData(String symbol, String startDate, String endDate){
		try{
			JsonArray quandlData = getQuandlData(symbol, startDate, endDate);
			if(quandlData != null){
				CallableStatement cstmt = null;
				int symbolId = getSymbolId(symbol);

				if(symbolId == NOT_FOUND){
					//add new symbol to SYMBOLS table and create new table after the symbol
					//e.g. "MSFT" added to SYMBOLS and the table named "MSFT" created
					String sql = "{ CALL ADD_TO_SYMBOLS_AND_CREATE_TABLE(?) }";
					cstmt = conn.prepareCall(sql);
					cstmt.setString(1, symbol);
					cstmt.execute();
					symbolId = getSymbolId(symbol);
				}

				for (JsonElement element : quandlData) {
					//each element is [date, ticker, price, split]
					// e.g. ["2016-12-28", "MSFT", 62.99, 2.0]
					JsonArray e = element.getAsJsonArray();
					String date = e.get(0).getAsString(); 
					double price = e.get(2).getAsDouble();
					double split = e.get(3).getAsDouble();
					//get ready to insert into table
					String sql = String.format("INSERT INTO %s VALUES(?, ?, ?, ?)",symbol);
					cstmt = conn.prepareCall(sql);

					cstmt.setString(1, date);
					cstmt.setDouble(2, price);
					cstmt.setDouble(3, split);
					cstmt.setInt(4, symbolId);

					cstmt.executeUpdate();
				}
				release(cstmt);
				updateIpoDelistingDate(symbol);
			}
		}catch(SQLException ex){
			logger.error("insertData() failed." + ex.getMessage());
		}
	}	

	private void updateIpoDelistingDate(String symbol) {
		try {
			String sql = "{ CALL UPDATE_IPO_DELISTING_DATE(?) }";
			CallableStatement cstmt = conn.prepareCall(sql);
			cstmt.setString(1, symbol);
			cstmt.execute();
			release(cstmt);
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}

	//return symbol_id of the symbol in SYMBOLS table
	private  int getSymbolId(String symbol) {
		try{
			String sql = "SELECT symbol_id FROM Symbols WHERE symbol=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, symbol);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				int symbolId = rs.getInt("symbol_id");
				release(pstmt,rs);
				return symbolId;
			}
			release(pstmt,rs);
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return NOT_FOUND;
	}

	//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e49
	//https://github.com/google/gson/blob/master/UserGuide.md
	private static JsonArray getQuandlData(String symbol, String startDate, String endDate) {
		try{
			URI uri = getRequestUri(symbol, startDate, endDate);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet request = new HttpGet(uri);
			System.out.println("http get: " + request.getURI());//TODO: uri printing, need del later
			ResponseHandler<Map<String,JsonObject>> rh = new QuandlResponseHandler();
			Map<String,JsonObject> quandlResponse = httpclient.execute(request,rh);
			//throw error message if failed to request data
			if(quandlResponse.containsKey("failure")){
				JsonObject error = quandlResponse.get("failure").getAsJsonObject("quandl_error");
				String code = error.get("code").getAsString();
				String msg = error.get("message").getAsString();
				throw new HttpException("Quandl error code: " + code + ". Message: " + msg);
			}else{
				//handle data if success
				JsonArray dataArr = quandlResponse.get("success").getAsJsonObject("datatable").getAsJsonArray("data");
				if(dataArr.size() == 0){
					return null;
				}
				return dataArr;
			} 
		}catch (HttpException ex){
			logger.error("HttpException:"+ex.getMessage());
		}catch(ClientProtocolException ex){
			logger.error("ClientProtocolException:" + ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());	
		}
		return null; //TODO: need a proper return 
	}

	//build URL to request data from Quandl
	private static URI getRequestUri(String symbol, String startDate, String endDate) throws URISyntaxException{
		//https://docs.quandl.com/docs/parameters-1
		URI uri = new URIBuilder()
				.setScheme("https")
				.setHost("quandl.com")
				.setPath("/api/v3/datatables/WIKI/PRICES.json")
				.setParameter("qopts.columns", "date,ticker,close,split_ratio")
				.setParameter("date.gte", startDate)
				.setParameter("date.lte", endDate)
				.setParameter("ticker", symbol)
				.setParameter("api_key", apiKey)
				.build();
		return uri;
	}

	@Override
	public void close() throws Exception {
		ConnectionManager.getInstance().releaseConnection(conn);

	}
}
