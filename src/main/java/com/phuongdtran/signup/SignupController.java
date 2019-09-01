package com.phuongdtran.signup;

import static com.phuongdtran.user.UserController.addUser;
import static com.phuongdtran.user.UserController.usernameExists;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.phuongdtran.util.Message;

import spark.Request;
import spark.Response;
import spark.Route;


public class SignupController {
	
	private static final Gson gson = new Gson();
	
	public static Route signup = ( Request request, Response response) -> {
		//extracts credentials
		JSONObject json = new JSONObject(request.body());
		String firstName = (String)json.get("firstname");
		String lastName = (String)json.get("lastname");
		String email = (String)json.get("emailaddress");
		String username = (String)json.get("username");
		String password = (String)json.get("password");
		// message sent back to client
		Message message = null;
		if (usernameExists(username)) {
			message = new Message("failure", "Username already exists");
		}else if (!isPasswordComplex(password)) {
			message = new Message("failure", "Password is not complex");
		}else{
			addUser(firstName, lastName, email, username, password);
			message = new Message("success", "ok");
		}
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
