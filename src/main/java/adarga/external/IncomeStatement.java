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

import adarga.getinfo.DB;
import adarga.getinfo.DBOne;
import adarga.utils.TableSet;

public class IncomeStatement {
	private static final Logger log = Logger.getLogger(IncomeStatement.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/financials/income-statement/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    private static HashMap<String, IS> years = new HashMap<String, IS>();
    
    
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
			while (iter.hasNext()) {
				JSONObject metricSet = (JSONObject) iter.next();
				IS ratios = gson.fromJson(metricSet.toString(), IS.class);
				String finDate = st.finDateConversion(ratios.getDate());
				years.put(finDate, ratios);
			}
		}
		
		return result;
		
	}
    
    
    static public HashMap<String, String> getMetricsList(IS metrics) {
		HashMap<String, String> list = new HashMap<String, String>();
		list.put("Revenue", 
				metrics.getRevenue());
		list.put("CostOfRevenue", 
				metrics.getCostOfRevenue());
		list.put("OperatingIncome", 
				metrics.getOperatingIncome());
		list.put("InterestExpense", 
				metrics.getInterestExpense());
		list.put("NetIncome", 
				metrics.getNetIncome());
		list.put("EPS", 
				metrics.getEPS());
		list.put("EPSDiluted", 
				metrics.getEPSDiluted());
		list.put("DividendPerShare", 
				metrics.getDividendPerShare());
		list.put("FreeCashFlowMargin", 
				metrics.getFreeCashFlowMargin());
		
		return list;
	}
    
    
    
    static public void storeReport(HashMap<String, String> companyData, 
    		DBOne one, 
    		List<TableSet> companyRegisters) throws IOException, ClassNotFoundException, ServletException, SQLException {
    	
    	String symbol = companyData.get("symbol");
    	boolean existsInAPI = execute(symbol);
    	if (!existsInAPI) {
    		log.info("ERROR: Income Statement doesn't exist");
    	}
    	
		if (existsInAPI) {
			List<String> SQLQuerys = new ArrayList<String>();
			Set<String> keys = years.keySet();
			Iterator<String> iter = keys.iterator();
			int batches = 0;
			while (iter.hasNext()) {
				String key = iter.next();
				IS metrics = years.get(key);
				HashMap<String, String> ratiosList = getMetricsList(metrics);
				Set<String> keysSet = ratiosList.keySet();
				
				Iterator<String> keyRatio = keysSet.iterator();
				while (keyRatio.hasNext()) {
					String concept = keyRatio.next();
					
					Double ratio = null;
					if (ratiosList.get(concept) != null) {
						if (!ratiosList.get(concept).equals("")) {
							ratio = Double.valueOf(ratiosList.get(concept));
						}
						
					}
					Storage stA = new Storage();
					String SQL = stA.SQLAddRow(companyData, concept, ratio, key, "Income Statement", one, companyRegisters);	
					
					SQLQuerys.add(SQL);	
				}
				
			}
			
			Iterator<String> iterSQL = SQLQuerys.iterator();
			Storage stB = new Storage();
			while (iterSQL.hasNext()) {
				stB.addBatch(iterSQL.next(), one);
			}
			stB.executeBatch(one);
			
		}
		
	}
    
    
    static public class IS {
    	
    	@SerializedName("date") private String date;
    	@SerializedName("Revenue") private String Revenue;
    	@SerializedName("Cost of Revenue") private String CostOfRevenue;
    	@SerializedName("Operating Income") private String OperatingIncome;
    	@SerializedName("Interest Expense") private String InterestExpense;
    	@SerializedName("Net Income") private String NetIncome;
    	@SerializedName("EPS") private String EPS;
    	@SerializedName("EPS Diluted") private String EPSDiluted;
    	@SerializedName("Dividend per Share") private String DividendPerShare;
    	@SerializedName("Free Cash Flow margin") private String FreeCashFlowMargin;
		
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getRevenue() {
			return Revenue;
		}
		public void setRevenue(String revenue) {
			Revenue = revenue;
		}
		public String getCostOfRevenue() {
			return CostOfRevenue;
		}
		public void setCostOfRevenue(String costOfRevenue) {
			CostOfRevenue = costOfRevenue;
		}
		public String getOperatingIncome() {
			return OperatingIncome;
		}
		public void setOperatingIncome(String operatingIncome) {
			OperatingIncome = operatingIncome;
		}
		public String getInterestExpense() {
			return InterestExpense;
		}
		public void setInterestExpense(String interestExpense) {
			InterestExpense = interestExpense;
		}
		public String getNetIncome() {
			return NetIncome;
		}
		public void setNetIncome(String netIncome) {
			NetIncome = netIncome;
		}
		public String getEPS() {
			return EPS;
		}
		public void setEPS(String ePS) {
			EPS = ePS;
		}
		public String getEPSDiluted() {
			return EPSDiluted;
		}
		public void setEPSDiluted(String ePSDiluted) {
			EPSDiluted = ePSDiluted;
		}
		public String getDividendPerShare() {
			return DividendPerShare;
		}
		public void setDividendPerShare(String dividendPerShare) {
			DividendPerShare = dividendPerShare;
		}
		public String getFreeCashFlowMargin() {
			return FreeCashFlowMargin;
		}
		public void setFreeCashFlowMargin(String freeCashFlowMargin) {
			FreeCashFlowMargin = freeCashFlowMargin;
		}
		
		
		
    }

	public static void main(String[] args) {
		

	}

}
