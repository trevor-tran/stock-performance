package app;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.debug.DebugScreen.enableDebugScreen;

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
		get(Path.Web.HOME,       HomeController.fetchOneStock);
		get(Path.Web.HOME, "application/json", HomeController.fetchOneStock);
		get(Path.Web.SUMMARY, "application/json", HomeController.fetchSummary);
		get(Path.Web.SIGNIN, 		SigninController.handleSigninDisplay);
		post(Path.Web.SIGNIN, 		SigninController.handleSigninPost);
		post(Path.Web.SIGNOUT, 		SigninController.handleSignoutPost);
		get(Path.Web.SIGNUP, 		SignupController.handleSignupDisplay);
		post(Path.Web.SIGNUP, 		SignupController.handleSignupPost);
		
		get("*", 					ViewUtil.notFound);
		
		//after(Path.Web.HOME,       Filters.addGzipHeader);
	}
}
