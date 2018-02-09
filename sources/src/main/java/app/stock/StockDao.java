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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e49
	//https://github.com/google/gson/blob/master/UserGuide.md
	public static Map<String,List<PriceData>> getStockData(String symbol, String startDate, String endDate) {
		try{
			URI uri = setUri(symbol, startDate, endDate);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(uri);
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
			List<PriceData> dataList = reformatJson(dataArr);
			Map<String,List<PriceData>> data = new HashMap<String,List<PriceData>>();
			data.put(symbol, dataList);
			return data;
			
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
				.setParameter("qopts.columns", "close,date")
				.setParameter("date.gte", startDate)
				.setParameter("date.lte", endDate)
				.setParameter("ticker", symbol)
				.setParameter("api_key", apiKey)
				.build();
		return uri;
	}
	//TODO: find easier way
	private static List<PriceData> reformatJson(JsonArray dataArr){
		List<PriceData> priceList = new ArrayList<PriceData>();
		//Gson gson = new GsonBuilder().create();
		float date = 2000;
		for ( JsonElement element : dataArr)
		{
			JsonArray e = element.getAsJsonArray();
			float price = e.get(0).getAsFloat();
			date += 1;// e.get(1).getAsString();
			PriceData priceData = new PriceData(price,date);
			priceList.add(priceData);
		}
		return priceList;
	}
}
