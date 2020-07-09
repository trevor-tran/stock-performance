package com.phuongdtran.stock;

import com.google.gson.Gson;
import com.phuongdtran.util.Message;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockController {

	private static final Gson gson = new Gson();
	private static final StockManager stockManager = StockManager.getInstance();

	public static Route getData = (Request request, Response response) -> {
		Message message;
		JSONObject json = new JSONObject(request.body());

		String startDate = (String)json.get("start_date");
		String endDate = (String)json.get("end_date");

		JSONArray jsonSymbols = json.getJSONArray("symbols");
		List<String> symbols = new ArrayList<>();
		for (int i = 0; i < jsonSymbols.length(); i++) {
			symbols.add(jsonSymbols.getString(i));
		}

		Map<String, List<Stock>> stockData = stockManager.get(symbols, startDate, endDate);
		if (stockData == null || stockData.size() == 0) {
			message = new Message(false, "Cannot get data");
		} else {
			message = new Message(true, gson.toJson(stockData));
		}
		return gson.toJson(message);
	};
}
