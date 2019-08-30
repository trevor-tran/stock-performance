package com.phuongdtran;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.debug.DebugScreen.enableDebugScreen;

import com.phuongdtran.home.HomeController;
import com.phuongdtran.investment.InvestmentController;
import com.phuongdtran.signin.SigninController;
import com.phuongdtran.signup.SignupController;
import com.phuongdtran.util.DatabaseConnection;
import com.phuongdtran.util.Filters;
import com.phuongdtran.util.Path;
import com.phuongdtran.util.ViewUtil;
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
		get(Path.Web.HOME,  	     					HomeController.handleHomeDisplay);
		post(Path.Web.STOCKDATA,	"application/json", HomeController.fetchData);
		get(Path.Web.SUMMARY,		"application/json", HomeController.fetchSummary);
		post(Path.Web.GOOGLESIGNIN,						SigninController.handleGoogleSignin);
		get(Path.Web.SIGNIN,		"application/json",	SigninController.fetchInvestment);
//		get(Path.Web.SIGNIN, 							SigninController.handleSigninDisplay);
		post(Path.Web.SIGNIN, 							SigninController.handleSignin);
		post(Path.Web.SIGNOUT, 							SigninController.handleSignoutPost);
//		get(Path.Web.SIGNUP, 							SignupController.handleSignupDisplay);
		post(Path.Web.SIGNUP, 							SignupController.handleSignupPost);
		post(Path.Web.REMOVESYMBOL,						InvestmentController.removeSymbol);
		get("*", 					ViewUtil.notFound);
		
		//after(Path.Web.HOME,       Filters.addGzipHeader);
	}
}
