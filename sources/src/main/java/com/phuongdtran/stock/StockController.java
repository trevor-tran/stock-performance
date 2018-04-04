package com.phuongdtran.stock;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockController {

	private static Map<String,Map<String,String>> summary;
	protected static Set<String> symbols = new HashSet<String>();
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static Map<String,Map<String,Double>> getData(long investment, String symbol, String startDate, String endDate) {
		//recent added symbol received first
		symbols.add(symbol);//TODO: will cause a bug if stock data is not already available
		Map<String,Map<String,Double>> balances = new TreeMap<String,Map<String,Double>>();
		StockDao stockDao = null; 
		try{	
			stockDao = new StockDao(); 
			Map<String,List<Stock>> data = stockDao.getData(symbol, startDate, endDate);
			if(data != null){
				//https://www.geeksforgeeks.org/iterate-map-java/
				Iterator< Map.Entry<String,List<Stock>>> iterator = data.entrySet().iterator();

				//get first entry
				Map.Entry<String,List<Stock>> entry = iterator.next();
				//e.g {"MSFT":14.2 , "AAPL":10.5 , "GOOGL":10.0}
				Map<String,Double> quantityOfStocks = computeQuantity(investment, entry.getValue());
				// singleDayBalances ==	{symbol:balance} e.g {"MSFT": 5000,"GOOGL": 10000}
				Map<String,Double> singleDayBalances = computeBalances(entry.getValue(), quantityOfStocks);
				balances.put(entry.getKey(), singleDayBalances);

				while(iterator.hasNext() ){
					entry = iterator.next();
					singleDayBalances = computeBalances(entry.getValue(), quantityOfStocks);
					balances.put(entry.getKey(), singleDayBalances);
				}
				return balances;
			}
		}catch(SQLException ex){
			logger.error("SQLException:" + ex.getMessage());	
		}finally{
			stockDao.close();
		}
		return null; //TODO: need a proper return

	}

	private static Map<String,Double> computeBalances( List<Stock> stockList, Map<String, Double> quantities) {
		Map<String,Double> singleDayBalances = new HashMap<String,Double>();
		for (Stock stock : stockList){
			if(stock.getSplit() != 1d){
				double numberOfShares = quantities.get(stock.getTicker());
				numberOfShares = numberOfShares * stock.getSplit();
				//update the quantity corresponding to each stock in quantityOfStocks Map
				quantities.put(stock.getTicker(), numberOfShares);
			}
			double balance = round(quantities.get(stock.getTicker()) * stock.getPrice(), 2);
			singleDayBalances.put(stock.getTicker(), balance);
		}
		return singleDayBalances;
	}

	private static Map<String,Double> computeQuantity (long investment, List<Stock> firstEntry) {
		Map<String,Double> quantityOfStocks = new HashMap<String,Double>();
		for(Stock stock : firstEntry) {
			double quantity = investment / stock.getPrice(); 
			quantityOfStocks.put(stock.getTicker(), quantity);
		}
		return quantityOfStocks;
	}
	//TODO: temporarily return null to avoiding fixing errors for now.
	public static Map<String,String> getSummary(){
		return null;
	}

	// round numbers to nth decimal places
	private static double round(double number,int n){
		double decimal = Math.pow(10, n);
		return Math.round(number * decimal) / decimal; 
	}
}
