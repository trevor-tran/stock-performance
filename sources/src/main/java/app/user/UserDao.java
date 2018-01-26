package app.user;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import app.util.DatabaseConnection;;
/**
 * Manage all communications with database
 * @author PhuongTran
 *
 */
public class UserDao {
	private static Statement connect() throws Exception
	{
		Connection connection = DatabaseConnection.getConnection();
		Statement statement = connection.createStatement();
		return statement;

	}
	/**
	 * Get all credentials of the user from database
	 * @param username
	 * @return User
	 * @throws Exception
	 */
	public static User getUserByName(String username) throws Exception{
		Statement statement = connect();
		String sql = String.format("SELECT * FROM users WHERE username='%s'",username);
		ResultSet rs = statement.executeQuery(sql);
		//DatabaseConnection.closeConnection(); TODO:why cannot close connection at this point?
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
			return user;
		}
		return null;
	}
	//public static void setUser(String username,)

}
