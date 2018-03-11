package app.home;
import static app.stock.StockDao.getSummary;
import static app.util.JsonUtil.dataToJson;
import static app.util.RequestUtil.clientAcceptsJson;

import java.util.Map;

import app.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class SummaryController {

	public static Route fetchSummary = (Request request, Response response) -> {
		if (clientAcceptsJson(request)){
			Map<String,Object> summary = getSummary();
			response.header("Content-Type", "application/json");
			return dataToJson(summary);
		}
		return ViewUtil.notAcceptable.handle(request, response);
	};
}
