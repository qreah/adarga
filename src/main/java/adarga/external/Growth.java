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

import adarga.external.FinantialRatios.Ratios;
import adarga.external.KeyMetrics.Metrics;

public class Growth {
	
	private static final Logger log = Logger.getLogger(Growth.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/financial-statement-growth/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    private static HashMap<String, GrowthMetrics> years = new HashMap<String, GrowthMetrics>();
    
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
		if (j.has("growth")) {
			result = true;
			JSONArray gmetrics = j.getJSONArray("growth");
			Gson gson = new Gson();
			Iterator<Object> iter = gmetrics.iterator();
			while (iter.hasNext()) {
				JSONObject gmetricSet = (JSONObject) iter.next();
				GrowthMetrics ratios = gson.fromJson(gmetricSet.toString(), GrowthMetrics.class);
				String finDate = st.finDateConversion(ratios.getDate());
				years.put(finDate, ratios);
			}
		}
		
		return result;
		
    }
    
    static public HashMap<String, String> getMetricsList(GrowthMetrics metrics) {
		HashMap<String, String> list = new HashMap<String, String>();
		list.put("GrossProfitGrowth", 
				metrics.getGrossProfitGrowth());
		list.put("OperatingIncomeGrowth", 
				metrics.getOperatingIncomeGrowth());
		list.put("NetIncomeGrowth", 
				metrics.getNetIncomeGrowth());
		list.put("EPSGrowth", 
				metrics.getEPSGrowth());
		list.put("EPSDilutedGrowth", 
				metrics.getEPSDilutedGrowth());
		list.put("DividendsPerShareGrowth", 
				metrics.getDividendsPerShareGrowth());
		list.put("FreeCashFlowgrowth", 
				metrics.getFreeCashFlowgrowth());
		list.put("_10YRevenueGrowthPerShare", 
				metrics.get_10YRevenueGrowthPerShare());
		list.put("_5YRevenueGrowthPerShare", 
				metrics.get_5YRevenueGrowthPerShare());
		list.put("_3YRevenueGrowthPerShare", 
				metrics.get_3YRevenueGrowthPerShare());
		list.put("_10YOperatingCFGrowthPerShare", 
						metrics.get_10YOperatingCFGrowthPerShare());
		list.put("_5YOperatingCFGrowthPerShare", 
				metrics.get_5YOperatingCFGrowthPerShare());
		list.put("_3YOperatingCFGrowthPerShare", 
				metrics.get_3YOperatingCFGrowthPerShare());
		list.put("_10YDividendPerShareGrowthPerShare", 
				metrics.get_10YDividendPerShareGrowthPerShare());
		list.put("_5YDividendPerShareGrowthPerShare", 
				metrics.get_5YDividendPerShareGrowthPerShare());
		list.put("_3YDividendPerShareGrowthPerShare", 
				metrics.get_3YDividendPerShareGrowthPerShare());
		list.put("DebtGrowth", 
				metrics.getDebtGrowth());
		list.put("RDExpenseGrowth", 
				metrics.getRDExpenseGrowth());
		list.put("SGAExpensesGrowth", 
				metrics.getSGAExpensesGrowth());
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
				GrowthMetrics metrics = years.get(key);
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
    
    static public class GrowthMetrics {
    	
    	@Key("date") private String date;
		@Key("Gross Profit Growth") private String GrossProfitGrowth;
		@Key("Operating Income Growth") private String OperatingIncomeGrowth;
		@Key("Net Income Growth") private String NetIncomeGrowth;
		@Key("EPS Growth") private String EPSGrowth;
		@Key("EPS Diluted Growth") private String EPSDilutedGrowth;
		@Key("Dividends per Share Growth") private String DividendsPerShareGrowth;
		@Key("Free Cash Flow growth") private String FreeCashFlowgrowth;
		@Key("10Y Revenue Growth (per Share)") private String _10YRevenueGrowthPerShare;
		@Key("5Y Revenue Growth (per Share)") private String _5YRevenueGrowthPerShare;
		@Key("3Y Revenue Growth (per Share)") private String _3YRevenueGrowthPerShare;
		@Key("10Y Operating CF Growth (per Share)") private String _10YOperatingCFGrowthPerShare;
		@Key("5Y Operating CF Growth (per Share)") private String _5YOperatingCFGrowthPerShare;
		@Key("3Y Operating CF Growth (per Share)") private String _3YOperatingCFGrowthPerShare;
		@Key("10Y Dividend per Share Growth (per Share)") private String _10YDividendPerShareGrowthPerShare;
		@Key("5Y Dividend per Share Growth (per Share)") private String _5YDividendPerShareGrowthPerShare;
		@Key("3Y Dividend per Share Growth (per Share)") private String _3YDividendPerShareGrowthPerShare;
		@Key("Debt Growth") private String DebtGrowth;
		@Key("R&D Expense Growth") private String RDExpenseGrowth;
		@Key("SG&A Expenses Growth") private String SGAExpensesGrowth;
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		
		public String getGrossProfitGrowth() {
			return GrossProfitGrowth;
		}
		public void setGrossProfitGrowth(String grossProfitGrowth) {
			GrossProfitGrowth = grossProfitGrowth;
		}
		public String getOperatingIncomeGrowth() {
			return OperatingIncomeGrowth;
		}
		public void setOperatingIncomeGrowth(String operatingIncomeGrowth) {
			OperatingIncomeGrowth = operatingIncomeGrowth;
		}
		public String getNetIncomeGrowth() {
			return NetIncomeGrowth;
		}
		public void setNetIncomeGrowth(String netIncomeGrowth) {
			NetIncomeGrowth = netIncomeGrowth;
		}
		public String getEPSGrowth() {
			return EPSGrowth;
		}
		public void setEPSGrowth(String ePSGrowth) {
			EPSGrowth = ePSGrowth;
		}
		public String getEPSDilutedGrowth() {
			return EPSDilutedGrowth;
		}
		public void setEPSDilutedGrowth(String ePSDilutedGrowth) {
			EPSDilutedGrowth = ePSDilutedGrowth;
		}
		public String getDividendsPerShareGrowth() {
			return DividendsPerShareGrowth;
		}
		public void setDividendsPerShareGrowth(String dividendsPerShareGrowth) {
			DividendsPerShareGrowth = dividendsPerShareGrowth;
		}
		public String getFreeCashFlowgrowth() {
			return FreeCashFlowgrowth;
		}
		public void setFreeCashFlowgrowth(String freeCashFlowgrowth) {
			FreeCashFlowgrowth = freeCashFlowgrowth;
		}
		public String get_10YRevenueGrowthPerShare() {
			return _10YRevenueGrowthPerShare;
		}
		public void set_10YRevenueGrowthPerShare(String _10yRevenueGrowthPerShare) {
			_10YRevenueGrowthPerShare = _10yRevenueGrowthPerShare;
		}
		public String get_5YRevenueGrowthPerShare() {
			return _5YRevenueGrowthPerShare;
		}
		public void set_5YRevenueGrowthPerShare(String _5yRevenueGrowthPerShare) {
			_5YRevenueGrowthPerShare = _5yRevenueGrowthPerShare;
		}
		public String get_3YRevenueGrowthPerShare() {
			return _3YRevenueGrowthPerShare;
		}
		public void set_3YRevenueGrowthPerShare(String _3yRevenueGrowthPerShare) {
			_3YRevenueGrowthPerShare = _3yRevenueGrowthPerShare;
		}
		public String get_10YOperatingCFGrowthPerShare() {
			return _10YOperatingCFGrowthPerShare;
		}
		public void set_10YOperatingCFGrowthPerShare(String _10yOperatingCFGrowthPerShare) {
			_10YOperatingCFGrowthPerShare = _10yOperatingCFGrowthPerShare;
		}
		public String get_5YOperatingCFGrowthPerShare() {
			return _5YOperatingCFGrowthPerShare;
		}
		public void set_5YOperatingCFGrowthPerShare(String _5yOperatingCFGrowthPerShare) {
			_5YOperatingCFGrowthPerShare = _5yOperatingCFGrowthPerShare;
		}
		public String get_3YOperatingCFGrowthPerShare() {
			return _3YOperatingCFGrowthPerShare;
		}
		public void set_3YOperatingCFGrowthPerShare(String _3yOperatingCFGrowthPerShare) {
			_3YOperatingCFGrowthPerShare = _3yOperatingCFGrowthPerShare;
		}
		public String get_10YDividendPerShareGrowthPerShare() {
			return _10YDividendPerShareGrowthPerShare;
		}
		public void set_10YDividendPerShareGrowthPerShare(String _10yDividendPerShareGrowthPerShare) {
			_10YDividendPerShareGrowthPerShare = _10yDividendPerShareGrowthPerShare;
		}
		public String get_5YDividendPerShareGrowthPerShare() {
			return _5YDividendPerShareGrowthPerShare;
		}
		public void set_5YDividendPerShareGrowthPerShare(String _5yDividendPerShareGrowthPerShare) {
			_5YDividendPerShareGrowthPerShare = _5yDividendPerShareGrowthPerShare;
		}
		public String get_3YDividendPerShareGrowthPerShare() {
			return _3YDividendPerShareGrowthPerShare;
		}
		public void set_3YDividendPerShareGrowthPerShare(String _3yDividendPerShareGrowthPerShare) {
			_3YDividendPerShareGrowthPerShare = _3yDividendPerShareGrowthPerShare;
		}
		public String getDebtGrowth() {
			return DebtGrowth;
		}
		public void setDebtGrowth(String debtGrowth) {
			DebtGrowth = debtGrowth;
		}
		public String getRDExpenseGrowth() {
			return RDExpenseGrowth;
		}
		public void setRDExpenseGrowth(String rDExpenseGrowth) {
			RDExpenseGrowth = rDExpenseGrowth;
		}
		public String getSGAExpensesGrowth() {
			return SGAExpensesGrowth;
		}
		public void setSGAExpensesGrowth(String sGAExpensesGrowth) {
			SGAExpensesGrowth = sGAExpensesGrowth;
		}
		
		
		
		
		
    }

	public static void main(String[] args) {
		

	}

}
