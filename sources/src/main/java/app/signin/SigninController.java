package app.signin;

import static app.user.UserController.authenticate;
import static app.user.UserController.getFirstName;
import static app.user.UserDao.INVALID_USER_ID;
import static app.user.UserDao.addGoogleUser;
import static app.user.UserDao.getUserId;
import static app.util.RequestUtil.getQueryPassword;
import static app.util.RequestUtil.getQueryUsername;
import static app.util.RequestUtil.getSessionUserId;
import static app.util.RequestUtil.getQueryGoogleToken;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


import app.util.Path;
import app.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class SigninController {

	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	//the client ID you created for your app in the Google Developers Console
	//https://developers.google.com/identity/sign-in/web/sign-in
	private static final String APP_CLIENT_ID = "60643896300-nmj8u9au70jb4512hfs4ao2254e4j0t2.apps.googleusercontent.com";

	public static Route handleSigninDisplay = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};

	public static Route handleSigninPost = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		String username = getQueryUsername(request);
		String password = getQueryPassword(request);
		if ( username.contains(" ")){
			model.put("usernameContainsSpace", true);
		}
		int userId = authenticate(username, password);
		if(userId == INVALID_USER_ID){
			model.put("authenticationFailed", true);
		}else{
			request.session().attribute("firstName", getFirstName(userId));
			request.session().attribute("currentUserId", userId);
			response.redirect(Path.Web.HOME);
		}
		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};

	//https://developers.google.com/identity/sign-in/web/backend-auth
	public static Route handleGoogleSignin = (Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		//after sign in successfully with Google account, 
		//an idToken will be sent to server to verify the account and obtain personal information
		String idToken = getQueryGoogleToken(request);
		if( idToken != null) {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
					.setAudience(Collections.singletonList(APP_CLIENT_ID))
					.build();
			GoogleIdToken gToken = verifier.verify(idToken);
			if( gToken != null){
				Payload payload = gToken.getPayload();
				//similar to native users, sign in by Google account saved in database with unique id
				int userId = getGoogleUserId(payload);
				request.session().attribute("currentUserId", userId);
				request.session().attribute("firstName", getFirstName(userId));
				response.redirect(Path.Web.HOME);
			}
		}
		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};

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

	/**
	 * look up google account in database, add to database if not already added
	 * @param payload
	 * @return userID in database table
	 */
	private static int getGoogleUserId(Payload payload){
		//use this as username in database. e.g gUserIdentifier == "212312312312321"
		String gUserIdentifier = payload.getSubject();
		int userId = getUserId(gUserIdentifier); 
		if( userId != INVALID_USER_ID){
			return userId;
		}else{
			String email = payload.getEmail();
			String firstName = (String)payload.get("given_name");
			String lastName = (String)payload.get("family_name");
			// username is gUserIdentifier
			addGoogleUser( gUserIdentifier, firstName, lastName, email);
			return getUserId(gUserIdentifier);
		}
	}
}
