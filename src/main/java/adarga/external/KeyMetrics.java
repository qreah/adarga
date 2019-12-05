package adarga.external;

import java.io.IOException;
import java.lang.reflect.Type;
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
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.DB;
import adarga.utis.qreah;



public class KeyMetrics {
	private static final Logger log = Logger.getLogger(KeyMetrics.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/company-key-metrics/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    private static HashMap<String, Metrics> years = new HashMap<String, Metrics>();
    private static Profile profile;
    
	

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
		log.info(json);
		JSONObject j = new JSONObject(json);
		if (j.has("metrics")) {
			result = true;
			JSONArray metrics = j.getJSONArray("metrics");
			Gson gson = new Gson();
			Iterator<Object> iter = metrics.iterator();
			while (iter.hasNext()) {
				JSONObject metricSet = (JSONObject) iter.next();
				log.info(metricSet.toString());
				Metrics ratios = gson.fromJson(metricSet.toString(), Metrics.class);
				String finDate = st.finDateConversion(ratios.getDate());
				log.info(ratios.toString());
				
				years.put(finDate, ratios);
			}
		}
		
		
		profile = new CompanyProfile().getProfile(symbol);
		
		return result;
		
	}
	
	static public HashMap<String, String> getMetricsList(Metrics metrics) {
		HashMap<String, String> list = new HashMap<String, String>();
		list.put("revenuePerShare", 
				metrics.getRevenuePerShare());
		list.put("freeCashFlowPerShare", 
				metrics.getFreeCashFlowPerShare());
		list.put("PERatio", 
				metrics.getPERatio());
		list.put("priceToSalesRatio", 
				metrics.getPriceToSalesRatio());
		list.put("freeCashFlowYield", 
				metrics.getFreeCashFlowYield());
		list.put("debtToAssets", 
				metrics.getDebtToAssets());
		list.put("dividendYield", 
				metrics.getDividendYield());
		list.put("payoutRatio", 
				metrics.getPayoutRatio());
		list.put("SGAToRevenue", 
				metrics.getSGAToRevenue());
		list.put("RDToRevenue", 
				metrics.getRDToRevenue());
		list.put("GrahamNumber", 
				metrics.getGrahamNumber());
		list.put("returnOnTangibleAssets", 
				metrics.getReturnOnTangibleAssets());
		list.put("ROE", 
				metrics.getROE());
		list.put("CapexToOperatingCashFlow", 
				metrics.getCapexToOperatingCashFlow());
		return list;
	}
	
	public static void storeReport(HashMap<String, String> companyData) throws IOException, ClassNotFoundException, ServletException, SQLException {
		String symbol = companyData.get("symbol");
		boolean exists = execute(symbol);
		if (exists) {
			Storage st = new Storage();
			Set<String> keys = years.keySet();
			
			Iterator<String> iter = keys.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				Metrics metrics = years.get(key);
				HashMap<String, String> ratiosList = getMetricsList(metrics);
				DB db = new DB();
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
					
					String SQL = st.storeRow(companyData, concept, ratio, key, "KeyMetrics");	
					db.addBatch(SQL);
				}
				db.executeBatch();
				db.close();
			}
		}
		
	}
	
	
	
	static public class Metrics {
		
		@SerializedName("date") private String date;
		@SerializedName("Revenue per Share") private String revenuePerShare;
		@SerializedName("Free Cash Flow per Share") private String freeCashFlowPerShare;
		@SerializedName("PE ratio") private String PERatio;
		@SerializedName("Price to Sales Ratio") private String priceToSalesRatio;
		@SerializedName("Free Cash Flow Yield") private String freeCashFlowYield;
		@SerializedName("Debt to Assets") private String debtToAssets;
		@SerializedName("Dividend Yield") private String dividendYield;
		@SerializedName("Payout Ratio") private String payoutRatio;
		@SerializedName("SG&A to Revenue") private String SGAToRevenue;
		@SerializedName("R&D to Revenue") private String RDToRevenue;
		@SerializedName("Graham Number") private String GrahamNumber;
		@SerializedName("Return on Tangible Assets") private String returnOnTangibleAssets;
		@SerializedName("ROE") private String ROE;
		@SerializedName("Capex to Operating Cash Flow") private String CapexToOperatingCashFlow;
		
		public String category(String ratio) {
			String result = "";
			switch(ratio) {
				case "revenuePerShare":
					result = "Per share";
					break;
				case "freeCashFlowPerShare":
					result = "Per share";
					break;
				case "PERatio":
					result = "Valuation";
					break;
				case "priceToSalesRatio":
					result = "Valuation";
					break;
				case "freeCashFlowYield":
					result = "Valuation";
					break;
				case "debtToAssets":
					result = "Debt";
					break;
				case "dividendYield":
					result = "Shareholder return";
					break;
				case "payoutRatio":
					result = "Shareholder return";
					break;
				case "SGAToRevenue":
					result = "Profitability";
					break;
				case "RDToRevenue":
					result = "Profitability";
					break;
				case "GrahamNumber":
					result = "Valuation";
					break;
				case "returnOnTangibleAssets":
					result = "Shareholder return";
					break;
				case "ROE":
					result = "Shareholder return";
					break;
				case "CapexToOperatingCashFlow":
					result = "Shareholder return";
					break;
					
			
			}
			
			return result;
		}
		
		@Override
		public String toString() {
			return "Metrics [date=" + date + ", revenuePerShare=" + revenuePerShare + ", freeCashFlowPerShare="
					+ freeCashFlowPerShare + ", PERatio=" + PERatio + ", priceToSalesRatio=" + priceToSalesRatio
					+ ", freeCashFlowYield=" + freeCashFlowYield + ", debtToAssets=" + debtToAssets + ", dividendYield="
					+ dividendYield + ", payoutRatio=" + payoutRatio + ", SGAToRevenue=" + SGAToRevenue
					+ ", RDToRevenue=" + RDToRevenue + ", GrahamNumber=" + GrahamNumber + ", returnOnTangibleAssets="
					+ returnOnTangibleAssets + ", ROE=" + ROE + ", CapexToOperatingCashFlow=" + CapexToOperatingCashFlow
					+ "]";
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getRevenuePerShare() {
			return revenuePerShare;
		}
		public void setRevenuePerShare(String revenuePerShare) {
			this.revenuePerShare = revenuePerShare;
		}
		public String getFreeCashFlowPerShare() {
			return freeCashFlowPerShare;
		}
		public void setFreeCashFlowPerShare(String freeCashFlowPerShare) {
			this.freeCashFlowPerShare = freeCashFlowPerShare;
		}
		public String getPERatio() {
			return PERatio;
		}
		public void setPERatio(String pERatio) {
			PERatio = pERatio;
		}
		public String getPriceToSalesRatio() {
			return priceToSalesRatio;
		}
		public void setPriceToSalesRatio(String priceToSalesRatio) {
			this.priceToSalesRatio = priceToSalesRatio;
		}
		public String getFreeCashFlowYield() {
			return freeCashFlowYield;
		}
		public void setFreeCashFlowYield(String freeCashFlowYield) {
			this.freeCashFlowYield = freeCashFlowYield;
		}
		public String getDebtToAssets() {
			return debtToAssets;
		}
		public void setDebtToAssets(String debtToAssets) {
			this.debtToAssets = debtToAssets;
		}
		public String getDividendYield() {
			return dividendYield;
		}
		public void setDividendYield(String dividendYield) {
			this.dividendYield = dividendYield;
		}
		public String getPayoutRatio() {
			return payoutRatio;
		}
		public void setPayoutRatio(String payoutRatio) {
			this.payoutRatio = payoutRatio;
		}
		public String getSGAToRevenue() {
			return SGAToRevenue;
		}
		public void setSGAToRevenue(String sGAToRevenue) {
			SGAToRevenue = sGAToRevenue;
		}
		public String getRDToRevenue() {
			return RDToRevenue;
		}
		public void setRDToRevenue(String rDToRevenue) {
			RDToRevenue = rDToRevenue;
		}
		public String getGrahamNumber() {
			return GrahamNumber;
		}
		public void setGrahamNumber(String grahamNumber) {
			GrahamNumber = grahamNumber;
		}
		public String getReturnOnTangibleAssets() {
			return returnOnTangibleAssets;
		}
		public void setReturnOnTangibleAssets(String returnOnTangibleAssets) {
			this.returnOnTangibleAssets = returnOnTangibleAssets;
		}
		public String getROE() {
			return ROE;
		}
		public void setROE(String rOE) {
			ROE = rOE;
		}
		public String getCapexToOperatingCashFlow() {
			return CapexToOperatingCashFlow;
		}
		public void setCapexToOperatingCashFlow(String capexToOperatingCashFlow) {
			CapexToOperatingCashFlow = capexToOperatingCashFlow;
		}
		
		
		
		
	}
	
	
	
	


	public static void main(String[] args) throws IOException, ClassNotFoundException, ServletException, SQLException {
		//storeReport("MMM");
		
		

	}

}
