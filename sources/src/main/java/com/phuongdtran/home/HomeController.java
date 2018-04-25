package com.phuongdtran.home;

import static com.phuongdtran.util.RequestUtil.*;
import static com.phuongdtran.stock.StockController.getSummary;
import static com.phuongdtran.util.JsonUtil.dataToJson;
import static com.phuongdtran.util.RequestUtil.clientAcceptsHtml;
import static com.phuongdtran.util.RequestUtil.clientAcceptsJson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.phuongdtran.investment.Investment;
import com.phuongdtran.investment.InvestmentController;
import com.phuongdtran.signin.SigninController;
import com.phuongdtran.stock.StockController;
import com.phuongdtran.util.Path;
import com.phuongdtran.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;
public class HomeController {

	public static Route handleHomeDisplay = (Request request, Response response) -> {
		if(SigninController.isSignIn(request, response) && clientAcceptsHtml(request)){
			HashMap<String, Object> model = new HashMap<String, Object>();
			return ViewUtil.render(request, model, Path.Templates.HOME);
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};

	public static Route fetchData = (Request request, Response response) -> {
		if (SigninController.isSignIn(request, response) && clientAcceptsJson(request)) {
			InvestmentController.updateInvestment(request);
			Investment inv = getSessionInvestment(request);
			//TODO: how to pass in symbol(s) from the request to getData()
			Map<String,Map<String,Double>> data = StockController.getData(inv.getBudget(),inv.getStartDate(),inv.getEndDate(),inv.getSymbols());
			
			response.header("Content-Type", "application/json");
			return dataToJson(data);
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};

	public static Route fetchSummary = (Request request, Response response) -> {
		if (clientAcceptsJson(request)){
			Map<String,String> summary = getSummary();
			response.header("Content-Type", "application/json");
			return dataToJson(summary);
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};
}
