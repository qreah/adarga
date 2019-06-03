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

public class IncomeStatement {
	
	private static final Logger log = Logger.getLogger(IncomeStatement.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    static Map<String, Item> incomeStatementItems = new HashMap<String, Item>();
    static String name;
	
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
			System.out.println(value);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
	public Item InterestExpense() {
		Item interestExpense = incomeStatementItems.get("Interest Expense");
		if (interestExpense != null) {
			
		} else {
			interestExpense = incomeStatementItems.get("Interest expense");
		}
		
		return interestExpense;
	}
	
	public Item costOfRevenue() {
		Item interestExpense = incomeStatementItems.get("Cost of revenue");
		if (interestExpense != null) {
			
		} else {
			interestExpense = incomeStatementItems.get("Cost of revenue");
		}
		
		return interestExpense;
	}
	
	
	public Item Revenue() {
		Item interestExpense = incomeStatementItems.get("Revenue");
		if (interestExpense != null) {
			
		} else {
			interestExpense = incomeStatementItems.get("Revenue");
		}
		
		return interestExpense;
	}
	
	public Item provisionForIncomeTaxes() {
		Item interestExpense = incomeStatementItems.get("Provision for income taxes");
		if (interestExpense != null) {
			
		} else {
			interestExpense = incomeStatementItems.get("Provision for income taxes");
		}
		
		return interestExpense;
	}
	
	public Item netIncome() {
		Item interestExpense = incomeStatementItems.get("Net income");
		if (interestExpense != null) {
			
		} else {
			interestExpense = incomeStatementItems.get("Net income");
		}
		
		return interestExpense;
	}
	
	
	public Item SGA() {
		Item interestExpense = incomeStatementItems.get("Sales, General and administrative");
		if (interestExpense != null) {
			
		} else {
			interestExpense = incomeStatementItems.get("Sales, General and administrative");
		}
		
		return interestExpense;
	}
	
	
	public Item grossProfit() {
		Item interestExpense = incomeStatementItems.get("Gross profit");
		if (interestExpense != null) {
			
		} else {
			interestExpense = incomeStatementItems.get("Gross profit");
		}
		
		return interestExpense;
	}
	
	
	
	public Item manageTotalOperatingExpenses() {
		Item totalOperatingExpenses = new Item();
		
		if (name.equals("XOM")) {
			totalOperatingExpenses = incomeStatementItems.get("Total costs and expenses");
		} else {
			totalOperatingExpenses = incomeStatementItems.get("Total operating expenses");
			
		}
		
		return totalOperatingExpenses;
	}
	
	public Item OperatingIncome() {
		Item operatingIncome = incomeStatementItems.get("Operating income");
		Item revenue = incomeStatementItems.get("Revenue");
		Item COGS = incomeStatementItems.get("Cost of revenue");
		Item totalOperatingExpenses = manageTotalOperatingExpenses();
		if (operatingIncome != null) {
				
		} else {
			
			operatingIncome = revenue.substract(COGS);
			operatingIncome = operatingIncome.substract(totalOperatingExpenses);
		}
		return operatingIncome;
	}
}
