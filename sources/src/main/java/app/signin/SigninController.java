package app.signin;

import java.util.HashMap;
import java.util.Map;

import app.util.Path;
import app.util.ViewUtil;
import spark.*;
import static app.user.UserController.*;
public class SigninController {

	public static Route handleSigninDisplay = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};
	public static Route handelSigninPost = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		if ( username.contains(" ")){
			model.put("usernameContainsSpace", true);
			//return ViewUtil.render(request, model, Path.Templates.SIGNIN);
		}
		int userId = authenticate(username, password);
		if(userId == -1){
			model.put("authenticationFailed", true);
			//return ViewUtil.render(request, model, Path.Templates.SIGNIN);
			
		}
		else{
			request.session().attribute("firstName", getFirstName(userId));
			request.session().attribute("currentUserId", userId);
			response.redirect(Path.Web.HOME);
			//return ViewUtil.render(request, model, Path.Templates.HOME);
		}
		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
		//return null;//ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};
	public static Route handleSignoutPost = (Request request, Response response) -> {
		request.session().removeAttribute("currentUserId");
		request.session().removeAttribute("firstName");
		//request.session().attribute("SignedOut", true);
		response.redirect(Path.Web.HOME);
		return null;
	};

	public static boolean IsSignIn(Request request, Response response) throws Exception {
		if (request.session().attribute("currentUserId") == null) {
			// the current username/email may be passed to us in a cookie
			//String userCookie = request.cookie("currentUser");
			//if (userCookie != null && userCookie.length() > 0) {
			// save the username in a session variable with the same key name
			//request.session().attribute("currentUser", userCookie);
			//handleGoogleSignIn(userCookie);
			return false;
		}
		return true;
	};
}
