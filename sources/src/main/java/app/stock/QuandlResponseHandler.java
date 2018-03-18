package app.stock;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

//https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e49
public class QuandlResponseHandler implements ResponseHandler<Map<String,JsonObject>>{
	
	public Map<String,JsonObject> handleResponse( final HttpResponse response) throws IOException{
		Map<String, JsonObject> quandlResponse = new HashMap<String, JsonObject>();
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		Gson gson = new GsonBuilder().create();
		ContentType contentType = ContentType.getOrDefault(entity);
		Charset charset = contentType.getCharset();
		Reader reader = new InputStreamReader(entity.getContent(), charset);
		
		if (statusLine.getStatusCode() >= 300){
			if(entity.getContentLength() != 0){
				//return error message from Quandl if available
				//https://docs.quandl.com/docs/error-codes
				quandlResponse.put("failure", gson.fromJson(reader, JsonObject.class));
			}else {
				throw new HttpResponseException(statusLine.getStatusCode(),statusLine.getReasonPhrase());
			}
		}
		if (entity == null) {
			throw new ClientProtocolException("Response contains no content");
		}
		//return data if succeeded
		quandlResponse.put("success", gson.fromJson(reader, JsonObject.class));
		return quandlResponse;
	}
}
