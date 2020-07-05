package com.phuongdtran.stock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.phuongdtran.executor.CytherExecutor;
import com.phuongdtran.util.ConnectionManager;
import com.phuongdtran.util.ThreadPool;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class StockManager {

    private static StockManager instance = null;

    private IStockDao stockDao;
    private boolean isOpen;
    private DateTimeFormatter formatter;

    private StockManager() {
        stockDao = new StockDao(new CytherExecutor());
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public static StockManager getInstance() {
        if(instance == null){
            synchronized (ConnectionManager.class) {
                if(instance == null)
                    instance = new StockManager();
            }
        }
        return instance;
    }

    public Map<String, List<Stock>> get(List<String> symbols, String startDate, String endDate) {
        List<Ticker> tickers = new ArrayList<>();
        List<Future<Ticker>> futures = new ArrayList<>();
        Map<String, List<Stock>> data = new HashMap<>();
        Map<String, String> queryDates;
        try {
            if (!isOpen) {
                stockDao.open();
                isOpen = true;
            }

            ThreadPoolExecutor executor = ThreadPool.getInstance();
            List<CacheCallable> callables = new ArrayList<>();
            for (String symbol : symbols) {
                callables.add(new CacheCallable(symbol, new CytherExecutor(), startDate, endDate));
            }
            futures = executor.invokeAll(callables);
            for (Future<Ticker> t : futures) {
                tickers.add(t.get());
            }
            queryDates = findQueryDates(tickers, startDate, endDate);
            for(String symbol :symbols) {
                List<Stock> singleSymbol = stockDao.get(symbol, queryDates.get("start"), queryDates.get("end"));
                data.put(symbol.toUpperCase(), singleSymbol);
            }
//            stockDao.close();
//            isOpen = false;
            return data;
        } catch (SQLException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private void cacheStockFull(String symbol) throws SQLException{
//        if (!isOpen) return;
//        cacheStock(symbol, IStockService.OUTPUTSIZE.FULL);
//
//    }
//
//    private void cacheStockCompact(String symbol) throws SQLException {
//        if (!isOpen) return;
//        cacheStock(symbol, IStockService.OUTPUTSIZE.COMPACT);
//    }
//
//    private void cacheStock(String symbol, IStockService.OUTPUTSIZE outputsize) throws SQLException {
//        Map<String, JsonObject> raw = StockAPIHandler.get(symbol, outputsize);
//        if (raw != null) {
//            for (Map.Entry<String, JsonObject> entry : raw.entrySet()) {
//                JsonElement price = entry.getValue().getAsJsonPrimitive("price");
//                JsonElement dividend = entry.getValue().getAsJsonPrimitive("dividend");
//                JsonElement split = entry.getValue().getAsJsonPrimitive("split");
//
//                Stock stock = new Stock(symbol, entry.getKey(), price.getAsDouble(),
//                        split.getAsDouble(), dividend.getAsDouble());
//                stockDao.add(stock);
//            }
//        }
//    }

    /**
     * Each stock may have different ipo dates and, possibly, delisting dates.
     * This function find out what is a mutual range of dates that can query data for all stock symbols
     * @param tickers
     * @param startDate requested start date from client
     * @param endDate requested end date from client
     * @return Java Map containing two keys ("start", "end")
     */
    private Map<String, String> findQueryDates(List<Ticker> tickers, String startDate, String endDate) {
        Map<String,String> queryDates = new HashMap<>();

        String mutualIpo = "1792-05-17";
        String mutualDelisting = LocalDate.now().format(formatter);

        for (Ticker ticker : tickers) {
            if (mutualIpo.compareTo(ticker.getIpo()) < 0) {
                mutualIpo = ticker.getIpo();
            }
            if (mutualDelisting.compareTo(ticker.getDelisting()) > 0) {
                mutualDelisting = ticker.getDelisting();
            }
        }

        queryDates.put("start", startDate);
        queryDates.put("end", endDate);
        if (startDate.compareTo(mutualIpo) < 0) {
            queryDates.put("start", mutualIpo);
        }
        if (endDate.compareTo(mutualDelisting) > 0) {
            queryDates.put("end", mutualDelisting);
        }

        return queryDates;
    }
}
