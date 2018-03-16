package app.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//connect to database
public class DatabaseConnection {

	/** 
	 * for webapp <-> MySQL connections on the same server, we may wish to disable SSL to avoid MySQL certificate errors.<br/>
	 * @see https://stackoverflow.com/a/34449182
	 */
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static boolean useSSL = false;
	private static String hostname;
	private static String port;
	private static String dbname;
	private static String username;
	private static String password;
	private static Connection connection = null;

	public static Connection getConnection() throws Exception
	{
		if (connection != null) 
			return connection;

		return getConnection(dbname,username,password);  // TODO: password
	}

	public static void initialize() {
		try {
			// pre-load the mysql driver class, before we start using JDBC calls
			//   https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-connect-drivermanager.html
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			getDbSettings();
		} catch (Exception ex) {
			logger.error("cannot initialize database connection:"+ ex.getStackTrace());
		}
	}

	private static Connection getConnection(String dbName, String userName, String password)
	{
		try {
			String sslParam = useSSL ? "" : "?useSSL=false";
			return DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + dbName + sslParam, userName, password);
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		} catch (Exception e) {
			logger.error("getConnection() failed"+ e.getStackTrace());
		}
		return null;
	}
	
	private static void getDbSettings(){
		Properties props = new Properties();
		InputStream input = null;
		try{
			input = DatabaseConnection.class.getResourceAsStream("/config/mysql.properties");
			props.load(input);
			hostname = props.getProperty("hostname");
			port = props.getProperty("port");
			dbname = props.getProperty("dbname");
			username = props.getProperty("username");
			password = props.getProperty("password");
			if (input != null) {
	             input.close();
	         }
		}catch(IOException ex){
			logger.error("loading config settings failed. " + ex.getStackTrace());
		}
	}
}
