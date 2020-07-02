package com.phuongdtran.stock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.phuongdtran.executor.CytherExecutor;
import com.phuongdtran.util.ConnectionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StockManager {

    private static StockManager instance = null;

    private IStockDao stockDao;
    private boolean isOpen;

    private StockManager() {
        stockDao = new StockDao(new CytherExecutor());
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
        Map<String, List<Stock>> data = new HashMap<>();
        Map<String, String> queryDates;
        try {
            if (!isOpen) {
                stockDao.open();
                isOpen = true;
            }

            for (String symbol : symbols) {
                Ticker ticker = stockDao.getSymbolInfo(symbol);
                if (ticker == null) {
                    // get stock data from a financial service and store it in DB
                    cacheStock(symbol);
                } else {
                    // may need to cache stock in compact
//                    if (startDate.compareTo(ticker.getIpo()) < 0) {
//                        cacheStock(symbol);
//                    }
                }
                ticker = stockDao.getSymbolInfo(symbol);
                tickers.add(ticker);
            }
            queryDates = findQueryDates(tickers, startDate, endDate);
            for(String symbol :symbols) {
                List<Stock> singleSymbol = stockDao.get(symbol, queryDates.get("start"), queryDates.get("end"));
                data.put(symbol.toUpperCase(), singleSymbol);
            }
            stockDao.close();
            isOpen = false;
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void cacheStock(String symbol) throws SQLException{
        if (!isOpen) return;

        Map<String, JsonObject> raw = StockAPIHandler.get(symbol);
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

    private Map<String, String> findQueryDates(List<Ticker> tickers, String startDate, String endDate) {
        Map<String,String> queryDates = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
