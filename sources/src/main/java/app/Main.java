package app;
import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

import app.signin.SigninController;
import app.signup.SignupController;
import app.util.Filters;
import app.util.Path;
import app.util.ViewUtil;
public class Main {

	public static void main(String[] args) {
		
		//configure Spark
		port(4567);
		enableDebugScreen();
		staticFiles.location("/public");
		
		// Set up before-filters (called before each get/post)
		before("*", Filters.addTrailingSlashes);
		
		// Set up routes
		get("/", (req,res) -> {
			return "Hello world";
		});
		get(Path.Web.SIGNIN, 		SigninController.handleSigninDisplay);
		get(Path.Web.SIGNUP, 		SignupController.handleSignupDisplay);
		get("*", 					ViewUtil.notFound);
		
	}

}
