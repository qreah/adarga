package adarga.getinfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import adarga.getinfo.BalanceSheet.FinancialModelingPrepUrl;

public class CompanyInformation {
	private static final Logger log = Logger.getLogger(BalanceSheet.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    static Map<String, String> companyInformation = new HashMap<String, String>();
    static String name;
    
    public void execute(String companySymbol) throws IOException {
		name = companySymbol;
		String urlRaw = "https://financialmodelingprep.com/api/company/profile/";
		HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest request) -> {
	        request.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		FinancialModelingPrepUrl url = new FinancialModelingPrepUrl(urlRaw + companySymbol);
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		
		
			HttpResponse res = (HttpResponse)request.execute();			
			String resString = res.parseAsString().replace("<pre>", "");	
			if (companySymbol.equals("BRK.B")) {
				
			} else {
			JSONObject json = new JSONObject(resString).getJSONObject(companySymbol);
			//companyInformation.put("Price", Double.toString(json.getDouble("Price")));
			
			Iterator<String> iter = json.keys();
			
			while (iter.hasNext()) {
				String key = iter.next();
				
				if (json.get(key).equals(null)) {
				} else {
					try {
						String info = (String) json.get(key);
						companyInformation.put(key, info);
						log.info(info);
						log.info(key);
					} catch (Exception e) {
						Double info = json.getDouble(key);
						companyInformation.put(key, Double.toString(info));
					}
				}
				
			}
		  }	
		
		
				
	}
    
    public Double getStockPrice() {
    	Double stockPrice = Double.parseDouble(companyInformation.get("Price"));
    	return stockPrice;
    }
    
    public Double getMktCap() {
    	Double MktCap = Double.parseDouble(companyInformation.get("MktCap")) / 1000000;
    	return MktCap;
    }
    
    public Double numberOfShares() {
    	Double marketCapitalization = Double.parseDouble(companyInformation.get("MktCap")) /1000000;
    	Double stockPrice = Double.parseDouble(companyInformation.get("Price"));
    	Double numberOfShares = marketCapitalization / stockPrice;
    	return numberOfShares;
    }
    
    
public static void main(String[] args) throws Exception {
		
		CompanyInformation ci = new CompanyInformation();
		ci.execute("MMM");
		
		
		
	}
}
