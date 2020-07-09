package com.phuongdtran.user;

import com.phuongdtran.executor.CytherExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserController {

	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static boolean exists(String username){
		UserDao userDao = new UserDao(new CytherExecutor());
		try{
			userDao.open();
			return userDao.exists(username);
		}catch(SQLException ex){
			logger.error("usernameExists() failed." + ex.getMessage());
			return false;
		}finally{
			userDao.close();
		}
	}

	public static String getFirstName(String username){
		UserDao userDao = new UserDao(new CytherExecutor());
		try{
			userDao.open();
			return userDao.getFirstName(username);
		}catch(SQLException ex){
			logger.error("getFirstName() failed." + ex.getMessage());
			return null;
		}finally{
			userDao.close();
		}
	}

	public static boolean authenticate(String username, String passphrase){
		UserDao userDao = new UserDao(new CytherExecutor());
		try{
			userDao.open();
			if (username.isEmpty() || passphrase.isEmpty()){
				return false;
			}
			if (!userDao.exists(username)) {
				return false;
			}
			Password password = userDao.getPassword(username);
			return password.matches(passphrase);
		}catch(SQLException ex){
			logger.error("authenticate() failed." + ex.getMessage());
			return false;
		}finally{
			userDao.close();
		}
	}

	public static void add(User user){
		UserDao userDao = new UserDao(new CytherExecutor());
		try{
			userDao.open();
			userDao.add(user);
		}catch(SQLException ex){
			logger.error("addUser() failed." + ex.getMessage());
		}finally{
			userDao.close();
		}
	}

	/**
	 * Check if password contains at least one lower case letter,
	 * one upper case letter, one number, and length of at least eight.
	 * @see <a href ="http://stackoverflow.com/questions/3802192/regexp-java-for-password-validation">link</a>
	 * @param passphrase
	 * @return <i>true</i> if password is complex, <i>false</i> if not
	 */
	public static boolean isComplex(String passphrase) {
		Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
		Matcher matcher = pattern.matcher(passphrase);
		return matcher.matches();
	}

	/**
	 * look up google account in database, add to database if not already added
	 * @param payload
	 * @return userID in database table
	 */
//	public static int addGoogleAndGetId(Payload payload) {
//		UserDao userDao = null;
//		try{
//			userDao = new UserDao();
//			//e.g gUserIdentifier == "212312312312321"
//			String gUserIdentifier = payload.getSubject();
//			// username is gUserIdentifier
//			int userId = userDao.getUserId(gUserIdentifier);
//			if( userId == INVALID_USER_ID){
//				String email = payload.getEmail();
//				String firstName = (String)payload.get("given_name");
//				String lastName = (String)payload.get("family_name");
//				userDao.addGoogleUser(gUserIdentifier, firstName, lastName, email);
//				userId = userDao.getUserId(gUserIdentifier);
//			}
//			return userId;
//		}catch(SQLException ex){
//			logger.error("addUser() failed." + ex.getMessage());
//			return INVALID_USER_ID;
//		}finally{
//			userDao.close();
//		}
//	}


}