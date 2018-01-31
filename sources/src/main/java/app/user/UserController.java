package app.user;
import org.mindrot.jbcrypt.*;

public class UserController {
	public static boolean usernameExists( String username) throws Exception{
		return (UserDao.getUserId(username) != -1) ? true : false;
	}
	
	public static String getFirstName(int userId) throws Exception{
		//TODO: possible to have invalid userId,maybe need "if"
		return UserDao.getUserFirstName(userId);
	}
	/**
	 * 
	 * @param username
	 * @param password
	 * @return <i>-1</i> if authentication failed, <i>user_id</i> if succeeded
	 * @throws Exception
	 */
	public static int authenticate(String username, String password) throws Exception{
		if( username.isEmpty() || password.isEmpty() ){
			return -1;
		}
		int userId = UserDao.getUserId(username);
		if(userId == -1){
			return -1;
		}
		else{
			SigninCredentials signinInfo = UserDao.getSigninCredentials(userId);
			String hashedEnterPassword = BCrypt.hashpw(password, signinInfo.getSalt());
			return (hashedEnterPassword.equals(signinInfo.getHashedPassword())) ? userId : -1; 
		}
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