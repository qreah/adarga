package adarga.external;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import adarga.endpoints.GetFCFY;

public class ListPrice {
	private static final Logger log = Logger.getLogger(ListPrice.class.getName());
	private static String APIKey = "9b587440587b5542b0567b8ac89092d2";
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/stock/real-time-price/"; 
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    
    
    public String getMarketData() throws IOException {
    	String out = "<!DOCTYPE html><html><body><table>";
    	out = out + getCompaniesPrices();
    	out = out + "<tr><td>" + "EURDOLLAR" + "</td><td>" + getEURDOLLAR() + "</td></tr>";
    	out = out + "<tr><td>" + "SP500" + "</td><td>" + getSP500Price() + "</td></tr>";
    	out = out + "<tr><td>" + "NASDAQ" + "</td><td>" + getNASDAQPrice() + "</td></tr>";
    	out = out + "</table></body></html>";
		return out;
    }
    
    public String getCompaniesPrices() throws IOException {
    	String out = "";
    	String Companies = "BRK.B,QSR,GOOGL,PAYC,"
    			+ "NXST,TM,WMT,AAPL,STNE,ITX, "
    			+ "BAC,MTCH,NVDA,AMZN";
    	
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	String urlEndpointComposer = urlEndpoint + Companies + "?apikey=" + APIKey;
		GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		JSONObject j = new JSONObject(json);
		JSONArray companiesPriceList = (JSONArray) j.get("companiesPriceList");
		Iterator<Object> iter = companiesPriceList.iterator();
		while (iter.hasNext()) {
			JSONObject company = (JSONObject) iter.next();
			String symbol = company.getString("symbol");
			Double price = company.getDouble("price");
			out = out + "<tr><td>" + symbol + "</td><td>" + Double.toString(price).replace(".", ",") + "</td></tr>";
			log.info(symbol + " " + price);
		}
		
		return out;
    }
    
    public String getSP500Price() throws IOException {
    	String SP500 = "";
    	String urlEndpoint = "https://financialmodelingprep.com/api/v3/quote/%5EGSPC";
    	
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	String urlEndpointComposer = urlEndpoint + "?apikey=" + APIKey;
		GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		log.info(json);
		JSONArray array = new JSONArray(json);
		JSONObject companiesPriceList = (JSONObject) array.get(0);
		SP500 = String.valueOf(companiesPriceList.getDouble("price")).replace(".", ",");
		return SP500;
    }
    
    public String getNASDAQPrice() throws IOException {
    	String NASDAQ = "";
    	String urlEndpoint = "https://financialmodelingprep.com/api/v3/majors-indexes/.IXIC";
    	
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	String urlEndpointComposer = urlEndpoint + "?apikey=" + APIKey;
		GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		JSONObject jsonObj = new JSONObject(json);
		NASDAQ = String.valueOf(jsonObj.getDouble("price")).replace(".", ",");
		return NASDAQ;
    }
    
    public String getEURDOLLAR() throws IOException {
    	String EURDOLLAR = "";
    	String urlEndpoint = "https://financialmodelingprep.com/api/v3/forex/EURUSD";
    	
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	String urlEndpointComposer = urlEndpoint + "?apikey=" + APIKey;
		GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		JSONObject jsonObj = new JSONObject(json);
		EURDOLLAR = String.valueOf(jsonObj.getDouble("open")).replace(".", ",");
		return EURDOLLAR;
    }
    
    

	public static void main(String[] args) throws IOException {
		

	}

}
