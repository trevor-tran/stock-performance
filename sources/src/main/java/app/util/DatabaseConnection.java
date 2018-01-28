package app.util;

import java.sql.*;

//connect to database
public class DatabaseConnection {

	/** 
	 * for webapp <-> MySQL connections on the same server, we may wish to disable SSL to avoid MySQL certificate errors.<br/>
	 * @see https://stackoverflow.com/a/34449182
	 */
	private static boolean useSSL = false;
	private static Connection connection = null;

	public static Connection getConnection() throws Exception
	{
		if (connection != null) return connection;

		return getConnection("stock_performance","stock","Stock_Performance");  // TODO: password
	}

	public static void initialize() {
		try {
			// pre-load the mysql driver class, before we start using JDBC calls
			//   https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-connect-drivermanager.html
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Connection getConnection(String dbName, String userName, String password) throws Exception
	{
		try {
			String sslParam = useSSL ? "" : "?useSSL=false";
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + sslParam, userName, password);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
