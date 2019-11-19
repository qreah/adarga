package adarga.external;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONArray;
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
import com.google.api.client.util.Key;
import com.google.gson.Gson;

import adarga.external.IncomeStatement.IS;
import adarga.external.KeyMetrics.Metrics;

public class CashFlowStatement {
	
	private static final Logger log = Logger.getLogger(CashFlowStatement.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/financials/cash-flow-statement/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    private static HashMap<String, CS> years = new HashMap<String, CS>();
    
    
    public static boolean execute(String symbol) throws IOException {
		boolean result = false;
		String urlEndpointComposer = urlEndpoint + symbol + "?datatype=json";
		Storage st = new Storage();
		
		HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		JSONObject j = new JSONObject(json);
		if (j.has("financials")) {
			result = true;
			JSONArray metrics = j.getJSONArray("financials");
			Gson gson = new Gson();
			Iterator<Object> iter = metrics.iterator();
			while (iter.hasNext()) {
				JSONObject metricSet = (JSONObject) iter.next();
				CS ratios = gson.fromJson(metricSet.toString(), CS.class);
				String finDate = st.finDateConversion(ratios.getDate());
				years.put(finDate, ratios);
			}
		}
		
		
		return result;
		
	}
    
    
    static public HashMap<String, String> getMetricsList(CS metrics) {
		HashMap<String, String> list = new HashMap<String, String>();
		list.put("OperatingCashFlow", 
				metrics.getOperatingCashFlow());
		list.put("CapitalExpenditure", 
				metrics.getCapitalExpenditure());
		list.put("Inventories", 
				metrics.getInventories());
		list.put("AcquisitionsAndDisposals", 
				metrics.getAcquisitionsAndDisposals());
		list.put("InvestmentPurchasesAndSales", 
				metrics.getInvestmentPurchasesAndSales());
		list.put("InvestingCashFlow", 
				metrics.getInvestingCashFlow());
		list.put("IssuanceRepaymentOfDebt", 
				metrics.getIssuanceRepaymentOfDebt());
		list.put("IssuanceBuybacksOfShares", 
				metrics.getIssuanceBuybacksOfShares());
		list.put("DividendPayments", 
				metrics.getDividendPayments());
		list.put("FinancingCashFlow", 
				metrics.getFinancingCashFlow());
		list.put("NetCashFlow_ChangeInCash", 
				metrics.getNetCashFlow_ChangeInCash());
		list.put("FreeCashFlow", 
				metrics.getFreeCashFlow());
		list.put("EffectOfForexChangesOnCash", 
				metrics.getEffectOfForexChangesOnCash());
		
		return list;
	}
    
    public static void storeReport(String symbol) throws IOException, ClassNotFoundException, ServletException, SQLException {
		boolean exists = execute(symbol);
		if (exists) {
			Storage st = new Storage();
			Set<String> keys = years.keySet();
			
			Iterator<String> iter = keys.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				CS metrics = years.get(key);
				HashMap<String, String> ratiosList = getMetricsList(metrics);
				Set<String> keysSet = ratiosList.keySet();
				Iterator<String> keyRatio = keysSet.iterator();
				while (keyRatio.hasNext()) {
					String concept = keyRatio.next();
					log.info("value: " + ratiosList.get(concept));
					Double ratio = null;
					if (ratiosList.get(concept) != null) {
						if (!ratiosList.get(concept).equals("")) {
							ratio = Double.valueOf(ratiosList.get(concept));
						}
						
					}
					
					st.store(symbol, concept, ratio, key);	
				}
			}
		}
		
	}
    
    
    
    
    static public class CS {
    	
		@Key("date") private String date;
		@Key("Operating Cash Flow") private String OperatingCashFlow;
		@Key("Capital Expenditure") private String CapitalExpenditure;
		@Key("Inventories") private String Inventories;
		@Key("Acquisitions and disposals") private String AcquisitionsAndDisposals;
		@Key("Investment purchases and sales") private String InvestmentPurchasesAndSales;
		@Key("Investing Cash flow") private String InvestingCashFlow;
		@Key("Issuance (repayment) of debt") private String IssuanceRepaymentOfDebt;
		@Key("Issuance (buybacks) of shares") private String IssuanceBuybacksOfShares;
		@Key("Dividend payments") private String DividendPayments;
		@Key("Financing Cash Flow") private String FinancingCashFlow;
		@Key("Net cash flow / Change in cash") private String NetCashFlow_ChangeInCash;
		@Key("Free Cash Flow") private String FreeCashFlow;
		@Key("Effect of forex changes on cash") private String EffectOfForexChangesOnCash;
		
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getOperatingCashFlow() {
			return OperatingCashFlow;
		}
		public void setOperatingCashFlow(String operatingCashFlow) {
			OperatingCashFlow = operatingCashFlow;
		}
		public String getCapitalExpenditure() {
			return CapitalExpenditure;
		}
		public void setCapitalExpenditure(String capitalExpenditure) {
			CapitalExpenditure = capitalExpenditure;
		}
		public String getInventories() {
			return Inventories;
		}
		public void setInventories(String inventories) {
			Inventories = inventories;
		}
		public String getAcquisitionsAndDisposals() {
			return AcquisitionsAndDisposals;
		}
		public void setAcquisitionsAndDisposals(String acquisitionsAndDisposals) {
			AcquisitionsAndDisposals = acquisitionsAndDisposals;
		}
		public String getInvestmentPurchasesAndSales() {
			return InvestmentPurchasesAndSales;
		}
		public void setInvestmentPurchasesAndSales(String investmentPurchasesAndSales) {
			InvestmentPurchasesAndSales = investmentPurchasesAndSales;
		}
		public String getInvestingCashFlow() {
			return InvestingCashFlow;
		}
		public void setInvestingCashFlow(String investingCashFlow) {
			InvestingCashFlow = investingCashFlow;
		}
		public String getIssuanceRepaymentOfDebt() {
			return IssuanceRepaymentOfDebt;
		}
		public void setIssuanceRepaymentOfDebt(String issuanceRepaymentOfDebt) {
			IssuanceRepaymentOfDebt = issuanceRepaymentOfDebt;
		}
		public String getIssuanceBuybacksOfShares() {
			return IssuanceBuybacksOfShares;
		}
		public void setIssuanceBuybacksOfShares(String issuanceBuybacksOfShares) {
			IssuanceBuybacksOfShares = issuanceBuybacksOfShares;
		}
		public String getDividendPayments() {
			return DividendPayments;
		}
		public void setDividendPayments(String dividendPayments) {
			DividendPayments = dividendPayments;
		}
		public String getFinancingCashFlow() {
			return FinancingCashFlow;
		}
		public void setFinancingCashFlow(String financingCashFlow) {
			FinancingCashFlow = financingCashFlow;
		}
		public String getNetCashFlow_ChangeInCash() {
			return NetCashFlow_ChangeInCash;
		}
		public void setNetCashFlow_ChangeInCash(String netCashFlow_ChangeInCash) {
			NetCashFlow_ChangeInCash = netCashFlow_ChangeInCash;
		}
		public String getFreeCashFlow() {
			return FreeCashFlow;
		}
		public void setFreeCashFlow(String freeCashFlow) {
			FreeCashFlow = freeCashFlow;
		}
		public String getEffectOfForexChangesOnCash() {
			return EffectOfForexChangesOnCash;
		}
		public void setEffectOfForexChangesOnCash(String effectOfForexChangesOnCash) {
			EffectOfForexChangesOnCash = effectOfForexChangesOnCash;
		}
		
		
		
    }
    
    
	public static void main(String[] args) {
		

	}

}
