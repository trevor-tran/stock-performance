package app.stock;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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

import app.util.QueryExecutionHandler;

public class StockDao {

	//quandl api key
	private static final String apiKey = "LSHfJJyvzYHUyU9jHpn6";
	public static final int NOT_FOUND = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static ResultSet queryStockData(QueryExecutionHandler queryHandler, String symbol, String startDate, String endDate){
		try{
			if(getSymbolId(queryHandler, symbol) == NOT_FOUND){
				insertData(queryHandler, symbol, startDate, endDate);
			}else{
				mayUpdateTable(queryHandler, symbol, startDate, endDate);
			}
			
			String sql = "{CALL QUERY_DATA(?, ?, ?)}";
			PreparedStatement pstmt = queryHandler.prepareStatement(sql);
			pstmt.setString(1, symbol);
			pstmt.setString(2, startDate);
			pstmt.setString(3,endDate);
			ResultSet rs = pstmt.executeQuery();
			return rs;
		}catch(Exception ex){
			logger.error("queryStockData() failed." + ex.getMessage());
		}
		return null;
	}
	
	private static void mayUpdateTable (QueryExecutionHandler queryHandler, String tableName, String startDate,String endDate) {
		try{
			//e.g. front-end request from "2017-5-1" to "2017-10-1"
			//available data in mysql from "2017-8-1" to "2017-10-1"
			//only request Quandl data from "2017-5-1" to "2017-7-31"
			//String sql = String.format("{ CALL DATE_BEFORE_FIRST_DATE('%s', '%s') }",tableName,startDate);
			String sql = "{CALL DATE_BEFORE_FIRST_DATE(?,?)}";
			CallableStatement cstmt = queryHandler.prepareCall(sql);
			cstmt.setString(1, tableName);
			cstmt.setString(2, startDate);
			ResultSet rs = cstmt.executeQuery();
			if(rs.next()){
				String newEndDate = rs.getString("@beforeFirstDate");
				if(newEndDate != null){
					insertData(queryHandler, tableName, startDate, newEndDate);
				}
			}
			//e.g. front-end request from "2017-5-1" to "2017-10-1"
			//available data in mysql from "2017-5-1" to "2017-8-1"
			//only request Quandl data from "2017-8-2" to "2017-10-1"
			sql = "{ CALL DATE_AFTER_LAST_DATE(?, ?) }";
			cstmt = queryHandler.prepareCall(sql);
			cstmt.setString(1, tableName);
			cstmt.setString(2, endDate);
			rs = cstmt.executeQuery();
			if(rs.next()){
				String newStartDate = rs.getString("@afterLastDate");
				if(newStartDate != null){
					insertData(queryHandler, tableName, newStartDate, endDate);
				}
			}
			rs.close();
		}catch(Exception ex){
			logger.error("mayUpdateTable() failed."+ex.getMessage());
		}
	}
	
	private static void insertData(QueryExecutionHandler queryHandler, String symbol, String startDate, String endDate){
		try{
			JsonArray quandlData = getQuandlData(symbol, startDate, endDate);
			if(quandlData != null){
				CallableStatement cstmt;
				int symbolId = getSymbolId(queryHandler, symbol);
				if(symbolId == NOT_FOUND){
					//add new symbol to SYMBOLS table and create new table after the symbol
					//e.g. "MSFT" added to SYMBOLS and the table named "MSFT" created
					String sql = "{ CALL ADD_TO_SYMBOLS_AND_CREATE_TABLE(?) }";
					cstmt = queryHandler.prepareCall(sql);
					cstmt.setString(1, symbol);
					cstmt.execute();
					symbolId = getSymbolId(queryHandler,symbol);
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
					cstmt = queryHandler.prepareCall(sql);
					cstmt.setString(1, date);
					cstmt.setDouble(2, price);
					cstmt.setDouble(3, split);
					cstmt.setInt(4, symbolId);
					cstmt.executeUpdate();
				}
			}
		}catch(Exception ex){
			logger.error("insertData() failed." + ex.getMessage());
		}
	}	
	
	//return symbol_id of the symbol in SYMBOLS table
	private static int getSymbolId(QueryExecutionHandler queryHandler , String symbol) {
		try{
			String sql = "SELECT symbol_id FROM Symbols WHERE symbol=?";
			PreparedStatement pstmt = queryHandler.prepareStatement(sql);
			pstmt.setString(1, symbol);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				int symbolId = rs.getInt("symbol_id");
				rs.close();
				return symbolId;
			}
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
			logger.error(ex.getMessage());
		}catch(ClientProtocolException ex){
			logger.error(ex.getMessage());
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
}
