package app.user;

public class UserController {
	
	public static final int INVALID_USER_ID = -1;
	
	public static boolean usernameExists( String username){
		try{
			return UserDao.getUserId(username) != -1;
		}catch(Exception ex){
			System.out.println("Error in usernameExists() method");
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public static String getFirstName(int userId){
		try{
			//TODO: possible to have invalid userId,maybe need "if"
			return UserDao.getUserFirstName(userId);
		}catch(Exception ex){
			System.out.println("Error in getFirstName() method");
			System.out.println(ex.getMessage());
			return null;
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
			if(userId == -1){
				return INVALID_USER_ID;
			}
			else{
				Password signingInUser = UserDao.getSigninCredentials(userId);
				return signingInUser.matches(password) ? userId : INVALID_USER_ID;
			}
		}catch(Exception ex){
			System.out.println("Error in authenticate() method");
			System.out.println(ex.getMessage());
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
	public static void createUser(String firstName,String lastName,String email, String username,String password){
		try{
			Password newPassword = new Password(password);
			UserDao.setUser(firstName, lastName, email, username, newPassword.getSalt(), newPassword.getHashedPassword());;
		}catch(Exception ex){
			System.out.println("Error in  createUser() method");
			System.out.println(ex.getMessage());
		}
	}
}