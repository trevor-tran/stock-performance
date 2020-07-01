package com.phuongdtran.stock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.phuongdtran.executor.CytherExecutor;
import com.phuongdtran.util.ConnectionManager;

import java.sql.SQLException;
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
                }
            }
            Map<String, List<Stock>> data = new HashMap<>();
            for(String symbol :symbols) {
                List<Stock> singleSymbol = stockDao.get(symbol, startDate, endDate);
                data.put(symbol.toUpperCase(), singleSymbol);
            }
//            for (String symbol : symbols) {
//                Map<String, JsonObject> raw = StockAPIHandler.get(symbol);
//                if (raw != null) {
//                    List<Stock> stocks = new ArrayList<>();
//                    for (Map.Entry<String, JsonObject> entry : raw.entrySet()) {
//                        JsonElement price = entry.getValue().getAsJsonPrimitive("price");
//                        JsonElement dividend = entry.getValue().getAsJsonPrimitive("dividend");
//                        JsonElement split = entry.getValue().getAsJsonPrimitive("split");
//
//                        Stock stock = new Stock(symbol, entry.getKey(), price.getAsDouble(),
//                                split.getAsDouble(), dividend.getAsDouble());
//                        stocks.add(stock);
//                    }
//                    data.put(symbol, stocks);
//                }
//            }
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
}
