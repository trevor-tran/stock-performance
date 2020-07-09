package com.phuongdtran.stock;

import com.phuongdtran.executor.IExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class QueryCallable implements Callable<Map<String, List<Stock>>> {

    private String symbol;
    private IStockDao stockDao;
    private String from;
    private String to;

    public QueryCallable(String symbol, IExecutor executor, String from, String to) {
        this.symbol = symbol;
        this.stockDao = new StockDao(executor);
        this.from = from;
        this.to = to;
    }

    @Override
    public Map<String, List<Stock>> call() throws Exception {
        try {
            stockDao.open();
            Map<String, List<Stock>> map = new HashMap<>();
            List<Stock> stocks = stockDao.get(symbol, from, to);
            map.put(symbol, stocks);
            return map;
        } finally {
            stockDao.close();
        }
    }
}
