package app.stock;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.util.ArrayList;
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

	private static Map<String,String> summary;
	protected static Set<String> symbolsSet = new HashSet<String>();
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e49
	//https://github.com/google/gson/blob/master/UserGuide.md
	public static Map<String,Map<String,Double>> getData(long investment, String symbol, String startDate, String endDate) {
		//recent added symbol received first
		symbolsSet.add(symbol);
		Map<String,Map<String,Double>> balances = new TreeMap<String,Map<String,Double>>();
		try(StockDao stockDao = new StockDao()) {
			Map<String,List<Stock>> data = stockDao.getData(symbol, startDate, endDate);
			//https://www.geeksforgeeks.org/iterate-map-java/
			Iterator< Map.Entry<String,List<Stock>>> iterator = data.entrySet().iterator();

			//compute quantity of each stock based on the first entry
			//e.g {"MSFT":14.2 , "AAPL":10.5 , "GOOGL":10.0}
			Map<String,Double> quantityOfStocks = computeQuantity(investment, iterator.next().getValue());

			while(iterator.hasNext()){
				Map.Entry<String,List<Stock>> entry = iterator.next();
				
				// singleDayBalances ==	{symbol:balance} e.g {"MSFT": 5000,"GOOGL": 10000}
				Map<String,Double> singleDayBalances = computeBalances(entry.getValue(), quantityOfStocks);
				
				balances.put(entry.getKey(), singleDayBalances);
			}
			return balances;
			/*if(rs != null){
				summary = new HashMap<String,String>();
				//MUST use TreeMap here to sort dates
				Map<String,Map<String,Double>> balanceMap = new TreeMap<String,Map<String,Double>>();
				double numberOfShares=0;
				//["2016-12-28","MSFT",62.99,2.0]
				while(rs.next()){
					if(rs.isFirst()){
						//compute number of shares (investment divided by price on starting date)  
						numberOfShares = investment / rs.getDouble("price");
					}

					String date = rs.getString("date_as_id");
					String ticker = rs.getString("symbol"); 
					double price = rs.getDouble("price");
					double split = rs.getDouble("split_ratio");			
					if(split != 1d){
						numberOfShares = numberOfShares * split;
					}
					double balance = round(numberOfShares * price, 2);
					if (balanceMap.containsKey(date)){
						Map<String,Double> value = balanceMap.get(date);
						value.put(ticker, balance);
					}else {
						Map<String,Double> newValue = new HashMap<String,Double>();
						newValue.put(ticker, balance);
						balanceMap.put(date, newValue);
					}
					//summary
					if(rs.isFirst()){
						summary.put("symbol", ticker);
						summary.put("startDate", date);
						summary.put("startPrice", Double.toString(price));
						summary.put("startQuantity",Double.toString(numberOfShares));
						summary.put("startBalance", Double.toString(investment));
					}else if(rs.isLast()){
						summary.put("endDate", date);
						summary.put("endPrice", Double.toString(price));
						summary.put("endQuantity",Double.toString(numberOfShares));
						summary.put("endBalance",Double.toString(balance));
					}
				}
				rs.close();
				return balanceMap;		
				}*/
		}catch(Exception ex){
			logger.error("getData() failed." + ex.getStackTrace());	
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

	public static Map<String,String> getSummary(){
		return summary;
	}

	// round numbers to nth decimal places
	private static double round(double number,int n){
		double decimal = Math.pow(10, n);
		return Math.round(number * decimal) / decimal; 
	}
}
