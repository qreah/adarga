package adarga.getinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
	public boolean execute(String companySymbol) throws IOException, ClassNotFoundException, ServletException, SQLException {
		boolean result = true;
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
		
		if (json.toString().equals("{}")) {
			
			return false;
		}
		
		Iterator<String> iter = json.keys();
		
		while (iter.hasNext()) {
			String key = iter.next();
			QualityTest q = new QualityTest();
			q.getConcepts(key, companySymbol, "IS");
			Item item = new Item();
			item.setValues(json.getJSONObject(key));
			incomeStatementItems.put(key, item);
		}
		
		return result;
					
	}
	
	public int getYear() {
		int year;
		if (incomeStatementItems.get("Revenue") != null) {
    		year = incomeStatementItems.get("Revenue").lastYear();
    	} else {
    		if (incomeStatementItems.get("Net income") != null) {
    			year = incomeStatementItems.get("Net income").lastYear();
    		} else {
    			log.info(incomeStatementItems.toString());
    			log.info(incomeStatementItems.get("Income taxes").toString());
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
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, ServletException, SQLException {
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
	public Item InterestExpense() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k1 = utils.controlNull(incomeStatementItems.get("Interest Expense"), getYear());
		Item k2 = utils.controlNull(incomeStatementItems.get("Interest expenses"), getYear());
		Item k3 = utils.controlNull(incomeStatementItems.get("Total interest expense"), getYear());
		Item k = k1.sum(k2);
		k = k.sum(k3);
		
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Interest Expense");
			concepts.add("Interest expenses");
			concepts.add("Total interest expense");
			qT.uploadRareCases(name, concepts, "IncomeSheet");
			k = utils.controlNull(incomeStatementItems.get("Interest Expense"), getYear());
			
		}
		
		return k;
	}
	
	public Item costOfRevenue() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Cost of revenue");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Cost of revenue");
			qT.uploadRareCases(name, concepts, "IncomeSheet");
			k = utils.controlNull(incomeStatementItems.get("Cost of revenue"), getYear());
		}
		
		return k;
	}
	
	
	public Item Revenue() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Revenue");
		if (k != null) {	
		} else {
			k = incomeStatementItems.get("Total revenues");
			if (k != null) {	
			} else {
				k = incomeStatementItems.get("Total net revenue");
				if (k != null) {	
				} else {
					QualityTest qT = new QualityTest();
					List<String> concepts = new ArrayList<String>();
					concepts.add("Revenue");
					qT.uploadRareCases(name, concepts, "IncomeSheet");
					k = utils.controlNull(incomeStatementItems.get("Revenue"), getYear());
				}
			}
		}
		
		return k;
	}
	
	public Item provisionForIncomeTaxes() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Provision for income taxes");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Provision for income taxes");
			qT.uploadRareCases(name, concepts, "IncomeSheet");
			k = utils.controlNull(incomeStatementItems.get("Provision for income taxes"), getYear());
		}
		
		return k;
	}
	
	public Item netIncome() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Net income");
		
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Net income");
			qT.uploadRareCases(name, concepts, "IncomeSheet");
			k = utils.controlNull(incomeStatementItems.get("Net income"), getYear());
		}
		
		return k;
	}
	
	
	public Item SGA() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Sales, General and administrative");
		if (k != null) {	
		} else {
			Double i1 = 0.0;
			Double i2 = 0.0;
			Item k1 = incomeStatementItems.get("Operation and maintenance");
			Item k2 = incomeStatementItems.get("Other operating expenses");
			if (k1 != null) { i1 = 1.0;}
			if (k2 != null) { i2 = 1.0;}
			k = k1.multiplyNumber(i1);
			k = k.sum(k2.multiplyNumber(i2));
			
			if (k!=null) {
				
			} else {
				QualityTest qT = new QualityTest();
				List<String> concepts = new ArrayList<String>();
				concepts.add("Sales, General and administrative");
				qT.uploadRareCases(name, concepts, "IncomeSheet");
				k = utils.controlNull(incomeStatementItems.get("Sales, General and administrative"), getYear());

			}
		}
		
		return k;
	}
	
	
	public Item grossProfit() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Gross profit");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Gross profit");
			qT.uploadRareCases(name, concepts, "IncomeSheet");
			k = utils.controlNull(incomeStatementItems.get("Gross profit"), getYear());
		}
		
		return k;
	}
	
	
	
	@SuppressWarnings("unused")
	public Item OperatingExpenses() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Total operating expenses");
		if (k != null) {	
		} else {
			k = incomeStatementItems.get("Total costs and expenses");
			if (k != null) {
			} else {
				k = incomeStatementItems.get("Total expenses");
				if (k != null) {
					} else {
						QualityTest qT = new QualityTest();
						List<String> concepts = new ArrayList<String>();
						concepts.add("Total operating expenses");
						concepts.add("Total costs and expenses");
						concepts.add("Total expenses");
						qT.uploadRareCases(name, concepts, "IncomeSheet");
						k = utils.controlNull(incomeStatementItems.get("Total operating expenses"), getYear());
			
					}
			}
		}
		
		return k;
	}
	
	public Item OperatingIncome() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = incomeStatementItems.get("Operating income");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Operating income");
			qT.uploadRareCases(name, concepts, "IncomeSheet");
			k = utils.controlNull(incomeStatementItems.get("Operating income"), getYear());
		}
		
		
		return k;
	}
}
