package com.phuongdtran.stock;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

/**
 * It will decide where to get stock data from such as Quandl API or Alpha Vantage API
 */
public class StockAPIHandler {

	private static IStockService stockService;

	/**
	 * Get stock data from a stock API service and parse the data into a Map that each entry has the following format:
	 * { date : {
	 *          "price" : value1,
	 *          "dividend": value2,
	 *          "split": value3
	 *          }
	 * }
	 * where date represents a specific date (such as 2019-1-1)
	 * @param symbol the stock symbol like MSFT
	 * @return stock data if able to get data from the API service. Otherwise, return null.
	 * @throws IOException
	 */
	public static Map<String,JsonObject> get(String symbol, IStockService.OUTPUTSIZE outputsize){
		try {
			if (stockService == null) {
				stockService = new AlphaVantageService();
			}
			return stockService.get(symbol, outputsize);
		}catch (IOException ex) {
			ex.getStackTrace();
			return null;
		}
	}
}
