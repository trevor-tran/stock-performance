package app.user;
import org.mindrot.jbcrypt.*;

public class UserController {

	public static boolean authenticate(String username, String password) throws Exception{
		if( username.isEmpty() || password.isEmpty() ){
			return false;
		}
		User user = UserDao.getUserByName(username);
		if(user == null){
			return false;
		}
		String hashedPassword = BCrypt.hashpw(password, user.getSalt());
		return hashedPassword.equals(user.getHashedPassword());
	}
	
}
