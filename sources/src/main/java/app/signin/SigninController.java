package app.signin;

import static app.user.UserController.authenticate;
import static app.user.UserController.getFirstName;
import static app.util.RequestUtil.getQueryPassword;
import static app.util.RequestUtil.getQueryUsername;
import static app.util.RequestUtil.getSessionUserId;

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
		if(userId == -1){
			model.put("authenticationFailed", true);
		}else{
			request.session().attribute("firstName", getFirstName(userId));
			request.session().attribute("currentUserId", userId);
			response.redirect(Path.Web.HOME);
		}
		return ViewUtil.render(request, model, Path.Templates.SIGNIN);
	};

	public static Route handleSignoutPost = (Request request, Response response) -> {
		request.session().removeAttribute("currentUserId");
		request.session().removeAttribute("firstName");
		response.redirect(Path.Web.HOME);
		return null;
	};
	
	/**
	 * Check if user uses native sign-in or google sign-in.
	 * @see <a href="https://developers.google.com/identity/sign-in/web/backend-auth">developers.google.com</a> 
	 * @see	<a href="https://cloud.google.com/java/getting-started/authenticate-users">cloud.google.com</a>
	 */
	public static boolean isSignIn(Request request, Response response) throws Exception {
		String googleToken = request.cookie("currentToken");
		if ( getSessionUserId(request) == null && googleToken!=null) {
			// the current Google id token may be passed in as a cookie
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
					.setAudience(Collections.singletonList(APP_CLIENT_ID))
					.build();
			GoogleIdToken idToken = verifier.verify(googleToken);
			if(idToken != null){
				Payload payload = idToken.getPayload();
				System.out.println(payload.getEmail());
				System.out.println( (String)payload.get("given_name"));
				System.out.println( (String)payload.get("family_name"));
				return true;
			}
			response.redirect(Path.Web.SIGNIN);
			return false;
		}
		return true;
	};
}
