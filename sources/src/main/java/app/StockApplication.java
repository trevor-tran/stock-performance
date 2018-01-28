package app;
import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

import app.home.HomeController;
import app.signin.SigninController;
import app.signup.SignupController;
import app.util.DatabaseConnection;
import app.util.Filters;
import app.util.Path;
import app.util.ViewUtil;
public class StockApplication {

	public static void main(String[] args) {
		
		DatabaseConnection.initialize();
		
		//configure Spark
		port(4567);
		enableDebugScreen();
		staticFiles.location("/public");
		
		// Set up before-filters (called before each get/post)
		before("*", Filters.addTrailingSlashes);
		
		// Set up routes
		get("/hello/", (req,res) -> {
			return "Hello world";
		});
		get(Path.Web.HOME, 			HomeController.handleHomeDisplay);
		get(Path.Web.SIGNIN, 		SigninController.handleSigninDisplay);
		post(Path.Web.SIGNIN, 		SigninController.handelSigninPost);
		get(Path.Web.SIGNUP, 		SignupController.handleSignupDisplay);
		post(Path.Web.SIGNUP, 		SignupController.handleSignupPost);
		get("*", 					ViewUtil.notFound);
		
	}

}
