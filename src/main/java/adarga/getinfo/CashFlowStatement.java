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
import adarga.getinfo.IncomeStatement.FinancialModelingPrepUrl;
import utils.Utils;

public class CashFlowStatement {
	
	private static final Logger log = Logger.getLogger(CashFlowStatement.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    static Map<String, Item> cashFlowStatement = new HashMap<String, Item>();
    static String name;
    static int year;
    Utils utils = new Utils();
    
    public CashFlowStatement() {
    	cashFlowStatement = new HashMap<String, Item>();
    }
    
	
	public boolean execute(String companySymbol) throws IOException, ClassNotFoundException, ServletException, SQLException {
		boolean result = true;
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
		
		if (json.toString().equals("{}")) {
			
			return false;
		}
		
		
		Iterator<String> iter = json.keys();
		
		while (iter.hasNext()) {
			String key = iter.next();
			QualityTest q = new QualityTest();
			q.getConcepts(key, companySymbol, "CF");
			Item item = new Item();
			item.setValues(json.getJSONObject(key));
			cashFlowStatement.put(key, item);
		}
		
		return result;
					
	}
	
	public static Item get(String itemName) {
		return cashFlowStatement.get(itemName);
	}
	
	public int getYear() {
		if (cashFlowStatement.get("Net income") != null) {
    		year = cashFlowStatement.get("Net income").lastYear();
    	} else {
    		if (cashFlowStatement.get("Inventory") != null) {
    			year = cashFlowStatement.get("Inventory").lastYear();
    		} else {
    			
    			year = cashFlowStatement.get("Cash at end of period").lastYear();
    		}
    	}
		
		return year;
	}

	
	public static class FinancialModelingPrepUrl extends GenericUrl {
		 
	    public FinancialModelingPrepUrl(String encodedUrl) {
	        super(encodedUrl);
	    }
	}
	
	public Item NetCashProvidedByOperatingActivities() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Net cash provided by operating activities");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Net cash provided by operating activities");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Net cash provided by operating activities"), getYear());
		}
		return k;
	}
	
	public Item FCF() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Free cash flow");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Free cash flow");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Free cash flow"), getYear());
		}
		
		return k;
	}
	
	public Item DividendPaid() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item cashDividendsPaid = utils.controlNull(cashFlowStatement.get("Cash dividends paid"), getYear());
		Item dividendPaid = utils.controlNull(cashFlowStatement.get("Dividend paid"), getYear());
			
		Item k = cashDividendsPaid.sum(dividendPaid);
		if (k != null) {	
		} else {
				QualityTest qT = new QualityTest();
				List<String> concepts = new ArrayList<String>();
				concepts.add("Dividend paid");
				concepts.add("Cash dividends paid");
				concepts.add("Preferred dividend");
				qT.uploadRareCases(name, concepts, "CashFlowStatement");
				k = utils.controlNull(cashFlowStatement.get("Dividend paid"), getYear());
		}
		
		return k;
	}
	
	
	public Item NetCashUsedForInvestingActivities() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Net cash used for investing activities");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Net cash used for investing activities");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Net cash used for investing activities"), getYear());
		}		
		return k;
	}
	
	
	
	public Item NetCashProvidedByUsedForFinancingActivities() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Net cash provided by (used for) financing activities");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Net cash provided by (used for) financing activities");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Net cash provided by (used for) financing activities"), getYear());
		}		
		
		return k;
	}
	
	public Item PurchasesOfInvestments() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Purchases of investments");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Purchases of investments");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Purchases of investments"), getYear());
		}	
		
		return k;
	}
	
	public Item CommonStockIssued() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Common stock issued");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Common stock issued");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Common stock issued"), getYear());
		}	
				
		return k;
	}
	
	
	public Item DebtRepayment() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k1 = utils.controlNull(cashFlowStatement.get("Debt repayment"), getYear());
		Item k2 = utils.controlNull(cashFlowStatement.get("Long-term debt repayment"), getYear());
		Item k3 = utils.controlNull(cashFlowStatement.get("Amortization of debt and issuance costs"), getYear());

		
		Item k = k1.sum(k2);
		k = k.sum(k3);
		if (k != null) {	
		} else {
			
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Debt repayment");
			concepts.add("Long-term debt repayment");
			concepts.add("Amortization of debt and issuance costs");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Debt repayment"), getYear());
		}
		
		return k;
	}
	
	public Item DebtIssued() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item init = cashFlowStatement.get("Free cash flow");
		if (init == null) {
			init = cashFlowStatement.get("Cash at end of period");
		}
		if (init == null) {
			init = cashFlowStatement.get("Net cash provided by operating activities");
		}
		int lastYear = init.lastYear();
		int numYears = init.size();
		Item k = new Item();
		k.setZero(numYears, lastYear);
		Item kk = k;
		
		Item k1 = cashFlowStatement.get("Debt issued");
		if (k1 != null) {k = k.sum(k1);}
		Item k2 = cashFlowStatement.get("Long-term debt issued");
		if (k2 != null) {k = k.sum(k2);}
		
		if (kk.equals(k)) {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Debt issued");
			concepts.add("Long-term debt issued");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Debt issued"), getYear());
		}		
		return k;
	}
	
	public Item CommonStockRepurchased() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Common stock repurchased");
		if (k != null) {	
			k = k.sum(utils.controlNull(cashFlowStatement.get("Common stock repurchased"), getYear()));
		} else {
			k = cashFlowStatement.get("Net repurchased");
			if (k != null) {
				k = k.sum(utils.controlNull(cashFlowStatement.get("Common stock repurchased"), getYear()));
			} else {
				k = cashFlowStatement.get("Repurchases of treasury stock");
				if (k != null) {
					} else {
						QualityTest qT = new QualityTest();
						List<String> concepts = new ArrayList<String>();
						concepts.add("Common stock repurchased");
						concepts.add("Net repurchased");
						concepts.add("Repurchases of treasury stock");
						qT.uploadRareCases(name, concepts, "IncomeSheet");
						k = utils.controlNull(cashFlowStatement.get("Common stock repurchased"), getYear());
			
					}
			}
		}
		
		return k;
	}
	
	
	public Item AcquisitionsNet() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Acquisitions, net");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Acquisitions, net");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Acquisitions, net"), getYear());
		}	
		return k;
	}
	
	public Item PropertyPlantAndEquipmentReductions() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Property, plant, and equipment reductions");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Property, plant, and equipment reductions");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Property, plant, and equipment reductions"), getYear());
		}	
				
		return k;
	}
	
	
	public Item InvestmentsInPropertyPlantAndEquipment() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Investments in property, plant, and equipment");
		if (k != null) {	
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Investments in property, plant, and equipment");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Investments in property, plant, and equipment"), getYear());
		}	
		
		return k;
	}
	
	
	
	public Item SecuritiesSale() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item k = cashFlowStatement.get("Sales/Maturities of investments");
		if (k != null) {	
		} else {
			k = cashFlowStatement.get("Other investing activities");
			if (k != null) {
			} else {
				QualityTest qT = new QualityTest();
				List<String> concepts = new ArrayList<String>();
				concepts.add("Sales/Maturities of investments");
				concepts.add("Other investing activities");
				qT.uploadRareCases(name, concepts, "CashFlowStatement");
				k = utils.controlNull(cashFlowStatement.get("Sales/Maturities of investments"), getYear());
			}
		}
				
		return k;
	}
	
	public Item operatingCashFlow() throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item k = cashFlowStatement.get("Net cash provided by operating activities");
		if (k != null) {
		} else {
			QualityTest qT = new QualityTest();
			List<String> concepts = new ArrayList<String>();
			concepts.add("Net cash provided by operating activities");
			qT.uploadRareCases(name, concepts, "CashFlowStatement");
			k = utils.controlNull(cashFlowStatement.get("Net cash provided by operating activities"), getYear());
		}
		return k;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, ServletException, SQLException {
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
