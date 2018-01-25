package app.home;
import java.util.HashMap;
import java.util.Map;

import app.util.Path;
import app.util.ViewUtil;
import spark.*;

public class HomeController {
	private static final String apiKey = "LSHfJJyvzYHUyU9jHpn6"; 
	public static Route handleHomeDisplay = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		
		return ViewUtil.render(request, model, Path.Templates.HOME);
	};
}
