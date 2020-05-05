package com.phuongdtran.user;

import com.google.gson.Gson;
import com.phuongdtran.executor.IExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import static org.neo4j.helpers.collection.MapUtil.map;


public class UserDao implements IUserDao{

	private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private IExecutor executor;
	private boolean isOpen;
	private Gson gson;
	public UserDao( IExecutor executor) {
		this.executor = executor;
		isOpen = false;
		gson = new Gson();
	}

	@Override
	public void open() throws SQLException {
		executor.open();
		isOpen = true;
	}

	@Override
	public void close() {
		executor.close();
		isOpen = false;
	}

	@Override
	public boolean exists(String username) throws SQLException {
		connectionOpened();
		String query = "MATCH (u:User) WHERE u.username=$username return u";
		Iterator<Map<String, Object>> iterator = executor.query(query, map("username", username));
		return iterator.hasNext();
	}

	@Override
	public String getFirstName(String username) throws SQLException {
		connectionOpened();
		String query = "MATCH (u:User) WHERE u.username=$username RETURN u.firstName as firstname";
		Iterator<Map<String, Object>> iterator = executor.query(query, map("username", username));
		if (iterator.hasNext()) {
			Map<String, Object> firstName = map(iterator.next());
			return firstName.get("firstname").toString();
		}
		return null;
	}

	@Override
	public Password getPassword(String username) throws SQLException {
		connectionOpened();
		String query = "MATCH (u:User) WHERE u.username=$username RETURN u.hash as hash, u.salt as salt";
		Iterator<Map<String, Object>> iterator = executor.query(query, map("username", username));
		if (iterator.hasNext()) {
			Map<String, Object> credentials = map(iterator.next());
			return new Password(credentials.get("salt").toString(), credentials.get("hash").toString());
		}
		return null;
	}

	@Override
	public boolean add(User user) throws SQLException{
		connectionOpened();
		String query = "CREATE (u:User {username: $username, hash: $hash, salt: $salt, " +
				"firstName: $firstname, lastName: $lastname, email: $email })";
		return executor.create(query,
				map("username", user.getUsername(),
						"hash", user.getPassword().getHash(),
						"salt", user.getPassword().getSalt(),
						"firstname", user.getFirstName(),
						"lastname", user.getLastName(),
						"email", user.getEmail()));
	}

	/**
	 * Check whether open() has been called. If not, throw an exception.
	 * @throws SQLException
	 */
	private void connectionOpened() throws SQLException {
		if (!isOpen) {
			throw new SQLException("Not yet open a connection. Please call open()");
		}
	}


//	/**
//	 * execute INSERT query to add new Google user to users_table
//	 * @param firstName
//	 * @param lastName
//	 * @param email
//	 */
//	public void addGoogleUser(String gUserIdentifier, String firstName,String lastName,String email){
//		PreparedStatement pstmt = null;
//		try{
//			String sql = "INSERT INTO UserInfo(username, first_name, last_name, email, google_user) "
//					+ "VALUES(?, ?, ?, ?, ?)";
//			pstmt = conn.prepareStatement(sql);
//
//			pstmt.setString(1, gUserIdentifier);
//			pstmt.setString(2, firstName);
//			pstmt.setString(3, lastName);
//			pstmt.setString(4,email);
//			pstmt.setInt(5, 1);
//
//			pstmt.executeUpdate();
//		}catch (SQLException ex) {
//			logger.error("addGoogleUser() failed:" + ex.getMessage());
//		}finally{
//			release(pstmt);
//		}
//	}
}