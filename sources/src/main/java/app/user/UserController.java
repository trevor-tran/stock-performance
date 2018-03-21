package app.user;

import static app.user.UserDao.INVALID_USER_ID;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

public class UserController {
	
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public static boolean usernameExists( String username){
		try{
			return UserDao.getUserId(username) != INVALID_USER_ID;
		}catch(Exception ex){
			logger.error("UserController: usernameExists()." + ex.getStackTrace());
			return false;
		}
	}

	public static String getFirstName(int userId){
		try{
			//TODO: possible to have invalid userId,maybe need "if"
			return UserDao.getUserFirstName(userId);
		}catch(Exception ex){
			logger.error("UserControlle: getFirstName()." + ex.getStackTrace());
			return "";
		}
	}

	/**
	 * @param username
	 * @param password
	 * @return <i>-1</i> if authentication failed, <i>user_id</i> if succeeded
	 * @throws Exception
	 */
	public static int authenticate(String username, String password){
		try{
			if( username.isEmpty() || password.isEmpty() ){
				return INVALID_USER_ID;
			}
			int userId = UserDao.getUserId(username);
			if(userId == INVALID_USER_ID){
				return INVALID_USER_ID;
			}
			else{
				Password signingInUser = UserDao.getSigninCredentials(userId);
				return signingInUser.matches(password) ? userId : INVALID_USER_ID;
			}
		}catch(Exception ex){
			logger.error("UserController:authenticate()." + ex.getStackTrace());
			return INVALID_USER_ID;
		}
	}
	
	/**
	 * look up google account in database, add to database if not already added
	 * @param payload
	 * @return userID in database table
	 */
	public static int addGoogleAndGetId(Payload payload) {
		try{
			//e.g gUserIdentifier == "212312312312321"
			String gUserIdentifier = payload.getSubject();
			// username is gUserIdentifier
			int userId = UserDao.getUserId(gUserIdentifier); 
			if( userId != INVALID_USER_ID){
				return userId;
			}else{
				String email = payload.getEmail();
				String firstName = (String)payload.get("given_name");
				String lastName = (String)payload.get("family_name");
				UserDao.addGoogleUser(gUserIdentifier, firstName, lastName, email);
				return UserDao.getUserId(gUserIdentifier);
			}
		}catch(Exception ex){
			logger.error("UserController: addUser()." + ex.getStackTrace());
			return INVALID_USER_ID;
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
	public static void addUser(String firstName,String lastName,String email, String username,String password){
		try{
			Password newPassword = new Password(password);
			UserDao.addUser(firstName, lastName, email, username, newPassword.getSalt(), newPassword.getHashedPassword());;
		}catch(Exception ex){
			logger.error("UserController: addUser()." + ex.getStackTrace());
		}
	}	
}