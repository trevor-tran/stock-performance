package app.user;

import static app.user.UserDao.INVALID_USER_ID;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

public class UserController {

	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static boolean usernameExists( String username){
		UserDao userDao = null;
		try{
			userDao = new UserDao();
			return userDao.getUserId(username) != INVALID_USER_ID;
		}catch(SQLException ex){
			logger.error("usernameExists() failed." + ex.getMessage());
			return false;
		}finally{
			userDao.close();
		}
	}

	public static String getFirstName(int userId){
		UserDao userDao = null;
		try{
			userDao = new UserDao();
			//TODO: possible to have invalid userId,maybe need "if"
			return userDao.getUserFirstName(userId);
		}catch(SQLException ex){
			logger.error("getFirstName() failed." + ex.getMessage());
			return "";
		}finally{
			userDao.close();
		}
	}

	/**
	 * @param username
	 * @param password
	 * @return <i>-1</i> if authentication failed, <i>user_id</i> if succeeded
	 * @throws Exception
	 */
	public static int authenticate(String username, String password){
		UserDao userDao = null;
		try{
			userDao = new UserDao();
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
		}catch(SQLException ex){
			logger.error("authenticate() failed." + ex.getMessage());
			return INVALID_USER_ID;
		}finally{
			userDao.close();
		}
	}

	/**
	 * look up google account in database, add to database if not already added
	 * @param payload
	 * @return userID in database table
	 */
	public static int addGoogleAndGetId(Payload payload) {
		UserDao userDao = null;
		try{
			userDao = new UserDao(); 
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
		}catch(SQLException ex){
			logger.error("addUser() failed." + ex.getMessage());
			return INVALID_USER_ID;
		}finally{
			userDao.close();
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
		UserDao userDao = null;
		try{
			userDao = new UserDao();
			Password newPassword = new Password(password);
			userDao.addUser(firstName, lastName, email, username, newPassword.getSalt(), newPassword.getHashedPassword());;
		}catch(SQLException ex){
			logger.error("addUser() failed." + ex.getMessage());
		}finally{
			userDao.close();
		}
	}	
}