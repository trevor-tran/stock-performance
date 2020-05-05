package com.phuongdtran;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.options;
import static spark.Spark.staticFiles;
import static spark.debug.DebugScreen.enableDebugScreen;

import com.phuongdtran.home.HomeController;
import com.phuongdtran.investment.InvestmentController;
import com.phuongdtran.signin.SigninController;
import com.phuongdtran.signup.SignupController;
import com.phuongdtran.stock.StockController;
import com.phuongdtran.util.*;


public class StockApplication {

	public static void main(String[] args) {


		DatabaseConnection.initialize();
		Neo4jConnection.initialize();

		//configure Spark
		port(4567);
		enableDebugScreen();
		staticFiles.location("/public");
		//https://gist.github.com/saeidzebardast/e375b7d17be3e0f4dddf
		options("/*",
		        (request, response) -> {

		            String accessControlRequestHeaders = request
		                    .headers("Access-Control-Request-Headers");
		            if (accessControlRequestHeaders != null) {
		                response.header("Access-Control-Allow-Headers",
		                        accessControlRequestHeaders);
		            }

		            String accessControlRequestMethod = request
		                    .headers("Access-Control-Request-Method");
		            if (accessControlRequestMethod != null) {
		                response.header("Access-Control-Allow-Methods",
		                        accessControlRequestMethod);
		            }

		            return "OK";
		        });

		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
		// Set up before-filters (called before each get/post)
		before("*", Filters.addTrailingSlashes);

		// Set up routes
//		get(Path.Web.HOME,  	     					HomeController.handleHomeDisplay);
		post(Path.Web.DATA,	"application/json", StockController.getData);
//		get(Path.Web.SUMMARY,		"application/json", HomeController.fetchSummary);
//		post(Path.Web.GOOGLESIGNIN,						SigninController.handleGoogleSignin);
		get(Path.Web.SIGNIN,		"application/json",	SigninController.fetchInvestment);
		post(Path.Web.SIGNIN, 							SigninController.signin);
		post(Path.Web.SIGNOUT, 							SigninController.handleSignoutPost);
		post(Path.Web.SIGNUP, 							SignupController.signup);
		post(Path.Web.REMOVESYMBOL,						InvestmentController.removeSymbol);
//		get("*", 					ViewUtil.notFound);

		//after(Path.Web.HOME,       Filters.addGzipHeader);
	}
}
