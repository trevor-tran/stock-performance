package app.home;
import static app.util.JsonUtil.dataToJson;
import static app.util.RequestUtil.clientAcceptsHtml;
import static app.util.RequestUtil.clientAcceptsJson;

import java.util.HashMap;
import java.util.Map;


import app.stock.StockDao;
import app.util.JsonUtil;
import app.util.Path;
import app.util.ViewUtil;
import spark.*;

public class HomeController {


	public static Route fetchAllStocks = (Request request, Response response) -> {
		//LoginController.ensureUserIsLoggedIn(request, response);
		if (clientAcceptsHtml(request)) {
			HashMap<String, Object> model = new HashMap<>();
			//model.put("stocks", StockDao.getAllStocks());
			return ViewUtil.render(request, model, Path.Templates.HOME);
		}
		if (clientAcceptsJson(request)) {
			Map<String,Map<String,Float>> data;
			//String money = request.queryParams("money");
			String start = request.queryParams("start");
			String end = request.queryParams("end");
			String symbol = request.queryParams("symbol");
			data = StockDao.getStockData(symbol, start, end);
			response.header("Content-Type", "application/json");
			return dataToJson(data);
		}
		
		return ViewUtil.notAcceptable.handle(request, response);
	};

	public static Route fetchRawStockData = (Request request, Response response) -> {
		// TODO: validate JS session!
		//LoginController.ensureUserIsLoggedIn(request, response);
		if (clientAcceptsJson(request)) {

			String symbol =",";
			symbol += request.queryParams("symbol");
			Map<String,Map<String,Float>> data = StockDao.getStockData("AAPL"+symbol, "2017-1-3", "2018-2-14");

			response.header("Content-Type", "application/json");
			return dataToJson(data);
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};
	/* public static Route handleStockRequest = (Request request, Response response) -> {
		 Map<String,Object> model = new HashMap<String,Object>();

	   };*/
}
