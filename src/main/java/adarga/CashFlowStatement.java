package adarga;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

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

import adarga.IncomeStatement.FinancialModelingPrepUrl;

public class CashFlowStatement {
	
	private static final Logger log = Logger.getLogger(CashFlowStatement.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    static Map<String, Item> cashFlowStatement = new HashMap<String, Item>();
    static String name;
	
	public void execute(String companySymbol) throws IOException {
		name = companySymbol;
		String urlRaw = "https://financialmodelingprep.com/api/financials/cash-flow-statement/";
		HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest request) -> {
	        request.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		FinancialModelingPrepUrl url = new FinancialModelingPrepUrl(urlRaw + companySymbol);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = (HttpResponse)request.execute();
		String resString = res.parseAsString().replace("<pre>", "");
		
		JSONObject json = new JSONObject(resString).getJSONObject(companySymbol);
		Iterator<String> iter = json.keys();
		
		while (iter.hasNext()) {
			String key = iter.next();
			Item item = new Item();
			item.setValues(json.getJSONObject(key));
			cashFlowStatement.put(key, item);
		}
					
	}
	
	public static Item get(String itemName) {
		return cashFlowStatement.get(itemName);
	}
	
		

	
	public static class FinancialModelingPrepUrl extends GenericUrl {
		 
	    public FinancialModelingPrepUrl(String encodedUrl) {
	        super(encodedUrl);
	    }
	}
	
	public static void main(String[] args) throws IOException {
        try {
        	CashFlowStatement cs = new CashFlowStatement();
			cs.execute("aapl");
			Item currentAssets = get("Total current assets");
			int lastYear = currentAssets.lastYear();
			Double value = currentAssets.getValue(lastYear);
			System.out.println(value);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
}
