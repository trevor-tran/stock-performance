package com.phuongdtran.stock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.phuongdtran.executor.CytherExecutor;
import com.phuongdtran.executor.IExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Callable;



public class CacheCallable implements Callable<Ticker> {

	private String symbol;
	private IStockDao stockDao;
	private DateTimeFormatter formatter;
	private String startDate;
	private String endDate;

	public CacheCallable(String symbol, IExecutor executor, String startDate, String endDate) throws SQLException {
		this.symbol = symbol;
		this.stockDao = new StockDao(executor);
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public Ticker call() throws Exception {
		try {
			this.stockDao.open();
			Ticker ticker = stockDao.getSymbolInfo(symbol);
			if (ticker == null) {
				// this ticker has not added to DB before
				// get stock data as much as possible from a financial service.
				cacheStockFull(symbol);
			} else {
				// the ticker is is DB. Depending on what was the latest data point,
				// a decision is made to either only get 100 latest data points or full-length time series
				// if necessary.
				LocalDate recentDataPoint = LocalDate.parse(ticker.getDelisting(), formatter);
				LocalDate requestEndDate = LocalDate.parse(endDate, formatter);
				LocalDate now = LocalDate.parse(LocalDate.now().toString(), formatter);
				// TODO: "now" maybe weekends, need to do something to reduce the number of API calls
				if (recentDataPoint.isBefore(now) && requestEndDate.isAfter(recentDataPoint)) {
					if (recentDataPoint.plusDays(100).isEqual(now) || recentDataPoint.plusDays(100).isAfter(now)) {
						cacheStockCompact(symbol);
					} else {
						cacheStockFull(symbol);
					}
				}
			}
			return stockDao.getSymbolInfo(symbol);
		} finally {
			stockDao.close();
		}
	}

	private void cacheStockFull(String symbol) throws SQLException{
		cacheStock(symbol, IStockService.OUTPUTSIZE.FULL);

	}

	private void cacheStockCompact(String symbol) throws SQLException {
		cacheStock(symbol, IStockService.OUTPUTSIZE.COMPACT);
	}

	private void cacheStock(String symbol, IStockService.OUTPUTSIZE outputsize) throws SQLException {
		Map<String, JsonObject> raw = StockAPIHandler.get(symbol, outputsize);
		if (raw != null) {
			for (Map.Entry<String, JsonObject> entry : raw.entrySet()) {
				JsonElement price = entry.getValue().getAsJsonPrimitive("price");
				JsonElement dividend = entry.getValue().getAsJsonPrimitive("dividend");
				JsonElement split = entry.getValue().getAsJsonPrimitive("split");

				Stock stock = new Stock(symbol, entry.getKey(), price.getAsDouble(),
						split.getAsDouble(), dividend.getAsDouble());
				stockDao.add(stock);
			}
		}
	}
//
//	//TODO: Exception is not catched in StockDao class, should catch here?
//	@Override
//	public Void call() throws Exception {
//		if(getSymbolId(symbol) == NOT_FOUND){
//			//only add ticker to SYMBOLS table and create a new table for that ticker
//			//if got data return from Quandl
//			insertData(symbol);
//		}else if(getSymbolId(symbol) != NOT_FOUND){
//			mayUpdateTable(symbol);
//		}
//		return null;
//	}
//
//	private void mayUpdateTable (String tableName, String startDate,String endDate) {
//		//e.g. front-end request from "2017-5-1" to "2017-10-1"
//		//available data in mysql from "2017-8-1" to "2017-10-1"
//		//only request Quandl data from "2017-5-1" to "2017-7-31"
//		updateAtTheEnd(tableName, endDate);
//
//		//e.g. front-end request from "2017-5-1" to "2017-10-1"
//		//available data in mysql from "2017-5-1" to "2017-8-1"
//		//only request Quandl data from "2017-8-2" to "2017-10-1"
//		updateAtTheStart(tableName, startDate);
//	}
//
//	private void updateAtTheStart(String tableName, String startDate){
//		CallableStatement cstmt = null;
//		ResultSet rs = null;
//		try{
//			String sql = "{CALL PREVIOUS_TO_FIRST_DATE(?,?)}";
//			cstmt = conn.prepareCall(sql);
//			cstmt.setString(1, tableName);
//			cstmt.setString(2, startDate);
//			rs = cstmt.executeQuery();
//			if(rs.next()){
//				String newEndDate = rs.getString("@previousDate");
//				if(newEndDate != null){
//					insertData( tableName, startDate, newEndDate);
//				}
//			}
//		}catch(SQLException ex){
//			logger.error("mayUpdateTable() failed."+ex.getMessage());
//		}finally{
//			release(cstmt,rs);
//		}
//
//	}
//
//	private void updateAtTheEnd(String tableName,String endDate){
//		CallableStatement cstmt = null;
//		ResultSet rs = null;
//		try{
//			String sql = "{ CALL NEXT_TO_LAST_DATE(?, ?) }";
//			cstmt = conn.prepareCall(sql);
//			cstmt.setString(1, tableName);
//			cstmt.setString(2, endDate);
//			rs = cstmt.executeQuery();
//			if(rs.next()){
//				String newStartDate = rs.getString("@nextDate");
//				if(newStartDate != null){
//					insertData( tableName, newStartDate, endDate);
//				}
//			}
//		}catch(SQLException ex){
//			logger.error("mayUpdateTable() failed."+ex.getMessage());
//		}finally{
//			release(cstmt,rs);
//		}
//	}
//
//	private void insertData(String symbol){
//		CallableStatement cstmt = null;
//		try{
//            Map<String,JsonObject> stockData = StockAPIHandler.get(symbol);
//			if(stockData != null){
//				int symbolId = getSymbolId(symbol);
//				if(symbolId == NOT_FOUND){
//					//add new symbol to SYMBOLS table and create new table after the symbol
//					//e.g. "MSFT" added to SYMBOLS and the table named "MSFT" created
//					String sql = "{ CALL ADD_TO_SYMBOLS_AND_CREATE_TABLE(?) }";
//					cstmt = conn.prepareCall(sql);
//					cstmt.setString(1, symbol);
//					cstmt.executeUpdate();
//					symbolId = getSymbolId(symbol);
//				}
//				for (String date : stockData.keySet()) {
//		            JsonObject value = stockData.get(date);
//					double price = value.getAsJsonPrimitive("price").getAsDouble();
//					double split = value.getAsJsonPrimitive("split").getAsDouble();
//					//get ready to insert into table
//					String sql = String.format("INSERT INTO %s VALUES(?, ?, ?, ?)",symbol);
//					cstmt = conn.prepareCall(sql);
//					cstmt.setString(1, date);
//					cstmt.setDouble(2, price);
//					cstmt.setDouble(3, split);
//					cstmt.setInt(4, symbolId);
//					cstmt.executeUpdate();
//				}
//				updateIpoDelistingDate(symbol);
//			}
//		}catch(SQLException ex) {
//            logger.error("Failed to update database." + ex.getMessage());
//        }catch (IOException ex) {
//            logger.error("Failed to get data from an API service." + ex.getMessage());
//		}finally{
//			release(cstmt);
//		}
//	}
//
//	private void updateIpoDelistingDate(String symbol) {
//		CallableStatement cstmt = null;
//		try {
//			String sql = "{ CALL UPDATE_IPO_DELISTING_DATE(?) }";
//			cstmt = conn.prepareCall(sql);
//			cstmt.setString(1, symbol);
//			cstmt.execute();
//		}catch(SQLException ex){
//			logger.error("updateIpoDelistingDate() failed." + ex.getMessage());
//		}finally{
//			release(cstmt);
//		}
//	}
//
//	//return symbol_id of the symbol in SYMBOLS table
//	private  int getSymbolId(String symbol) {
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
}
