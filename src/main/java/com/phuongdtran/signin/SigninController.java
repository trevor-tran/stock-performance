package com.phuongdtran.signin;

import com.google.gson.Gson;
import com.phuongdtran.user.UserController;
import com.phuongdtran.util.Message;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class SigninController {
	private static final Gson gson = new Gson();

	public static Route signin = (Request request, Response response) -> {
		Message message;
		JSONObject json = new JSONObject(request.body());
		String username = (String)json.get("username");
		String password = (String)json.get("password");
		if(UserController.authenticate(username, password)) {
			String firstName = UserController.getFirstName(username);
			message = new Message(true, firstName);
		} else {
			message = new Message(false, "Wrong username or password");
		}
		return gson.toJson(message);
	};
}
