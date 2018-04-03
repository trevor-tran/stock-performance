package app.user;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.ConnectionManager;
import app.util.StatementAndResultSet;;
/**
 * Manage all communications with database
 * @author PhuongTran
 */
public class UserDao extends StatementAndResultSet implements AutoCloseable {

	private Connection conn = null;
	public static final int INVALID_USER_ID = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public UserDao() {
		conn = ConnectionManager.getInstance().getConnection();
	}

	public String getUserFirstName(int userId) {
		try{
			String sql = "SELECT first_name FROM UserInfo WHERE user_id=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()){
				return "";
			}
			String firstName = rs.getString("first_name");
			release(pstmt,rs);
			return firstName;
		} catch (SQLException ex) {
			logger.error("Database exception: " + ex);
			refreshConnection();
		} catch (Exception ex) {
			logger.error("getUserFirstName() failed:" + ex);
		}
		return "";
	}

	/**
	 * Find user by username
	 * @param username
	 * @return <i>user_id</i> if found the user, <i>-1</i> if not found.
	 * @throws Exception
	 */
	public int getUserId(String username) {
		try{
			String sql = "SELECT user_id FROM UserInfo WHERE username=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				int userId = rs.getInt(1);
				release(pstmt,rs);
				return userId;
			}
			release(pstmt,rs);
			return INVALID_USER_ID;
		} catch (SQLException ex) {
			logger.error("Database exception: " + ex);
			refreshConnection();
		} catch (Exception e) {
			logger.error("getUserId() failed" + e);
		}
		return INVALID_USER_ID;
	}

	/**
	 * Get sign-in credentials, salt and hashed password
	 * @param userId
	 * @return <i>SigninCredentials</i>, <i>null</i> if userId not exist
	 * @throws Exception
	 */
	public Password getSigninCredentials(int userId) {
		try{
			String sql = "SELECT salt,hashed_password FROM UserInfo WHERE user_id=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				String salt = rs.getString("salt");
				String hashedPassword = rs.getString("hashed_password");
				release(pstmt, rs);
				return new Password(salt, hashedPassword);
			}
			release(pstmt, rs);
		}catch (SQLException ex) {
			logger.error("Database exception: " + ex);
			refreshConnection();
		}catch (Exception ex) {
			logger.error("getSigninCredentials() failed:" + ex);
		}
		return null;
	}
	/**
	 * execute INSERT query to add new user to users_table
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param username
	 * @param salt
	 * @param hashedPassword
	 * @throws Exception
	 */
	public void addUser(String firstName,String lastName,String email, String username,String salt,String hashedPassword){
		try{
			String sql = "INSERT INTO UserInfo( first_name, last_name, email, username, salt, hashed_password, google_user) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			pstmt.setString(3,email);
			pstmt.setString(4, username);
			pstmt.setString(5, salt);
			pstmt.setString(6, hashedPassword);
			pstmt.setInt(7, 0);
			pstmt.executeUpdate();
			
			release(pstmt);
		}catch (Exception ex) {
			logger.error("addUser() failed:" + ex);
		}
	}

	/**
	 * execute INSERT query to add new Google user to users_table
	 * @param firstName
	 * @param lastName
	 * @param email
	 */
	public void addGoogleUser(String gUserIdentifier, String firstName,String lastName,String email){
		try{
			String sql = "INSERT INTO UserInfo(username, first_name, last_name, email, google_user) "
					+ "VALUES(?, ?, ?, ?, ?)";	
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, gUserIdentifier);
			pstmt.setString(2, firstName);
			pstmt.setString(3, lastName);
			pstmt.setString(4,email);
			pstmt.setInt(5, 1);
			pstmt.executeUpdate();
			
			release(pstmt);
		}catch (Exception ex) {
			logger.error("addGoogleUser() failed:" + ex);
		}
	}

	
	private void refreshConnection(){
		ConnectionManager.getInstance().releaseConnection(conn);
		conn = ConnectionManager.getInstance().getConnection();
	}

	@Override
	public void close() throws Exception {
		ConnectionManager.getInstance().releaseConnection(conn);
	}
}