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
			//first column is user_id. Second column is username
			String salt = rs.getString(3);
			String hashedPassword = rs.getString(4);
			String email = rs.getString(5);
			BigDecimal investment = rs.getBigDecimal(6);
			Date startDate = rs.getDate(7);
			Date endDate = rs.getDate(8);
			User user = new User(username, salt, hashedPassword, email, investment, startDate, endDate);
			//DatabaseConnection.closeConnection();
			return user;
		}
		//DatabaseConnection.closeConnection();
		return null;
		
	}

}
