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

public class BalanceSheet {
	private static final Logger log = Logger.getLogger(BalanceSheet.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/financials/balance-sheet-statement/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    private static HashMap<String, BS> years = new HashMap<String, BS>();
    
    
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
				BS ratios = gson.fromJson(metricSet.toString(), BS.class);
				String finDate = st.finDateConversion(ratios.getDate());
				years.put(finDate, ratios);
			}
		}
		
		
		return result;
		
	}
    
    static public HashMap<String, String> getMetricsList(BS metrics) {
		HashMap<String, String> list = new HashMap<String, String>();
		list.put("CashAndShortTermInvestments", 
				metrics.getCashAndShortTermInvestments());
		list.put("Receivables", 
				metrics.getReceivables());
		list.put("Inventories", 
				metrics.getInventories());
		list.put("TotalCurrentAssets", 
				metrics.getTotalCurrentAssets());
		list.put("PropertyPlantEquipmentNet", 
				metrics.getPropertyPlantEquipmentNet());
		list.put("GoodwillAndIntangibleAssets", 
				metrics.getGoodwillAndIntangibleAssets());
		list.put("TotalNonCurrentAssets", 
				metrics.getTotalNonCurrentAssets());
		list.put("TotalAssets", 
				metrics.getTotalAssets());
		list.put("Payables", 
				metrics.getPayables());
		list.put("ShortTermDebt", 
				metrics.getShortTermDebt());
		list.put("TotalCurrentLiabilities", 
				metrics.getTotalCurrentLiabilities());
		list.put("LongTermDebt", 
				metrics.getLongTermDebt());
		list.put("TotalDebt", 
				metrics.getTotalDebt());
		list.put("DeferredRevenue", 
				metrics.getDeferredRevenue());
		list.put("TotalNonCurrentLiabilities", 
				metrics.getTotalNonCurrentLiabilities());
		list.put("TotalLiabilities", 
				metrics.getTotalLiabilities());
		list.put("TotalShareholdersEquity", 
				metrics.getTotalShareholdersEquity());
		list.put("NetDebt", 
				metrics.getNetDebt());
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
				BS metrics = years.get(key);
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
    
    
    
    static public class BS {
    	
		@Key("date") private String date;
		@Key("Cash and short-term investments") private String CashAndShortTermInvestments;
		@Key("Receivables") private String Receivables;
		@Key("Inventories") private String Inventories;
		@Key("Total current assets") private String TotalCurrentAssets;
		@Key("Property, Plant & Equipment Net") private String PropertyPlantEquipmentNet;
		@Key("Goodwill and Intangible Assets") private String GoodwillAndIntangibleAssets;
		@Key("Total non-current assets") private String TotalNonCurrentAssets;
		@Key("Total assets") private String TotalAssets;
		@Key("Payables") private String Payables;
		@Key("Short-term debt") private String ShortTermDebt;
		@Key("Total current liabilities") private String TotalCurrentLiabilities;
		@Key("Long-term debt") private String LongTermDebt;
		@Key("Total debt") private String TotalDebt;
		@Key("Deferred revenue") private String DeferredRevenue;
		@Key("Total non-current liabilities") private String TotalNonCurrentLiabilities;
		@Key("Total liabilities") private String TotalLiabilities;
		@Key("Total shareholders equity") private String TotalShareholdersEquity;
		@Key("Net Debt") private String NetDebt;
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getCashAndShortTermInvestments() {
			return CashAndShortTermInvestments;
		}
		public void setCashAndShortTermInvestments(String cashAndShortTermInvestments) {
			CashAndShortTermInvestments = cashAndShortTermInvestments;
		}
		public String getReceivables() {
			return Receivables;
		}
		public void setReceivables(String receivables) {
			Receivables = receivables;
		}
		public String getInventories() {
			return Inventories;
		}
		public void setInventories(String inventories) {
			Inventories = inventories;
		}
		public String getTotalCurrentAssets() {
			return TotalCurrentAssets;
		}
		public void setTotalCurrentAssets(String totalCurrentAssets) {
			TotalCurrentAssets = totalCurrentAssets;
		}
		public String getPropertyPlantEquipmentNet() {
			return PropertyPlantEquipmentNet;
		}
		public void setPropertyPlantEquipmentNet(String propertyPlantEquipmentNet) {
			PropertyPlantEquipmentNet = propertyPlantEquipmentNet;
		}
		public String getGoodwillAndIntangibleAssets() {
			return GoodwillAndIntangibleAssets;
		}
		public void setGoodwillAndIntangibleAssets(String goodwillAndIntangibleAssets) {
			GoodwillAndIntangibleAssets = goodwillAndIntangibleAssets;
		}
		public String getTotalNonCurrentAssets() {
			return TotalNonCurrentAssets;
		}
		public void setTotalNonCurrentAssets(String totalNonCurrentAssets) {
			TotalNonCurrentAssets = totalNonCurrentAssets;
		}
		public String getTotalAssets() {
			return TotalAssets;
		}
		public void setTotalAssets(String totalAssets) {
			TotalAssets = totalAssets;
		}
		public String getPayables() {
			return Payables;
		}
		public void setPayables(String payables) {
			Payables = payables;
		}
		public String getShortTermDebt() {
			return ShortTermDebt;
		}
		public void setShortTermDebt(String shortTermDebt) {
			ShortTermDebt = shortTermDebt;
		}
		public String getTotalCurrentLiabilities() {
			return TotalCurrentLiabilities;
		}
		public void setTotalCurrentLiabilities(String totalCurrentLiabilities) {
			TotalCurrentLiabilities = totalCurrentLiabilities;
		}
		public String getLongTermDebt() {
			return LongTermDebt;
		}
		public void setLongTermDebt(String longTermDebt) {
			LongTermDebt = longTermDebt;
		}
		public String getTotalDebt() {
			return TotalDebt;
		}
		public void setTotalDebt(String totalDebt) {
			TotalDebt = totalDebt;
		}
		public String getDeferredRevenue() {
			return DeferredRevenue;
		}
		public void setDeferredRevenue(String deferredRevenue) {
			DeferredRevenue = deferredRevenue;
		}
		public String getTotalNonCurrentLiabilities() {
			return TotalNonCurrentLiabilities;
		}
		public void setTotalNonCurrentLiabilities(String totalNonCurrentLiabilities) {
			TotalNonCurrentLiabilities = totalNonCurrentLiabilities;
		}
		public String getTotalLiabilities() {
			return TotalLiabilities;
		}
		public void setTotalLiabilities(String totalLiabilities) {
			TotalLiabilities = totalLiabilities;
		}
		public String getTotalShareholdersEquity() {
			return TotalShareholdersEquity;
		}
		public void setTotalShareholdersEquity(String totalShareholdersEquity) {
			TotalShareholdersEquity = totalShareholdersEquity;
		}
		public String getNetDebt() {
			return NetDebt;
		}
		public void setNetDebt(String netDebt) {
			NetDebt = netDebt;
		}
		
		
		
		
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
