package app.signup;

import static app.user.UserController.createUser;
import static app.user.UserController.usernameExists;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.util.Path;
import app.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class SignupController {

	public static Route handleSignupDisplay = (Request request, Response response ) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		return ViewUtil.render(request, model, Path.Templates.SIGNUP);
	};
	
	public static Route handleSignupPost = ( Request request, Response response) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		//extracts credentials
		String firstName = request.queryParams("firstname");
		String lastName = request.queryParams("lastname");
		String email = request.queryParams("emailaddress");
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		String reenterPassword = request.queryParams("reenterpassword"); 
		if (usernameExists(username)) {
			model.put("usernameExists", true);
		} 
		else if(! Objects.equals(password, reenterPassword)) {
			model.put("passwordNotMatch",true);
			return ViewUtil.render(request, model, Path.Templates.SIGNUP);
		} 
		else if (!isPasswordComplex(password)) {
			model.put("passwordNotComplex", true);
		}
		//TODO: Email Address Validation
		else{
			createUser(firstName, lastName, email, username, password);
			model.put("signupSucceeded", true);
		}
		return ViewUtil.render(request, model, Path.Templates.SIGNUP);
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
