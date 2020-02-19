package adarga.external;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.google.gson.annotations.SerializedName;

import adarga.external.IncomeStatement.IS;
import adarga.getinfo.DB;
import adarga.getinfo.DBOne;
import adarga.utils.TableSet;
import adarga.utils.qreah;

public class BalanceSheet {
	private static final Logger log = Logger.getLogger(BalanceSheet.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/financials/balance-sheet-statement/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    private static HashMap<String, BS> years;
    
    public BalanceSheet() {
    	years = new HashMap<String, BS>();
    }
    
    public static boolean execute(String symbol) throws IOException, ClassNotFoundException, ServletException, SQLException {
		
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
			years.clear();
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
    
    
    
    
    public static void storeReport(HashMap<String, String> companyData, DBOne one, List<TableSet> companyRegisters) throws IOException, ClassNotFoundException, ServletException, SQLException {
		String symbol = companyData.get("symbol");
		
    	boolean exists = execute(symbol);
    	
		if (exists) {
			
			Set<String> keys = years.keySet();
			List<String> SQLQuerys = new ArrayList<String>();
			Iterator<String> iter = keys.iterator();
			// Itera para cada año
			
			
			int numYears = 0;
			while (iter.hasNext()) {
			//for (int m=0; m < keys.size(); m++) {
				String key = iter.next();
				if (numYears < keys.size()) {
					numYears++;
					
					BS metrics = years.get(key);
					HashMap<String, String> ratiosList = getMetricsList(metrics);
					Set<String> keysSet = ratiosList.keySet();
					Iterator<String> keyRatio = keysSet.iterator();
					
					// Itera para cada partida
					while (keyRatio.hasNext()) {
						String concept = keyRatio.next();
						String ratio = null;
						if (ratiosList.get(concept) != null) {
							if (!ratiosList.get(concept).equals("")) {
								ratio = ratiosList.get(concept).replace(".", ",");
							}
							
						}
						
						Storage stA = new Storage();
						String SQL = stA.SQLAddRow(companyData, concept, ratio, key, "Balance Sheet", one, companyRegisters);	
						SQLQuerys.add(SQL);	
						
					}
				}
			
				
			}
			Storage stB = new Storage();
			stB.store(SQLQuerys, one);
			
		}
		
	}
    
    
    
    static public class BS {
    	
		@SerializedName("date") private String date;
		@SerializedName("Cash and short-term investments") private String CashAndShortTermInvestments;
		@SerializedName("Receivables") private String Receivables;
		@SerializedName("Inventories") private String Inventories;
		@SerializedName("Total current assets") private String TotalCurrentAssets;
		@SerializedName("Property, Plant & Equipment Net") private String PropertyPlantEquipmentNet;
		@SerializedName("Goodwill and Intangible Assets") private String GoodwillAndIntangibleAssets;
		@SerializedName("Total non-current assets") private String TotalNonCurrentAssets;
		@SerializedName("Total assets") private String TotalAssets;
		@SerializedName("Payables") private String Payables;
		@SerializedName("Short-term debt") private String ShortTermDebt;
		@SerializedName("Total current liabilities") private String TotalCurrentLiabilities;
		@SerializedName("Long-term debt") private String LongTermDebt;
		@SerializedName("Total debt") private String TotalDebt;
		@SerializedName("Deferred revenue") private String DeferredRevenue;
		@SerializedName("Total non-current liabilities") private String TotalNonCurrentLiabilities;
		@SerializedName("Total liabilities") private String TotalLiabilities;
		@SerializedName("Total shareholders equity") private String TotalShareholdersEquity;
		@SerializedName("Net Debt") private String NetDebt;
		
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
