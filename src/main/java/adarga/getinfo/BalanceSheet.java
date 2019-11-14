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
	
	public boolean execute(String companySymbol) throws IOException, ClassNotFoundException, ServletException, SQLException {
		boolean result = true;
		name = companySymbol;
		String urlRaw = "https://financialmodelingprep.com/api/financials/balance-sheet-statement/";
		HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest request) -> {
	        request.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		log.info(urlRaw + companySymbol);
		FinancialModelingPrepUrl url = new FinancialModelingPrepUrl(urlRaw + companySymbol);
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		HttpResponse res = (HttpResponse)request.execute();
		String resString = res.parseAsString().replace("<pre>", "");
		
			
		JSONObject json = new JSONObject(resString).getJSONObject(companySymbol);
		
		if (json.toString().equals("{}")) {
			
			return false;
		}
		
		Iterator<String> iter = json.keys();
		
		while (iter.hasNext()) {
			String key = iter.next();
			QualityTest q = new QualityTest();
			q.getConcepts(key, companySymbol, "BS");
			Item item = new Item();
			item.setValues(json.getJSONObject(key));
			balanceSheetItems.put(key, item);
		}
		return result;
					
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
	
	public Item Equity() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Total stockholders' equity");
		if (k != null) {
			
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Total stockholders' equity");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Total stockholders' equity"), getYear());
		}
		return k;
	}
	
	public Item Receivables() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Receivables");
		if (k != null) {
			
		} else {
			k = balanceSheetItems.get("Premiums and other receivables");
			if (k != null) {
			} else {
				QualityTest qT = new QualityTest();
				List<String> concepts = new ArrayList<String>();
				concepts.add("Receivables");
				concepts.add("Premiums and other receivables");
				qT.uploadRareCases(name, concepts, "BalanceSheet");
				k = utils.controlNull(balanceSheetItems.get("Receivables"), getYear());
			}
		}
		return k;
	}
	
	public Item accountsPayable() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k1 = utils.controlNull(balanceSheetItems.get("Accounts payable"), getYear());
		Item k2 = utils.controlNull(balanceSheetItems.get("Payables"), getYear());
		Item k3 = utils.controlNull(balanceSheetItems.get("Payables and accrued expenses"), getYear());
		Item k = k1.sum(k2);
		k = k.sum(k3);
		
		if (k != null) {
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Accounts payable");
			concepts.add("Payables and accrued expenses");
			concepts.add("Payables");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Accounts payable"), getYear());
		}
		return k;
	}
	
	public Item totalCurrentAssets() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Total current assets");
		
		//TODO: Valorar si tener un esquema en el que controles diferentes nombres 
		// del item global y si no está sumar todos los sumandos posibles
		// pero con el controlNUll por si no está ese item específico
		
		
		if (k != null) {
			
		} else {
			k = balanceSheetItems.get("Receivables");
			if (k != null) {
				Item cash = utils.controlNull(balanceSheetItems.get("Cash and cash equivalents"), getYear());
				Item cashRestricted = utils.controlNull(balanceSheetItems.get("Restricted cash"), getYear());
				k = balanceSheetItems.get("Receivables").sum(cash);
				k = k.sum(cashRestricted);
			} else {
				QualityTest qT = new QualityTest();
				List<String> concepts = new ArrayList<String>();
				concepts.add("Total current assets");
				concepts.add("Receivables");
				qT.uploadRareCases(name, concepts, "BalanceSheet");
				k = utils.controlNull(balanceSheetItems.get("Total current assets"), getYear());
			}
			
		}
		return k;
	}
	
	
	public Item totalCurrentLiabilities() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Total current liabilities");
		if (k != null) {
			
		} else {
			k = balanceSheetItems.get("Payables and accrued expenses");
			if (k != null) {
				
			} else {
				QualityTest qT = new QualityTest();
				List<String> concepts = new ArrayList<String>();
				concepts.add("Total current liabilities");
				concepts.add("Payables and accrued expenses");
				qT.uploadRareCases(name, concepts, "BalanceSheet");
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
	
	public Item longTermDebt() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Long-term debt");
		
		if (k != null) {
			
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Long-term debt");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Long-term debt"), getYear());
		}
		return k;
	}
	
	public Item totalAssets() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Total assets");
		if (k != null) {
			
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Total assets");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Total assets"), getYear());
		}
		return k;
	}
	
	public Item shortTermDebt() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Short-term debt");
		if (k != null) {
			
		} else {
			k = balanceSheetItems.get("Short-term borrowing");
			if (k != null) {
				
			} else {
				QualityTest qT = new QualityTest();
				List<String> concepts = new ArrayList<String>();
				concepts.add("Short-term debt");
				concepts.add("Short-term borrowing");
				qT.uploadRareCases(name, concepts, "BalanceSheet");
				k = utils.controlNull(balanceSheetItems.get("Short-term debt"), getYear());
			}
		}
		return k;
	}
	
	public Item totalCash() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item init = balanceSheetItems.get("Total assets");
		int lastYear = init.lastYear();
		int numYears = init.size();
		Item k = new Item();
		k.setZero(numYears, lastYear);
		Item kk = k;
		
		Item k1 = balanceSheetItems.get("Total cash");
		if (k1 != null) {k = k.sum(k1);}
		Item k2 = balanceSheetItems.get("Cash and cash equivalents");  
		if (k2 != null) {k = k.sum(k2);}
		Item k3 = balanceSheetItems.get("Restricted cash and cash equivalents");
		if (k3 != null) {k = k.sum(k3);}                            
		Item k4 = balanceSheetItems.get("Restricted cash and cash equivalents");	
		if (k4 != null) {k = k.sum(k4);} 	
			
		if (kk.equals(k)) {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Total cash");
			concepts.add("Cash and cash equivalents");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Total cash"), getYear());
		}
		
		return k;
	}
	
	public Item Inventories() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Inventories");
		if (k != null) {
			
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Inventories");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Inventories"), getYear());
		}
		return k;
	}
	
	public Item Goodwill() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Goodwill");
		
		if (k != null) {
			
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Goodwill");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			k = utils.controlNull(balanceSheetItems.get("Goodwill"), getYear());
		}
				
		return k;
	}
	
	public Item IntagibleAssets() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = balanceSheetItems.get("Intangible assets");
		
		if (k != null) {
		
		} else {
			k = balanceSheetItems.get("Other intangible assets");
			if (k != null) {
			
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Intangible assets");
			concepts.add("Other intangible assets");
			qT.uploadRareCases(name, concepts, "BalanceSheet");
			
			k = utils.controlNull(balanceSheetItems.get("Intangible assets"), getYear());
		}
		
		}
				
		return k;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, ServletException, SQLException {
        try {
        	BalanceSheet bs = new BalanceSheet();
			bs.execute("aapl");
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
}



