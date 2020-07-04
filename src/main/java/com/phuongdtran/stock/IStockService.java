package com.phuongdtran.stock;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

public interface IStockService {
    enum OUTPUTSIZE {
        FULL, COMPACT
    }

    Map<String, JsonObject> get(String symbol, OUTPUTSIZE outputsize) throws IOException;
}
