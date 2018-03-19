package app.user;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.DatabaseConnection;;
/**
 * Manage all communications with database
 * @author PhuongTran
 */
public class UserDao {

	public static final int INVALID_USER_ID = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	
	public static String getUserFirstName(int userId) {
		try{
			Connection connection = DatabaseConnection.getConnection();
			Statement statement = connection.createStatement();
			String sql = String.format("SELECT first_name FROM users WHERE user_id=%d" , userId);
			ResultSet rs = statement.executeQuery(sql);
			if(!rs.next()){
				return "";
			}
			String firstName = rs.getString(1);
			close(connection,statement,rs);
			return firstName;
		} catch (SQLException ex) {
			logger.error("Database exception: " + ex);
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
	public static int getUserId(String username) {
		try{
			Connection connection = DatabaseConnection.getConnection();
			Statement statement = connection.createStatement();
			String sql = String.format("SELECT user_id FROM users WHERE username='%s'" , username);
			ResultSet rs = statement.executeQuery(sql);
			if(rs.next()){
				int userId = rs.getInt(1);
				close(connection,statement,rs);
				return userId;
			}
			close(connection,statement,rs);
			return INVALID_USER_ID;
		} catch (SQLException ex) {
			logger.error("Database exception: " + ex);
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
	public static Password getSigninCredentials(int userId) {
		try{
			Connection connection = DatabaseConnection.getConnection();
			Statement statement = connection.createStatement();
			String sql = String.format("SELECT salt,hashed_password FROM users WHERE user_id=%d",userId);
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			String salt = rs.getString(1);
			String hashedPassword = rs.getString(2);
			close(connection,statement,rs);
			return new Password(salt, hashedPassword);
		} catch (SQLException ex) {
			logger.error("Database exception: " + ex);
		} catch (Exception ex) {
			logger.error("getSigninCredentials() failed:" + ex);
		}
		return null;
	}
	public static UserInfo getUserInvestmentInfo( int userId){
		try{
			Connection connection = DatabaseConnection.getConnection();
			Statement statement = connection.createStatement();
			String sql = String.format("SELECT user.first_name,user.investment,user.start_date,user.end_date,stocks.symbol,stock.number_of_shares"
					+ " FROM users WHERE users.user_id=%d"
					+ " AND users.user_id=stocks.user_id",userId);
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			BigDecimal investment = rs.getBigDecimal(1);
			Date startDate = rs.getDate(2);
			Date endDate = rs.getDate(3);
			String stockSymbol = rs.getString(4);
			double numberOfShares = rs.getInt(5);
			close(connection,statement,rs);
			return new UserInfo(investment, startDate, endDate, stockSymbol, numberOfShares);
		} catch (SQLException ex) {
			logger.error("Database exception: " + ex);
		} catch (Exception ex) {
			logger.error("getUserInvestmentInfo() failed:" + ex);
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
	public static void createUser(String firstName,String lastName,String email, String username,String salt,String hashedPassword){
		try{
			Connection connection = DatabaseConnection.getConnection();
			Statement statement = connection.createStatement();
			String sql = String.format("INSERT INTO users(first_name,last_name,email,username,salt,hashed_password,google_user) "
					+ "VALUES('%s','%s','%s','%s','%s','%s',%d)", firstName,lastName,email,username,salt,hashedPassword,0);
			statement.executeUpdate(sql);
			close(connection,statement);
		}catch (SQLException ex) {
			logger.error("Database exception: " + ex);
		}catch (Exception ex) {
			logger.error("createUser() failed:" + ex);
		}
	}

	/**
	 * close <i>Connection,Statement, ResultSet</i>
	 * @param connection
	 * @param statement
	 * @param rs
	 * @throws SQLException
	 */
	private static void close(Connection connection, Statement statement, ResultSet rs){
		try{
			if(rs != null){rs.close();}
			if( statement != null){ statement.close();}
			if (connection != null){connection.close();}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}finally{
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(rs);
		}
	}

	/**
	 * close <i>Connection, Statement</i>
	 * @param connection
	 * @param statement
	 * @throws SQLException
	 */
	private static void close(Connection connection, Statement statement){
		try{
			if( statement != null){ statement.close();}
			if (connection != null){connection.close();}
		}catch(SQLException ex){
			logger.error("Database exception:", ex);
		}finally{
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(statement);
		}
	}
}