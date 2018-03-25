package app.user;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.QueryHandler;;
/**
 * Manage all communications with database
 * @author PhuongTran
 */
public class UserDao {

	public static final int INVALID_USER_ID = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	public static String getUserFirstName(int userId) {
		try(QueryHandler queryHandler = new QueryHandler()) {
			String sql = String.format("SELECT first_name FROM UserInfo WHERE user_id=%d" , userId);
			ResultSet rs = queryHandler.executeQueryViaStatement(sql);
			if(!rs.next()){
				return "";
			}
			String firstName = rs.getString(1);
			queryHandler.close();
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
		try(QueryHandler queryHandler = new QueryHandler()) {
			String sql = String.format("SELECT user_id FROM UserInfo WHERE username='%s'" , username);
			ResultSet rs = queryHandler.executeQueryViaStatement(sql);
			if(rs.next()){
				int userId = rs.getInt(1);
				queryHandler.close();
				return userId;
			}
			queryHandler.close();
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
		try(QueryHandler queryHandler = new QueryHandler()){
			String sql = String.format("SELECT salt,hashed_password FROM UserInfo WHERE user_id=%d",userId);
			ResultSet rs = queryHandler.executeQueryViaStatement(sql);
			if(rs.next()){
				String salt = rs.getString(1);
				String hashedPassword = rs.getString(2);
				return new Password(salt, hashedPassword);
			}
		} catch (SQLException ex) {
			logger.error("Database exception: " + ex);
		} catch (Exception ex) {
			logger.error("getSigninCredentials() failed:" + ex);
		}
		return null;
	}
	public static UserInfo getUserInvestmentInfo( int userId){
		try(QueryHandler queryHandler = new QueryHandler()){
			String sql = String.format("SELECT user.first_name,user.investment,user.start_date,user.end_date,stocks.symbol,stock.number_of_shares"
					+ " FROM UserInfo WHERE UserInfo.user_id=%d"
					+ " AND UserInfo.user_id=stocks.user_id",userId);
			ResultSet rs = queryHandler.executeQueryViaStatement(sql);
			if(rs.next()){
				BigDecimal investment = rs.getBigDecimal(1);
				Date startDate = rs.getDate(2);
				Date endDate = rs.getDate(3);
				String stockSymbol = rs.getString(4);
				double numberOfShares = rs.getInt(5);
				return new UserInfo(investment, startDate, endDate, stockSymbol, numberOfShares);
			}
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
	public static void addUser(String firstName,String lastName,String email, String username,String salt,String hashedPassword){
		try(QueryHandler queryHandler = new QueryHandler()) {
			String sql = String.format("INSERT INTO UserInfo( first_name, last_name, email, username, salt, hashed_password, google_user) "
					+ "VALUES('%s','%s','%s','%s','%s','%s',%d)", firstName,lastName,email,username,salt,hashedPassword,0);
			queryHandler.executeUpdateViaStatement(sql);
			queryHandler.close();
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
	public static void addGoogleUser(String gUserIdentifier, String firstName,String lastName,String email){
		try(QueryHandler queryHandler = new QueryHandler()) {
			String sql = String.format("INSERT INTO UserInfo(username, first_name, last_name, email, google_user) "
					+ "VALUES('%s','%s','%s','%s',%d)", gUserIdentifier, firstName, lastName, email, 1);	
			queryHandler.executeUpdateViaStatement(sql);
			queryHandler.close();
		}catch (Exception ex) {
			logger.error("addGoogleUser() failed:" + ex);
		}
	}
}