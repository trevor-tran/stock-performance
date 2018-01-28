package app.user;
import org.mindrot.jbcrypt.*;

public class UserController {

	public static boolean authenticate(String username, String password) throws Exception{
		if( username.isEmpty() || password.isEmpty() ){
			return false;
		}
		User user = UserDao.getUserCredentials(username);
		if(user == null){
			return false;
		}
		String hashedPassword = BCrypt.hashpw(password, user.getSalt());
		return hashedPassword.equals(user.getHashedPassword());
	}
	/**
	 * Hash password and create a new user in database
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	public static void createUser(String firstName,String lastName,String email, String username,String password) throws Exception{
		String salt = BCrypt.gensalt();
		String hashedPassword = BCrypt.hashpw(password, salt);
		UserDao.setUser(firstName, lastName, email, username, salt, hashedPassword);
	}
}