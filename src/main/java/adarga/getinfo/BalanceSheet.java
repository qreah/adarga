package adarga.getinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

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

import adarga.analysis.QualityTest;
import utils.Utils;



public class BalanceSheet {
	
	private static final Logger log = Logger.getLogger(BalanceSheet.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    static Map<String, Item> balanceSheetItems = new HashMap<String, Item>();
    static String name;
    Utils utils = new Utils();
    
    public BalanceSheet() {
    	balanceSheetItems = new HashMap<String, Item>();
    }
	
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
	
	public int getYear() {
		int year;
		if (balanceSheetItems.get("Receivables") != null) {
    		year = balanceSheetItems.get("Receivables").lastYear();
    	} else {
    		if (balanceSheetItems.get("Total assets") != null) {
    			year = balanceSheetItems.get("Total assets").lastYear();
    		} else {
    			year = balanceSheetItems.get("Total liabilities").lastYear();
    		}
    	}
		
		return year;
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
		Item k = balanceSheetItems.get("Total stockholders' equity");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Total stockholders' equity");
			k = utils.controlNull(balanceSheetItems.get("Total stockholders' equity"), getYear());
		}
		return k;
	}
	
	public Item Receivables() {
		Item k = balanceSheetItems.get("Receivables");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Receivables");
			k = utils.controlNull(balanceSheetItems.get("Receivables"), getYear());
		}
		return k;
	}
	
	public Item accountsPayable() {
		Item k = balanceSheetItems.get("Accounts payable");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Accounts payable");
			k = utils.controlNull(balanceSheetItems.get("Accounts payable"), getYear());
		}
		return k;
	}
	
	public Item totalCurrentAssets() {
		Item k = balanceSheetItems.get("Total current assets");
		if (k != null) {
			
		} else {
			k = balanceSheetItems.get("Receivables");
			if (k != null) {
				
			} else {
				log.info("Empresa a monitorizar: " + name);
				log.info("Término que no aparece: Total current assets");
				log.info("Término que no aparece: Receivables");
				k = utils.controlNull(balanceSheetItems.get("Total current assets"), getYear());
			}
			
		}
		return k;
	}
	
	
	public Item totalCurrentLiabilities() {
		Item k = balanceSheetItems.get("Total current liabilities");
		if (k != null) {
			
		} else {
			k = balanceSheetItems.get("Payables and accrued expenses");
			if (k != null) {
				
			} else {
				log.info("Empresa a monitorizar: " + name);
				log.info("Término que no aparece: Total current liabilities");
				log.info("Término que no aparece: Payables and accrued expenses");
				k = utils.controlNull(balanceSheetItems.get("Total current liabilities"), getYear());
			}
			
		}
		return k;
	}
	
	
	
	public Item capitalLeases() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Capital leases");
		
		if (k != null) {
			
		} else {
			
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Capital leases");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Capital leases"), getYear());
		}
		
		return k;
	}
	
	public Item longTermDebt() {
		Item k = balanceSheetItems.get("Long-term debt");
		
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Long-term debt");
			k = utils.controlNull(balanceSheetItems.get("Long-term debt"), getYear());
		}
		return k;
	}
	
	public Item totalAssets() {
		Item k = balanceSheetItems.get("Total assets");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Total assets");
			k = utils.controlNull(balanceSheetItems.get("Total assets"), getYear());
		}
		return k;
	}
	
	public Item shortTermDebt() {
		Item k = balanceSheetItems.get("Short-term debt");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Short-term debt");
			k = utils.controlNull(balanceSheetItems.get("Short-term debt"), getYear());
		}
		return k;
	}
	
	public Item totalCash() {
		
		Item k = new Item();
		Item k1 = balanceSheetItems.get("Total cash");
		//TODO: arreglarlo
		//Item k2 = balanceSheetItems.get("Cash and cash equivalents").sum(balanceSheetItems.get("Restricted cash"));
		
		if (k1 != null) {
			k = k1;
					
		} else {
				log.info("Empresa a monitorizar: " + name);
				log.info("Término que no aparece: Total cash");
				log.info("Término que no aparece: Cash and cash equivalents y Restricted cash");
				k = utils.controlNull(balanceSheetItems.get("Total cash"), getYear());
		}
		return k;
	}
	
	public Item Inventories() {
		Item k = balanceSheetItems.get("Inventories");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Inventories");
			k = utils.controlNull(balanceSheetItems.get("Inventories"), getYear());
		}
		return k;
	}
	
	public Item Goodwill() {
		Item k = balanceSheetItems.get("Goodwill");
		
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Goodwill");
			k = utils.controlNull(balanceSheetItems.get("Goodwill"), getYear());
		}
				
		return k;
	}
	
	public Item IntagibleAssets() {
		Item k = balanceSheetItems.get("Intangible assets");
		
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Intangible assets");
			k = utils.controlNull(balanceSheetItems.get("Intangible assets"), getYear());
		}
				
		return k;
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



