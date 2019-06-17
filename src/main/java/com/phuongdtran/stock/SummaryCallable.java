package com.phuongdtran.stock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class SummaryCallable implements Callable<Map<String,String>>{
	
	private Map<String,List<Stock>> data;
	private long budget;
	private Map<String,String> summary;
	
	public SummaryCallable(Map<String,List<Stock>> data, long budget){
		this.data = data;
		this.budget = budget;
		summary = new HashMap<String,String>();
	}
	@Override
	public Map<String, String> call() throws Exception {
		Iterator< Map.Entry<String,List<Stock>>> iterator = data.entrySet().iterator();
		Map.Entry<String, List<Stock>> firstEntry = iterator.next();
		return null;
	}
	
	private Map<String,Double> computeQuantity (long investment, List<Stock> firstEntry) {
		Map<String,Double> quantityOfStocks = new HashMap<String,Double>();
		for(Stock stock : firstEntry) {
			double quantity = investment / stock.getPrice(); 
			quantityOfStocks.put(stock.getTicker(), quantity);
		}
		return quantityOfStocks;
	}

}
