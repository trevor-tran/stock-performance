package com.phuongdtran.stock;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
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

import com.google.common.base.Objects;
import com.phuongdtran.investment.Investment;

public class StockController {

	private static List<SummaryAttribute> summaryList;
	private static List<SummaryAttribute> endList;
	private static Map<String,List<SummaryAttribute>> summary;
	private static String start;//from
	private static String end;//to
	//protected Set<String> symbols;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static Map<String,Map<String,Double>> getData(long budget, String startDate,String endDate, Set<String> symbols) {
		summaryList = new ArrayList<SummaryAttribute>();
		summary = new TreeMap<String, List<SummaryAttribute>>();
		Map<String,Map<String,Double>> balances = new TreeMap<String,Map<String,Double>>();	 
		Map<String,List<Stock>> data = StockDao.getData(symbols, startDate, endDate);
		if(data != null){
			//https://www.geeksforgeeks.org/iterate-map-java/
			Iterator< Map.Entry<String,List<Stock>>> iterator = data.entrySet().iterator();

			//get first entry
			Map.Entry<String,List<Stock>> entry = iterator.next();
			start = entry.getKey();
			//e.g {"MSFT":14.2 , "AAPL":10.5 , "GOOGL":10.0}
			Map<String,Double> quantityOfStocks = computeQuantity(budget, entry.getValue());
			// singleDayBalances ==	{symbol:balance} e.g {"MSFT": 5000,"GOOGL": 10000}
			Map<String,Double> oneDayBalances = computeBalances(entry.getValue(), quantityOfStocks);
			balances.put(entry.getKey(), oneDayBalances);

			while( iterator.hasNext()){
				entry = iterator.next();
				oneDayBalances = computeBalances(entry.getValue(), quantityOfStocks);
				balances.put(entry.getKey(), oneDayBalances);
			}
			//"entry" is now pointed to last element in "data"
			end = entry.getKey();
			for(Stock stock : entry.getValue()){
				for(SummaryAttribute sa : summaryList){
					if( Objects.equal(stock.getTicker(), sa.getSymbol())){
						sa.setEndPrice(stock.getPrice());
					}
				}
			}
		}
		StockDao.close();
		return balances;
	}

	private static Map<String,Double> computeBalances( List<Stock> stockList, Map<String, Double> quantities) {
		Map<String,Double> singleDayBalances = new HashMap<String,Double>();
		for (Stock stock : stockList){
			if(stock.getSplit() != 1d){
				double numberOfShares = quantities.get(stock.getTicker());
				numberOfShares = numberOfShares * stock.getSplit();
				//update the quantity corresponding to each stock in quantityOfStocks
				quantities.put(stock.getTicker(), numberOfShares);
			}
			double balance = round(quantities.get(stock.getTicker()) * stock.getPrice(), 2);
			singleDayBalances.put(stock.getTicker(), balance);
		}
		return singleDayBalances;
	}

	private static Map<String,Double> computeQuantity (long budget, List<Stock> firstEntry) {
		Map<String,Double> quantityOfStocks = new HashMap<String,Double>();
		for(Stock stock : firstEntry) {
			double quantity = budget / stock.getPrice();
			quantityOfStocks.put(stock.getTicker(), quantity);
			
			//summary
			SummaryAttribute sa = new SummaryAttribute();
			sa.setSymbol(stock.getTicker());
			sa.setStartPrice(stock.getPrice());
			sa.setStartBalance(budget);
			sa.setStartQuantity(quantity);
			summaryList.add(sa);
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
