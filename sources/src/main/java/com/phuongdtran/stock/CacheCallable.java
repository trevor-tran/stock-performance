package com.phuongdtran.stock;

import static com.phuongdtran.stock.StockDao.NOT_FOUND;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

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
import com.phuongdtran.util.StatementAndResultSet;

public class CacheCallable extends StatementAndResultSet implements Callable<Void> {

	//quandl api keys
	private static final String[] apiKeys = { "LSHfJJyvzYHUyU9jHpn6", "gCex5psCGUqRBsjyXAxs"};
	private static int index = 0;
	private String symbol;
	private String startDate;
	private String endDate;
	private Connection conn;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public CacheCallable(String symbol, String startDate, String endDate, Connection conn) throws NullPointerException {
		this.symbol = symbol;
		this.startDate = startDate;
		this.endDate = endDate;
		if(conn == null){
			throw new NullPointerException("Connection is null");
		}
		this.conn = conn;
	}

	@Override
	public Void call() throws Exception {
		if(getSymbolId(symbol) == NOT_FOUND){
			//only add ticker to SYMBOLS table and create a new table for that ticker
			//if got data return from Quandl 
			insertData(symbol, startDate, endDate);
		}else if(getSymbolId(symbol) != NOT_FOUND){
			mayUpdateTable(symbol, startDate, endDate);
		}
		return null;
	}
	
	private void mayUpdateTable (String tableName, String startDate,String endDate) {
		//e.g. front-end request from "2017-5-1" to "2017-10-1"
		//available data in mysql from "2017-8-1" to "2017-10-1"
		//only request Quandl data from "2017-5-1" to "2017-7-31"
		updateAtTheEnd(tableName, endDate);

		//e.g. front-end request from "2017-5-1" to "2017-10-1"
		//available data in mysql from "2017-5-1" to "2017-8-1"
		//only request Quandl data from "2017-8-2" to "2017-10-1"
		updateAtTheStart(tableName, startDate);
	}

	private void updateAtTheStart(String tableName, String startDate){
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try{
			String sql = "{CALL PREVIOUS_TO_FIRST_DATE(?,?)}";
			cstmt = conn.prepareCall(sql);
			cstmt.setString(1, tableName);
			cstmt.setString(2, startDate);
			rs = cstmt.executeQuery();
			if(rs.next()){
				String newEndDate = rs.getString("@previousDate");
				if(newEndDate != null){
					insertData( tableName, startDate, newEndDate);
				}
			}
		}catch(SQLException ex){
			logger.error("mayUpdateTable() failed."+ex.getMessage());
		}finally{
			release(cstmt,rs);
		}

	}

	private void updateAtTheEnd(String tableName,String endDate){
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try{
			String sql = "{ CALL NEXT_TO_LAST_DATE(?, ?) }";
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
		}catch(SQLException ex){
			logger.error("mayUpdateTable() failed."+ex.getMessage());
		}finally{
			release(cstmt,rs);
		}
	}

	private void insertData(String symbol, String startDate, String endDate){
		CallableStatement cstmt = null;
		try{
			JsonArray quandlData = getQuandlData(symbol, startDate, endDate);
			if(quandlData != null){
				int symbolId = getSymbolId(symbol);
				if(symbolId == NOT_FOUND){
					//add new symbol to SYMBOLS table and create new table after the symbol
					//e.g. "MSFT" added to SYMBOLS and the table named "MSFT" created
					String sql = "{ CALL ADD_TO_SYMBOLS_AND_CREATE_TABLE(?) }";
					cstmt = conn.prepareCall(sql);
					cstmt.setString(1, symbol);
					cstmt.executeUpdate();
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
				updateIpoDelistingDate(symbol);
			}
		}catch(SQLException ex){
			logger.error("insertData() failed." + ex.getMessage());
		}finally{
			release(cstmt);
		}
	}	

	private void updateIpoDelistingDate(String symbol) {
		CallableStatement cstmt = null;
		try {
			String sql = "{ CALL UPDATE_IPO_DELISTING_DATE(?) }";
			cstmt = conn.prepareCall(sql);
			cstmt.setString(1, symbol);
			cstmt.execute();
		}catch(SQLException ex){
			logger.error("updateIpoDelistingDate() failed." + ex.getMessage());
		}finally{
			release(cstmt);
		}
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
		}catch(IOException ex){
			logger.error("IOException:"+ex.getMessage());	
		}catch(URISyntaxException ex){
			logger.error("URISyntaxException:"+ex.getMessage());	
		}
		return null; //TODO: may need a proper return 
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
				.setParameter("api_key", apiKeys[index++ % 2])
				.build();
		return uri;
	}
}
