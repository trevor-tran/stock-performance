package app.stock;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
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
		symbolsSet.add(symbol);
		try(StockDao stockDao = new StockDao()) {

			ResultSet rs = stockDao.queryStockData(symbol, startDate, endDate);
			if(rs != null){
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
			}
		}catch(Exception ex){
			logger.error("getData() failed." + ex.getStackTrace());	
		}
		return null; //TODO: need a proper return

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
