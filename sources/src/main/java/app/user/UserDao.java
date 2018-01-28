package app.user;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import app.util.DatabaseConnection;;
/**
 * Manage all communications with database
 * @author PhuongTran
 *
 */
public class UserDao {
	
	/**
	 * Get all credentials of the user from database
	 * @param username
	 * @return User
	 * @throws Exception
	 */
	public static User getUserCredentials(String username) throws Exception{
		Connection connection = DatabaseConnection.getConnection();
		Statement statement = connection.createStatement();
		String sql = String.format("SELECT * FROM users WHERE username='%s'",username);
		ResultSet rs = statement.executeQuery(sql);
		if(rs.next()){
			//first column is user_id
			String firstName = rs.getString(2);
			String lastName = rs.getString(3);
			//fourth column is username
			String salt = rs.getString(5);
			String hashedPassword = rs.getString(6);
			String email = rs.getString(7);
			BigDecimal investment = rs.getBigDecimal(8);
			Date startDate = rs.getDate(9);
			Date endDate = rs.getDate(10);
			User user = new User(firstName,lastName,username, salt, hashedPassword, email, investment, startDate, endDate);
			close(connection,statement,rs);
			return user;
		}
		close(connection,statement,rs);
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
	public static void setUser(String firstName,String lastName,String email, String username,String salt,String hashedPassword) throws Exception{
		Connection connection = DatabaseConnection.getConnection();
		Statement statement = connection.createStatement();
		String sql = String.format("INSERT INTO users(first_name,last_name,email,username,salt,hashed_password) "
				+ "VALUES('%s','%s','%s','%s','%s','%s')", firstName,lastName,email,username,salt,hashedPassword);
		statement.executeUpdate(sql);
		close(connection,statement);
	}
	
	/**
	 * close <i>Connection,Statement, ResultSet</i>
	 * @param connection
	 * @param statement
	 * @param rs
	 * @throws SQLException
	 */
	private static void close(Connection connection, Statement statement, ResultSet rs) throws SQLException{
		if(rs != null){rs.close();}
		if( statement != null){ statement.close();}
		if (connection != null){connection.close();}
	}
	/**
	 * close <i>Connection, Statement</i>
	 * @param connection
	 * @param statement
	 * @throws SQLException
	 */
	private static void close(Connection connection, Statement statement) throws SQLException{
		if( statement != null){ statement.close();}
		if (connection != null){connection.close();}
	}



}
