package com.phuongdtran.signup;

import static com.phuongdtran.user.UserController.addUser;
import static com.phuongdtran.user.UserController.usernameExists;
import static com.phuongdtran.util.RequestUtil.getSessionUserId;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.phuongdtran.util.Message;
import com.phuongdtran.util.Path;
import com.phuongdtran.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SignupController {
	
	private static Gson gson = new Gson();

	public static Route handleSignupDisplay = (Request request, Response response ) -> {
		System.out.println("in dipslay");
		String currentUserId = getSessionUserId(request);
		//allow to sign up a new account when no user has not signed in
		if(currentUserId == null){
			Map<String,Object> model = new HashMap<String,Object>();
			return ViewUtil.render(request, model, Path.Templates.SIGNUP);
		}
		//TODO:Should I do anything here if currentUserId is not null
		return null;
	};
	
	public static Route handleSignupPost = ( Request request, Response response) -> {
//		Map<String,Object> model = new HashMap<String,Object>();
		//extracts credentials
		JSONObject json = new JSONObject(request.body());
		String firstName = (String)json.get("firstname");
		String lastName = (String)json.get("lastname");
		String email = (String)json.get("emailaddress");
		String username = (String)json.get("username");
		String password = (String)json.get("password");
//		String reenterPassword = request.queryParams("reenterpassword"); 
		// message sent back to client
		Message message = null;
		if (usernameExists(username)) {
//			model.put("usernameExists", true);
			message = new Message("failure", "Username already exists");
//		}else if(! Objects.equals(password, reenterPassword)) {
//			model.put("passwordNotMatch",true);
//			return ViewUtil.render(request, model, Path.Templates.SIGNUP);
		}else if (!isPasswordComplex(password)) {
//			model.put("passwordNotComplex", true);
			message = new Message("failure", "Password is not complex");
		}
		//TODO: Email Address Validation
		else{
			addUser(firstName, lastName, email, username, password);
//			model.put("signupSucceeded", true);
			message = new Message("success", "ok");
		}
//		return ViewUtil.render(request, model, Path.Templates.SIGNUP);
		System.out.println(gson.toJson(message));
		return gson.toJson(message);
	};
	
	/**
	 * Check if password contains at least one lower case letter,
	 * one upper case letter, one number, and length of at least eight.
	 * @see <a href ="http://stackoverflow.com/questions/3802192/regexp-java-for-password-validation">link</a>
	 * @param password
	 * @return <i>true</i> if password is complex, <i>false</i> if not
	 */
	private static boolean isPasswordComplex(String password) {
		Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
}
