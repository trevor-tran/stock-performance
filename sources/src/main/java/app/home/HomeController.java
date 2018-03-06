package app.home;
import static app.util.JsonUtil.dataToJson;
import static app.util.RequestUtil.clientAcceptsHtml;
import static app.util.RequestUtil.clientAcceptsJson;

import java.util.HashMap;
import java.util.Map;

import app.stock.StockDao;
import app.util.Path;
import app.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class HomeController {


	public static Route fetchAllStocks = (Request request, Response response) -> {
		//LoginController.ensureUserIsLoggedIn(request, response);
		if (clientAcceptsHtml(request)) {
			HashMap<String, Object> model = new HashMap<>();
			//model.put("stocks", StockDao.getAllStocks());
			return ViewUtil.render(request, model, Path.Templates.HOME);
		}
		else if (clientAcceptsJson(request)) {
			//String money = request.queryParams("money");
			String start = request.queryParams("start");
			String end = request.queryParams("end");
			String symbol = request.queryParams("symbol");
			Map<String,Map<String,Float>> data = StockDao.getStockData(symbol, start, end);
			//TODO: handle null data
			response.header("Content-Type", "application/json");
			return dataToJson(data);
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};
}
