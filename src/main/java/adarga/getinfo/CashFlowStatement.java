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
	
	public Item NetCashProvidedByOperatingActivities() {
		Item NetCashProvidedByOperatingActivities = cashFlowStatement.get("Net cash provided by operating activities");
		if (NetCashProvidedByOperatingActivities != null) {
			
		} else {
			NetCashProvidedByOperatingActivities = cashFlowStatement.get("Net cash provided by operating activities");
		}
		
		return NetCashProvidedByOperatingActivities;
	}
	
	public Item FCF() {
		Item FCF = cashFlowStatement.get("Free cash flow");
		if (FCF != null) {
			
		} else {
			FCF = cashFlowStatement.get("Free cash flow");
		}
		
		return FCF;
	}
	
	public Item DividendPaid() {
		Item DividendPaid = cashFlowStatement.get("Dividend paid");
		if (DividendPaid != null) {
			
		} else {
			DividendPaid = cashFlowStatement.get("Dividend paid");
		}
		
		return DividendPaid;
	}
	
	
	public Item NetCashUsedForInvestingActivities() {
		Item NC = cashFlowStatement.get("Net cash used for investing activities");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Net cash used for investing activities");
		}
		
		return NC;
	}
	
	public Item InvestmentsInPropertyPlantAndEquipment() {
		Item NC = cashFlowStatement.get("Investments in property, plant, and equipment");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Investments in property, plant, and equipment");
		}
		
		return NC;
	}
	
	public Item NetCashProvidedByUsedForFinancingActivities() {
		Item NC = cashFlowStatement.get("Net cash provided by (used for) financing activities");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Net cash provided by (used for) financing activities");
		}
		
		return NC;
	}
	
	public Item PurchasesOfInvestments() {
		Item NC = cashFlowStatement.get("Purchases of investments");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Purchases of investments");
		}
		
		return NC;
	}
	
	public Item CommonStockIssued() {
		Item NC = cashFlowStatement.get("Common stock issued");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Common stock issued");
		}
		
		return NC;
	}
	
	
	public Item DebtRepayment() {
		Item NC = cashFlowStatement.get("Debt repayment");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Debt repayment");
		}
		
		return NC;
	}
	
	public Item DebtIssued() {
		Item NC = cashFlowStatement.get("Debt issued");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Debt issued");
		}
		
		return NC;
	}
	
	public Item CommonStockRepurchased() {
		Item NC = cashFlowStatement.get("Common stock repurchased");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Common stock repurchased");
		}
		
		return NC;
	}
	
	
	public Item AcquisitionsNet() {
		Item NC = cashFlowStatement.get("Acquisitions, net");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Acquisitions, net");
		}
		
		return NC;
	}
	
	public Item PropertyPlantAndEquipmentReductions() {
		Item NC = cashFlowStatement.get("Property, plant, and equipment reductions");
		if (NC != null) {
			
		} else {
			NC = cashFlowStatement.get("Property, plant, and equipment reductions");
		}
		
		return NC;
	}
	
	
	
	public Item SecuritiesInvestment() {
		Item investments = cashFlowStatement.get("Sales/Maturities of investments");
		Utils utils = new Utils();
		if (investments != null) {
			
		} else {
			investments = utils.controlNull(investments, cashFlowStatement.get("Inventory").lastYear());
		}
				
		return investments;
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
