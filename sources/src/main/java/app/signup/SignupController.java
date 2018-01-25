package app.signup;
import java.util.HashMap;
import java.util.Map;

import app.util.Path;
import app.util.ViewUtil;
import spark.*;

public class SignupController {

	public static Route handleSignupDisplay = (Request request, Response response ) -> {
		Map<String,Object> model = new HashMap<String,Object>();
		return ViewUtil.render(request, model, Path.Templates.SIGNUP);
	};
}
