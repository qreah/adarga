package adarga.getinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

import utils.Utils;



public class BalanceSheet {
	
	private static final Logger log = Logger.getLogger(BalanceSheet.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    static Map<String, Item> balanceSheetItems = new HashMap<String, Item>();
    static String name;
	
	public void execute(String companySymbol) throws IOException {
		name = companySymbol;
		String urlRaw = "https://financialmodelingprep.com/api/financials/balance-sheet-statement/";
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
			balanceSheetItems.put(key, item);
		}
					
	}
	
	public static Item get(String itemName) {
		return balanceSheetItems.get(itemName);
	}
	
		

	
	public static class FinancialModelingPrepUrl extends GenericUrl {
		 
	    public FinancialModelingPrepUrl(String encodedUrl) {
	        super(encodedUrl);
	    }
	}
	
	public Item Equity() {
		Item equity = balanceSheetItems.get("Total stockholders' equity");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Total Stockholders' equity");
		}
		return equity;
	}
	
	public Item Receivables() {
		Item equity = balanceSheetItems.get("Receivables");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Receivables");
		}
		return equity;
	}
	
	public Item accountsPayable() {
		Item equity = balanceSheetItems.get("Accounts payable");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Accounts payable");
		}
		return equity;
	}
	
	public Item totalCurrentAssets() {
		Item equity = balanceSheetItems.get("Total current assets");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Total current assets");
		}
		return equity;
	}
	
	
	public Item totalCurrentLiabilities() {
		Item equity = balanceSheetItems.get("Total current liabilities");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Total current liabilities");
		}
		return equity;
	}
	
	
	public Item capitalLeases() {
		Item equity = balanceSheetItems.get("Capital leases");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Capital leases");
		}
		return equity;
	}
	
	public Item longTermDebt() {
		Item equity = balanceSheetItems.get("Long-term debt");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Long-term debt");
		}
		return equity;
	}
	
	public Item totalAssets() {
		Item equity = balanceSheetItems.get("Total assets");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Total assets");
		}
		return equity;
	}
	
	public Item shortTermDebt() {
		Item equity = balanceSheetItems.get("Short-term debt");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Short-term debt");
		}
		return equity;
	}
	
	public Item totalCash() {
		Item equity = balanceSheetItems.get("Total cash");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Total cash");
		}
		return equity;
	}
	
	public Item Inventories() {
		Item equity = balanceSheetItems.get("Inventories");
		if (equity != null) {
			
		} else {
			equity = balanceSheetItems.get("Inventories");
		}
		return equity;
	}
	
	public Item Goodwill() {
		Item goodwill = balanceSheetItems.get("Goodwill");
		Utils utils = new Utils();
		if (goodwill != null) {
			
		} else {
			goodwill = utils.controlNull(goodwill, balanceSheetItems.get("Receivables").lastYear());
		}
				
		return goodwill;
	}
	
	public Item IntagibleAssets() {
		Item intagible = balanceSheetItems.get("Intangible assets");
		Utils utils = new Utils();
		if (intagible != null) {
			
		} else {
			intagible = utils.controlNull(intagible, balanceSheetItems.get("Receivables").lastYear());
		}
				
		return intagible;
	}
	
	public static void main(String[] args) throws IOException {
        try {
        	BalanceSheet bs = new BalanceSheet();
			bs.execute("aapl");
			Item currentAssets = get("Total current assets");
			int lastYear = currentAssets.lastYear();
			Double value = currentAssets.getValue(lastYear);
			System.out.println(value);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
}



