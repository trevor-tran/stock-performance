package app.home;
import static app.util.JsonUtil.dataToJson;
import static app.util.RequestUtil.clientAcceptsHtml;
import static app.util.RequestUtil.clientAcceptsJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import app.stock.PriceData;
import app.stock.Stock;
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
	         return dataToJson(StockDao.getStockData("AAPL", "2018-1-29", "2018-2-2"));
	      }
	      return ViewUtil.notAcceptable.handle(request, response);
	   };
	
	   public static Route fetchRawStockData = (Request request, Response response) -> {
	      // TODO: validate JS session!
	      //LoginController.ensureUserIsLoggedIn(request, response);
	      if (clientAcceptsJson(request)) {
	         Map<String,List<PriceData>> data = StockDao.getStockData("AAPL", "2018-1-29", "2018-2-2");
	         response.header("Content-Type", "application/json");
	         return JsonUtil.dataToJson(data);
	      }
	      return ViewUtil.notAcceptable.handle(request, response);
	   };
}
