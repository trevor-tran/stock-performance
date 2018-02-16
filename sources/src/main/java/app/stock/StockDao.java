package app.stock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
/**
 * Responsible for request stock data from quandl.com
 * @author PhuongTran
 *
 */
public class StockDao {
	//quandel api key
	private static final String apiKey = "LSHfJJyvzYHUyU9jHpn6";
	private static final String apiKey2 = "gCex5psCGUqRBsjyXAxs";

	//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e49
	//https://github.com/google/gson/blob/master/UserGuide.md
	public static Map<String,Map<String,Float>> getStockData(String symbol, String startDate, String endDate) {
		try{
			URI uri = setUri(symbol, startDate, endDate);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(uri);
			System.out.println(httpget.getURI());//TODO: uri printing
			ResponseHandler<JsonObject> rh = new ResponseHandler<JsonObject>() {

				@Override
				public JsonObject handleResponse(
						final HttpResponse response) throws IOException {
					StatusLine statusLine = response.getStatusLine();
					HttpEntity entity = response.getEntity();
					if (statusLine.getStatusCode() >= 300) {
						throw new HttpResponseException(
								statusLine.getStatusCode(),
								statusLine.getReasonPhrase());
					}
					if (entity == null) {
						throw new ClientProtocolException("Response contains no content");
					}
					Gson gson = new GsonBuilder().create();
					ContentType contentType = ContentType.getOrDefault(entity);
					Charset charset = contentType.getCharset();
					Reader reader = new InputStreamReader(entity.getContent(), charset);
					return gson.fromJson(reader, JsonObject.class);
				}
			};
			JsonObject myjson = httpclient.execute(httpget,rh);
			JsonArray dataArr = myjson.getAsJsonObject("datatable").getAsJsonArray("data");
			return reformatJson(dataArr);//TODO: experiencing data loss with large amount.
		} 
		catch(Exception ex){
			System.out.println(ex.getMessage());
			return null;
		}
	}
	private static URI setUri(String symbol, String startDate, String endDate) throws URISyntaxException{
		URI uri = new URIBuilder()
				.setScheme("https")
				.setHost("quandl.com")
				.setPath("/api/v3/datatables/WIKI/PRICES.json")
				.setParameter("qopts.columns", "date,ticker,close")
				.setParameter("date.gte", startDate)
				.setParameter("date.lte", endDate)
				.setParameter("ticker", symbol)
				.setParameter("api_key", apiKey)
				.build();
		return uri;
	}
	private static Map<String,Map<String,Float>> reformatJson(JsonArray dataArr){
		//MUST use TreeMap here to order dates
		Map<String,Map<String,Float>> priceMap = new TreeMap<String,Map<String,Float>>();
		for ( JsonElement element : dataArr)
		{
			JsonArray e = element.getAsJsonArray();
			String date = e.get(0).getAsString();
			String ticker = e.get(1).getAsString();
			float price = e.get(2).getAsFloat();
			if (priceMap.containsKey(date)){
				Map<String,Float> value = priceMap.get(date);
				value.put(ticker, price);
			}
			else {
				Map<String,Float> newValue = new HashMap<String,Float>();
				newValue.put(ticker, price);
				priceMap.put(date, newValue);
			}
		}
		return priceMap;
	}
}
