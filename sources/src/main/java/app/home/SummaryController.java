package app.home;
import spark.*;
import static app.util.RequestUtil.clientAcceptsJson;

public class SummaryController {

	public static Route fetchSummary = (Request request, Response response) -> {
		if (clientAcceptsJson(request)){
			
		}
		return null;
	};
}
