package com.phuongdtran.stock;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.phuongdtran.stock.StockDao.NOT_FOUND;
import static com.phuongdtran.util.ReleaseStatement.release;


public class CacheCallable implements Callable<Void> {
	@Override
	public Void call() throws Exception {
		return null;
	}
//
//	private static int index = 0;
//
//	private String symbol;
//	private Connection conn;
//	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//
//	public CacheCallable(String symbol, Connection conn) throws NullPointerException {
//		this.symbol = symbol;
//		if(conn == null){
//			throw new NullPointerException("Connection is null");
//		}
//		this.conn = conn;
//	}
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
