package app.home;
import static app.stock.StockDao.getSummary;
import static app.util.JsonUtil.dataToJson;
import static app.util.RequestUtil.clientAcceptsHtml;
import static app.util.RequestUtil.clientAcceptsJson;

import java.util.HashMap;
import java.util.Map;

import app.signin.SigninController;
import app.stock.StockDao;
import app.util.Path;
import app.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class HomeController {

	public static Route fetchOneStock = (Request request, Response response) -> {
		if(SigninController.isSignIn(request, response)){
			if (clientAcceptsHtml(request)) {
				HashMap<String, Object> model = new HashMap<>();
				//model.put("stocks", StockDao.getAllStocks());
				return ViewUtil.render(request, model, Path.Templates.HOME);
			}
			else if (clientAcceptsJson(request)) {
				long invest = Long.parseLong(request.queryParams("investment"));
				String start = request.queryParams("start");
				String end = request.queryParams("end");
				String symbol = request.queryParams("symbol");
				Map<String,Map<String,Double>> data = StockDao.getStockData(invest, symbol, start, end);
				//TODO: handle null data
				response.header("Content-Type", "application/json");
				return dataToJson(data);
			}
			return ViewUtil.notAcceptable.handle(request, response);
		}
		return null;
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
