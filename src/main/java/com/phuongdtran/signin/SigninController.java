package com.phuongdtran.signin;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.phuongdtran.investment.Investment;
import com.phuongdtran.investment.InvestmentController;
import com.phuongdtran.user.UserController;
import com.phuongdtran.util.JsonUtil;
import com.phuongdtran.util.Message;
import com.phuongdtran.util.Path;
import com.phuongdtran.util.ViewUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.phuongdtran.util.RequestUtil.getQueryGoogleToken;
import static com.phuongdtran.util.RequestUtil.getSessionUserId;
public class SigninController {
	private static final Gson gson = new Gson();
	private static final int INVALID_USER_ID = -1;

	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	//the client ID you created for your app in the Google Developers Console
	//https://developers.google.com/identity/sign-in/web/sign-in
	private static final String APP_CLIENT_ID = "60643896300-nmj8u9au70jb4512hfs4ao2254e4j0t2.apps.googleusercontent.com";

//	public static Route handleSigninDisplay = (Request request, Response response) -> {
//		Map<String,Object> model = new HashMap<String,Object>();
//		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
//	};

	public static Route signin = (Request request, Response response) -> {
//		Map<String,Object> model = new HashMap<String,Object>();
		Message message = null;
		JSONObject json = new JSONObject(request.body());
		String username = (String)json.get("username");
		String password = (String)json.get("password");
		if(UserController.authenticate(username, password)) {
			String firstName = UserController.getFirstName(username);
			message = new Message(true, firstName);
		} else {
			message = new Message(false, "Wrong username or password");
//			return null;
		}
		return gson.toJson(message);
//		if (username.contains(" ")){
//			model.put("usernameContainsSpace", true);
//		}
//		int userId = UserController.authenticate(username, password);
//		if(userId == INVALID_USER_ID){
//			model.put("authenticationFailed", true);
//		}else{
//			String firstName = UserController.getFirstName(userId);
//			request.session().attribute("firstname", StringUtils.capitalize(firstName));
//			request.session().attribute("userid", userId);
//			response.redirect(Path.Web.HOME);
//		}
//		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};

	public static Route fetchInvestment = (Request request, Response response) -> {
		String userId = getSessionUserId(request);
		Investment investment = InvestmentController.getInvestment( Integer.parseInt(userId));
		request.session().attribute("investment",investment);
		return JsonUtil.dataToJson(investment);
	};

	//https://developers.google.com/identity/sign-in/web/backend-auth
//	public static Route handleGoogleSignin = (Request request, Response response) -> {
//		//verify the id token obtained from front-end
//		String idToken = getQueryGoogleToken(request);
//		if( idToken != null) {
//			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
//					.setAudience(Collections.singletonList(APP_CLIENT_ID))
//					.build();
//
//			GoogleIdToken gToken = verifier.verify(idToken);
//
//			if( gToken != null){
//				Payload payload = gToken.getPayload();
//				//similar to native users, sign in by Google account saved in database with unique id
//				int userId = UserController.addGoogleAndGetId(payload);
//				if(userId != INVALID_USER_ID){
//					request.session().attribute("currentUserId", userId);
//					String firstName = UserController.getFirstName(userId);
//					request.session().attribute("firstName", StringUtils.capitalize(firstName));
//					response.redirect(Path.Web.HOME,200);
//					return null;
//				}
//			}
//		}
//		Map<String,Object> model = new HashMap<String,Object>();
//		model.put("authenticationFailed", true);
//		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
//	};

	public static Route handleSignoutPost = (Request request, Response response) -> {
		request.session().removeAttribute("currentUserId");
		request.session().removeAttribute("firstName");
		response.redirect(Path.Web.SIGNIN);
		return null;
	};

	public static boolean isSignIn(Request request, Response response) throws Exception {
		if ( getSessionUserId(request) == null) {
			response.redirect(Path.Web.SIGNIN);
			return false;
		}
		return true;
	};
}
