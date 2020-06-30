package com.phuongdtran.stock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.$Gson$Preconditions;
import com.phuongdtran.util.ConnectionManager;

import java.util.*;

public class StockManager {

    private static StockManager instance = null;

    private StockManager() {

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

    public Map<String, List<Stock>> get(List<String> symbols) {
        Map<String, List<Stock>> data = new HashMap<>();
        for (String symbol: symbols) {
            Map<String, JsonObject> raw = StockAPIHandler.get(symbol);
            if (raw != null) {
                List<Stock> stocks = new ArrayList<>();
                for (Map.Entry<String, JsonObject> entry : raw.entrySet()) {
                    JsonElement price = entry.getValue().getAsJsonPrimitive("price");
                    JsonElement dividend = entry.getValue().getAsJsonPrimitive("dividend");
                    JsonElement split = entry.getValue().getAsJsonPrimitive("split");

                    Stock stock = new Stock(symbol, entry.getKey(), price.getAsDouble(),
                            split.getAsDouble(), dividend.getAsDouble());
                    stocks.add(stock);
                }
                data.put(symbol, stocks);
            }
        }
        return data;
    }
}
