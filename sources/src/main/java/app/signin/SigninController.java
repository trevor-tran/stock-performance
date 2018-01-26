package app.signin;
import java.util.HashMap;
import java.util.Map;

import app.util.Path;
import app.util.ViewUtil;
import spark.*;
import  app.user.UserController;
public class SigninController {
	
	public static Route handleSigninDisplay = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};
	public static Route handelSigninPost = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		if(UserController.authenticate(username, password)){
			//model.put("isAuthenticated", true);
			response.redirect(Path.Web.HOME);
		}
		//else
			//model.put("isAuthenticated", false);
		
		return null;//ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};
}
