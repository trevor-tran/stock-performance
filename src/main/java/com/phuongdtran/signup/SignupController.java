package com.phuongdtran.signup;

import com.google.gson.Gson;
import com.phuongdtran.user.Password;
import com.phuongdtran.user.User;
import com.phuongdtran.user.UserController;
import com.phuongdtran.util.Message;
import org.json.JSONObject;
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
		String passphrase = (String)json.get("password");
		// message sent back to client
		Message message = null;
		if (UserController.exists(username)) {
			message = new Message(false, "Username already exists");
		}else if (!UserController.isComplex(passphrase)) {
			message = new Message(false, "Password is not complex");
		}else{
			User user = new User.Builder(username, new Password(passphrase))
					.firstName(firstName)
					.lastName(lastName)
					.email(email)
					.build();
			UserController.add(user);
			message = new Message(false, "ok");
		}
		return gson.toJson(message);
	};
}
