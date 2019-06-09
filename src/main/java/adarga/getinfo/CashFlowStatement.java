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
	
	public Item NetCashProvidedByOperatingActivities() {
		Item NC = cashFlowStatement.get("Net cash provided by operating activities");
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Net cash provided by operating activities");
			
			NC = utils.controlNull(cashFlowStatement.get("Net cash provided for operating activities"), getYear());
		}
		
		return NC;
	}
	
	public Item FCF() {
		Item FCF = cashFlowStatement.get("Free cash flow");
		if (FCF != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Free cash flow");
			FCF = utils.controlNull(cashFlowStatement.get("Free cash flow"), getYear());
		}
		
		return FCF;
	}
	
	public Item DividendPaid() {
		Item k = new Item();
		Item k1 = cashFlowStatement.get("Dividend paid");
		Item k2 = cashFlowStatement.get("Cash dividends paid");
		if (k1 != null) {
			k = k1;
		} else if (k2 != null) {
			k = k2;
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Dividend paid");
			log.info("Término que no aparece: Cash dividends paid");
			k = utils.controlNull(cashFlowStatement.get("Dividend paid"), getYear());
		}
		
		return k;
	}
	
	
	public Item NetCashUsedForInvestingActivities() {
		Item NC = cashFlowStatement.get("Net cash used for investing activities");
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Net cash used for investing activities");
			NC = utils.controlNull(cashFlowStatement.get("Net cash used for investing activities"), getYear());
		}
		
		return NC;
	}
	
	
	
	public Item NetCashProvidedByUsedForFinancingActivities() {
		Item NC = cashFlowStatement.get("Net cash provided by (used for) financing activities");
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Net cash provided by (used for) financing activities");
			NC = utils.controlNull(cashFlowStatement.get("Net cash provided by (used for) financing activities"), getYear());
		}
		
		return NC;
	}
	
	public Item PurchasesOfInvestments() {
		Item k = new Item();
		Item k1 = cashFlowStatement.get("Purchases of investments");
		
		if (k1 != null) {
			k = k1;
		
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Purchases of investments");
			
			k = utils.controlNull(cashFlowStatement.get("Purchases of investments"), getYear());
		}
		
		return k;
	}
	
	public Item CommonStockIssued() {
		Item NC = cashFlowStatement.get("Common stock issued");
		
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Common stock issued");
			NC = utils.controlNull(cashFlowStatement.get("Common stock issued"), getYear());
			
		}
		
		return NC;
	}
	
	
	public Item DebtRepayment() {
		Item NC = cashFlowStatement.get("Debt repayment");
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Debt repayment");
			NC = utils.controlNull(cashFlowStatement.get("Debt repayment"), getYear());
		}
		
		return NC;
	}
	
	public Item DebtIssued() {
		Item NC = cashFlowStatement.get("Debt issued");
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Debt issued");
			NC = utils.controlNull(cashFlowStatement.get("Debt issued"), getYear());
		}
		
		return NC;
	}
	
	public Item CommonStockRepurchased() {
		Item k = new Item();
		Item k1 = cashFlowStatement.get("Common stock repurchased");
		Item k2 = cashFlowStatement.get("Net repurchased");
		Item k3 = cashFlowStatement.get("Repurchases of treasury stock");
		if (k1 != null) {
			k = k1;
		} else if (k2 != null) {
			k = k2;
		} else if (k3 != null) {
			k = k3;
		} else {
				log.info("Empresa a monitorizar: " + name);
				log.info("Término que no aparece: Common stock repurchased");
				log.info("Término que no aparece: Net repurchased");
				log.info("Término que no aparece: Repurchases of treasury stock");
				k = utils.controlNull(cashFlowStatement.get("Common stock repurchased"), getYear());
			}		
		return k;
	}
	
	
	public Item AcquisitionsNet() {
		Item NC = cashFlowStatement.get("Acquisitions, net");
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Acquisitions, net");
			NC = utils.controlNull(cashFlowStatement.get("Acquisitions, net"), getYear());
		}
		
		return NC;
	}
	
	public Item PropertyPlantAndEquipmentReductions() {
		Item k = cashFlowStatement.get("Property, plant, and equipment reductions");
		if (k != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Property, plant, and equipment reductions");
			k = utils.controlNull(cashFlowStatement.get("Property, plant, and equipment reductions"), getYear());
		}
		
		return k;
	}
	
	
	public Item InvestmentsInPropertyPlantAndEquipment() {
		Item NC = cashFlowStatement.get("Investments in property, plant, and equipment");
		if (NC != null) {
			
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Investments in property, plant, and equipment");
			NC = utils.controlNull(cashFlowStatement.get("Investments in property, plant, and equipment"), getYear());
		}
		
		return NC;
	}
	
	
	
	public Item SecuritiesInvestment() {
		Item k = new Item();
		Item k1 = cashFlowStatement.get("Sales/Maturities of investments");
		Item k2 = cashFlowStatement.get("Other investing activities");
		
		if (k1 != null) {
			k = k1;
		} else if (k2 != null) {
			k = k2;
		} else {
			log.info("Empresa a monitorizar: " + name);
			log.info("Término que no aparece: Sales/Maturities of investments");
			log.info("Término que no aparece: Other investing activities");
			k = utils.controlNull(cashFlowStatement.get("Sales/Maturities of investments"), getYear());
		}
				
		return k;
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
