package adarga;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import adarga.BalanceSheet.FinancialModelingPrepUrl;

public class AutomaticValuation {
	private static final Logger log = Logger.getLogger(BalanceSheet.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    
    public void getCompanies() throws IOException, SQLException, ClassNotFoundException, ServletException {
    	DB db = new DB();
		String urlRaw = "https://api.adarga.org/sp500.json";
		HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest request) -> {
	        request.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		FinancialModelingPrepUrl url = new FinancialModelingPrepUrl(urlRaw);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = (HttpResponse)request.execute();
		String resString = res.parseAsString();
		JSONArray array = new JSONArray(resString);
		
		int round = db.getRound();
		int batch = 50;
		if ((round + batch) > 500) {
			batch = 500 - round;
			db.setRound(0);
		} else {
			db.setRound(round + batch);
		}
		log.info("last round: " + round + batch);
		for (int i = round; i < round + batch +1; i++) {
			JSONObject json = new JSONObject(array.get(i).toString());
			String Name = json.getString("Name").replaceAll("'", "");
			String Sector = json.getString("Sector");		
			String Symbol = json.getString("Symbol");
			log.info("Copmany: " + Symbol);
			BalanceSheet bs = new BalanceSheet();
			IncomeStatement is = new IncomeStatement();
			CashFlowStatement cs = new CashFlowStatement();
			Company com = new Company();
			try {
				com.getFinancialStatements(Symbol);
			
				CompanyInformation ci = new CompanyInformation();
				try {
					JSONObject result = com.analysis(bs, is, cs, ci);
					String salesGrowth = result.get("salesGrowth").toString();
					String NOPATGrowth = result.get("NOPATGrowth").toString();
					String NOPATMargin = result.get("NOPATMargin").toString();
					String netWorkingCapitalOverRevenues = result.get("netWorkingCapitalOverRevenues").toString();
					String netLongTermAssetsOverRevenue = result.get("netLongTermAssetsOverRevenue").toString();
					String netDebtToCapitalRatio = result.get("netDebtToCapitalRatio").toString();
					String afterTaxCostOfDebt = result.get("afterTaxCostOfDebt").toString();
					String OperatingROA = result.get("OperatingROA").toString();
					String salesOverNetAssets = result.get("salesOverNetAssets").toString();
					String ROE = result.get("ROE").toString();
					String returnOnTangibleEquity = result.get("returnOnTangibleEquity").toString();
					String growthIncome = result.get("growthIncome").toString();
					String growthOperatingIncome = result.get("growthOperatingIncome").toString();
					String g10Years = result.get("g10Years").toString();
					String SQL = "";
					if (db.companyIncluded(Symbol)) {
						SQL = "UPDATE apiadbossDB.CompanyValuation "
								+ "SET salesGrowth = '" + salesGrowth
								+ "' , NOPATGrowth = '" + NOPATGrowth
								+ "' , NOPATMargin = '" + NOPATMargin
								+ "' , netWorkingCapitalOverRevenues = '" + netWorkingCapitalOverRevenues
								+ "' , netLongTermAssetsOverRevenue = '" + netLongTermAssetsOverRevenue
								+ "' , netDebtToCapitalRatio = '" + netDebtToCapitalRatio
								+ "' , afterTaxCostOfDebt = '" + afterTaxCostOfDebt
								+ "' , OperatingROA = '" + OperatingROA
								+ "' , salesOverNetAssets = '" + salesOverNetAssets
								+ "' , ROE = '" + ROE
								+ "' , returnOnTangibleEquity = '" + returnOnTangibleEquity
								+ "' , growthIncome = '" + growthIncome
								+ "' , growthOperatingIncome = '" + growthOperatingIncome
								+ "' , g10Years = '" + g10Years
								+ "' WHERE Symbol = '" + Symbol + "'";
					} else {
						SQL = "INSERT INTO apiadbossDB.CompanyValuation "
								+ "	(Symbol, Sector, Company, salesGrowth, NOPATGrowth, NOPATMargin, "
								+ "netWorkingCapitalOverRevenues, netLongTermAssetsOverRevenue, netDebtToCapitalRatio, "
								+ "afterTaxCostOfDebt, OperatingROA, salesOverNetAssets, ROE, returnOnTangibleEquity,"
								+ "growthIncome, growthOperatingIncome, g10Years)"
								+ " VALUES ('" + Symbol + "', '" + Sector + "', '" + Name + "', '" + salesGrowth 
								+ "', '" + NOPATGrowth + "', '" + NOPATMargin + "', '" + netWorkingCapitalOverRevenues 
								+ "', '" + netLongTermAssetsOverRevenue 
								+ "', '" + netDebtToCapitalRatio 
								+ "', '" + afterTaxCostOfDebt + "', '" + OperatingROA + "', '" + salesOverNetAssets 
								+ "', '" + ROE 
								+ "', '" + returnOnTangibleEquity + "', '" + growthIncome + "', '" + growthOperatingIncome 
								+ "', '" + g10Years + "')";
								 
					}
					
					db.Execute(SQL);
				} catch (Exception e) {
					log.info("Error | Company: " + Symbol);
				}
			} catch (Exception e) {
				log.info("Error: Company Information. com.google.apphosting.api.DeadlineExceededException | Company: " + Symbol);
			}
		}
		
	}
}
