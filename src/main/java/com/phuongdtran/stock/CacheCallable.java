package com.phuongdtran.stock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.phuongdtran.executor.IExecutor;

import java.sql.SQLException;
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

	public CacheCallable(String symbol, IExecutor executor, String startDate, String endDate) {
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
}
