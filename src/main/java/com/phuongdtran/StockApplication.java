package com.phuongdtran;

import com.phuongdtran.signin.SigninController;
import com.phuongdtran.signup.SignupController;
import com.phuongdtran.stock.StockController;
import com.phuongdtran.util.DatabaseConnection;
import com.phuongdtran.util.Filters;
import com.phuongdtran.util.Neo4jConnection;
import com.phuongdtran.util.Path;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;


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
		post(Path.Web.DATA,	"application/json", StockController.getData);
		post(Path.Web.SIGNIN, 							SigninController.signin);
		post(Path.Web.SIGNUP, 							SignupController.signup);
	}
}
