package adarga.getinfo;

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

import adarga.getinfo.BalanceSheet.FinancialModelingPrepUrl;
import utils.Utils;

public class IncomeStatement {
	
	private static final Logger log = Logger.getLogger(IncomeStatement.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    static Map<String, Item> incomeStatementItems = new HashMap<String, Item>();
    static String name;
    Utils utils = new Utils();
    
    public IncomeStatement() {
    	incomeStatementItems = new HashMap<String, Item>();
    }
	
	@SuppressWarnings("unused")
	public void execute(String companySymbol) throws IOException {
		name = companySymbol;
		String urlRaw = "https://financialmodelingprep.com/api/financials/income-statement/";
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
			incomeStatementItems.put(key, item);
		}
					
	}
	
	public int getYear() {
		int year;
		if (incomeStatementItems.get("Revenue") != null) {
    		year = incomeStatementItems.get("Revenue").lastYear();
    	} else {
    		if (incomeStatementItems.get("Net income") != null) {
    			year = incomeStatementItems.get("Net income").lastYear();
    		} else {
    			year = incomeStatementItems.get("Income taxes").lastYear();
    		}
    	}
		
		return year;
	}
	
	public Item get(String itemName) {
		return incomeStatementItems.get(itemName);
	}
	
		

	
	public static class FinancialModelingPrepUrl extends GenericUrl {
		 
	    public FinancialModelingPrepUrl(String encodedUrl) {
	        super(encodedUrl);
	    }
	}
	
	public static void main(String[] args) throws IOException {
        try {
        	IncomeStatement is = new IncomeStatement();
			is.execute("aapl");
			Item currentAssets = is.get("Total current assets");
			int lastYear = currentAssets.lastYear();
			Double value = currentAssets.getValue(lastYear);
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
	public Item InterestExpense() {
		Item k = new Item();
		Item k1 = incomeStatementItems.get("Interest Expense");
		Item k2 = incomeStatementItems.get("Interest expenses");
		if (k1 != null) {
			k = k1;
		} else if (k2 != null) {
			k = k2;
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Interest Expense");
			log.info("Término que no aparece: Interest expenses");
			k = utils.controlNull(incomeStatementItems.get("Interest Expense"), getYear());
		}
		
		return k;
	}
	
	public Item costOfRevenue() {
		Item k = incomeStatementItems.get("Cost of revenue");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Cost of revenue");
			k = utils.controlNull(incomeStatementItems.get("Cost of revenue"), getYear());
		}
		
		return k;
	}
	
	
	public Item Revenue() {
		Item k = incomeStatementItems.get("Revenue");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Revenue");
			k = utils.controlNull(incomeStatementItems.get("Revenue"), getYear());
		}
		
		return k;
	}
	
	public Item provisionForIncomeTaxes() {
		Item k = incomeStatementItems.get("Provision for income taxes");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Provision for income taxes");
			k = utils.controlNull(incomeStatementItems.get("Provision for income taxes"), getYear());
		}
		
		return k;
	}
	
	public Item netIncome() {
		Item k = incomeStatementItems.get("Net income");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Net income");
			k = utils.controlNull(incomeStatementItems.get("Net income"), getYear());
		}
		
		return k;
	}
	
	
	public Item SGA() {
		Item k = incomeStatementItems.get("Sales, General and administrative");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Sales, General and administrative");
			k = utils.controlNull(incomeStatementItems.get("Sales, General and administrative"), getYear());
		}
		
		return k;
	}
	
	
	public Item grossProfit() {
		Item k = incomeStatementItems.get("Gross profit");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Gross profit");
			k = utils.controlNull(incomeStatementItems.get("Gross profit"), getYear());
		}
		
		return k;
	}
	
	
	
	@SuppressWarnings("unused")
	public Item OperatingExpenses() {
		Item k = new Item();
		Item k1 = incomeStatementItems.get("Total operating expenses");
		Item k2 = incomeStatementItems.get("Total costs and expenses");
		Item k3 = incomeStatementItems.get("Total expenses");
		if (k1 != null) {
			k = k1;
		} else if (k2 != null) {
			k = k2;
		} else if (k3 != null) {
			k = k3;
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Total operating expenses");
			log.info("Término que no aparece: Total costs and expenses");
			k = utils.controlNull(incomeStatementItems.get("Total operating expenses"), getYear());
		}
		
		return k;
	}
	
	public Item OperatingIncome() {
		Item operatingIncome = incomeStatementItems.get("Operating income");
		Item revenue = Revenue();
		Item COGS = costOfRevenue();
		Item totalOperatingExpenses = OperatingExpenses();
		if (operatingIncome != null) {
				
		} else {
			
			operatingIncome = revenue.substract(COGS);
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Operating income");
			operatingIncome = operatingIncome.substract(totalOperatingExpenses);
		}
		return operatingIncome;
	}
}
