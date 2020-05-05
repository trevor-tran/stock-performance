package com.phuongdtran.stock;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

public interface IStockService {

    Map<String, JsonObject> get(String symbol) throws IOException;
}
