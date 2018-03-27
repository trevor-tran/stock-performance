package app.user;

import static app.user.UserDao.INVALID_USER_ID;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

public class UserController {

	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static boolean usernameExists( String username){
		try( UserDao userDao = new UserDao()) {
			return userDao.getUserId(username) != INVALID_USER_ID;
		}catch(Exception ex){
			logger.error("UserController: usernameExists()." + ex.getMessage());
			return false;
		}
	}

	public static String getFirstName(int userId){
		try( UserDao userDao = new UserDao()) {
			//TODO: possible to have invalid userId,maybe need "if"
			return userDao.getUserFirstName(userId);
		}catch(Exception ex){
			logger.error("UserControlle: getFirstName()." + ex.getMessage());
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
		try( UserDao userDao = new UserDao()) {
			if( username.isEmpty() || password.isEmpty() ){
				return INVALID_USER_ID;
			}
			int userId = userDao.getUserId(username);
			if(userId == INVALID_USER_ID){
				return INVALID_USER_ID;
			}
			else{
				Password signingInUser = userDao.getSigninCredentials(userId);
				return signingInUser.matches(password) ? userId : INVALID_USER_ID;
			}
		}catch(Exception ex){
			logger.error("UserController:authenticate()." + ex.getMessage());
			return INVALID_USER_ID;
		}
	}

	/**
	 * look up google account in database, add to database if not already added
	 * @param payload
	 * @return userID in database table
	 */
	public static int addGoogleAndGetId(Payload payload) {
		try( UserDao userDao = new UserDao()) { 
			//e.g gUserIdentifier == "212312312312321"
			String gUserIdentifier = payload.getSubject();
			// username is gUserIdentifier
			int userId = userDao.getUserId(gUserIdentifier); 
			if( userId == INVALID_USER_ID){
				String email = payload.getEmail();
				String firstName = (String)payload.get("given_name");
				String lastName = (String)payload.get("family_name");
				userDao.addGoogleUser(gUserIdentifier, firstName, lastName, email);
				userId = userDao.getUserId(gUserIdentifier);
			}
			return userId;
		}catch(Exception ex){
			logger.error("UserController: addUser()." + ex.getMessage());
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
		try( UserDao userDao = new UserDao()) {
			Password newPassword = new Password(password);
			userDao.addUser(firstName, lastName, email, username, newPassword.getSalt(), newPassword.getHashedPassword());;
		}catch(Exception ex){
			logger.error("UserController: addUser()." + ex.getMessage());
		}
	}	
}