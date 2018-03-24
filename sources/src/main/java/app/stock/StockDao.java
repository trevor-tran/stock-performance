package app.stock;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.util.QueryHandler;

public class StockDao {

	//quandl api key
	private static final String apiKey = "LSHfJJyvzYHUyU9jHpn6";
	public static final int NOT_FOUND = -1;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static QueryHandler queryStockData(String symbol, String startDate, String endDate){
		try{
			QueryHandler queryHandler = new QueryHandler();
			Statement statement = queryHandler.getStatement();
			if(getSymbolId(statement, symbol) == NOT_FOUND){
				insertData(statement, symbol, startDate, endDate);
			}else{
				updateTable(statement, symbol, startDate, endDate);
			}
			String sql = String.format( 
								"SELECT t.date_as_id, s.symbol, t.price, t.split_ratio "
							+	"FROM %s AS t "
							+	"INNER JOIN Symbols AS s "
							+	"WHERE t.symbol_id = s.symbol_id "
							+ 	"AND t.date_as_id BETWEEN '%s' AND '%s'", symbol, startDate, endDate);
			
			queryHandler.executeQuery(sql);
			return queryHandler;
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return null;
	}
	
	private static void updateTable(Statement statement, String symbol, String startDate,String endDate) {
		try{
			String sql = String.format("CALL END_DATE_OF_QUANDL_REQUEST('%s', '%s')", symbol, startDate);
			ResultSet rs = statement.executeQuery(sql);
			if(rs.next()){
				String newEndDate = rs.getString("@beforeFirstDate");
				if(newEndDate != null){
					insertData(statement, symbol, startDate, newEndDate);
				}else{
					insertData(statement, symbol, startDate, endDate);
				}
			}
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}
	
	private static void insertData(Statement statement, String symbol, String startDate, String endDate){
		try{
			JsonArray quandlData = getQuandlData(symbol, startDate, endDate);
			if(quandlData != null){
				int symbolId = getSymbolId(statement, symbol);
				if(symbolId == NOT_FOUND){
					symbolId = addSymbolAndCreateTable(statement, symbol);
				}
				for (JsonElement element : quandlData) {
					//each element is ["2016-12-28", "MSFT", 62.99, 2.0]
					JsonArray e = element.getAsJsonArray();
					String date = e.get(0).getAsString(); 
					double price = e.get(2).getAsDouble();
					double split = e.get(3).getAsDouble();
					String sql = String.format("INSERT INTO %s VALUES('%s', %f, %f,%d)", symbol,date,price,split,symbolId);
					statement.executeUpdate(sql);
				}
			}
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}	
	
	private static int addSymbolAndCreateTable(Statement statement,String symbol){
		try{
			//add new symbol to Symbols table
			String sql = String.format("INSERT INTO Symbols(symbol) VALUES('%s')", symbol);
			statement.executeUpdate(sql);
			
			//create new table for new symbol added
			sql = String.format(
					"CREATE TABLE %s ("
							+"date_as_id date not null,"
							+"price decimal(13,2) not null,"
							+"split_ratio double not null,"
							+"symbol_id int not null,"
							+"primary key (date_as_id),"
							+"foreign key (symbol_id) references Symbols(symbol_id))", symbol);
			statement.executeUpdate(sql);
			
			//return symbol_id from Symbols table
			return getSymbolId(statement,symbol);
		
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return NOT_FOUND;
	}
	
	private static int getSymbolId(Statement statement, String symbol) {
		try{
			String sql = String.format( "SELECT symbol_id FROM Symbols WHERE symbol='%s'",symbol);
			ResultSet rs = statement.executeQuery(sql);

			if(rs.next()){
				int symbolId = rs.getInt("symbol_id");
				rs.close();
				return symbolId;
			}
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
		return NOT_FOUND;
	}

	//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e49
	//https://github.com/google/gson/blob/master/UserGuide.md
	private static JsonArray getQuandlData(String symbol, String startDate, String endDate) {
		try{
			URI uri = getRequestUri(symbol, startDate, endDate);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet request = new HttpGet(uri);
			System.out.println("http get: " + request.getURI());//TODO: uri printing, need del later
			ResponseHandler<Map<String,JsonObject>> rh = new QuandlResponseHandler();
			Map<String,JsonObject> quandlResponse = httpclient.execute(request,rh);
			//throw error message if failed to request data
			if(quandlResponse.containsKey("failure")){
				JsonObject error = quandlResponse.get("failure").getAsJsonObject("quandl_error");
				String code = error.get("code").getAsString();
				String msg = error.get("message").getAsString();
				throw new HttpException("Quandl error code: " + code + ". Message: " + msg);
			}else{
				//handle data if success
				JsonArray dataArr = quandlResponse.get("success").getAsJsonObject("datatable").getAsJsonArray("data");
				if(dataArr.size() == 0){
					return null;
				}
				return dataArr;
			} 
		}catch (HttpException ex){
			logger.error(ex.getMessage());
		}catch(ClientProtocolException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error(ex.getMessage());	
		}
		return null; //TODO: need a proper return 
	}

	//build URL to request data from Quandl
	private static URI getRequestUri(String symbol, String startDate, String endDate) throws URISyntaxException{
		//https://docs.quandl.com/docs/parameters-1
		URI uri = new URIBuilder()
				.setScheme("https")
				.setHost("quandl.com")
				.setPath("/api/v3/datatables/WIKI/PRICES.json")
				.setParameter("qopts.columns", "date,ticker,close,split_ratio")
				.setParameter("date.gte", startDate)
				.setParameter("date.lte", endDate)
				.setParameter("ticker", symbol)
				.setParameter("api_key", apiKey)
				.build();
		return uri;
	}
}
