package adarga.getinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
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

import adarga.getinfo.BalanceSheet.FinancialModelingPrepUrl;

public class AutomaticValuation {
	private static final Logger log = Logger.getLogger(AutomaticValuation.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    int rows = 7936;
    
    public void getCompanies() throws IOException, SQLException, ClassNotFoundException, ServletException {	
		
		//String urlRaw = "https://api.adarga.org/tickers.json";
		String urlRaw = "https://financialmodelingprep.com/api/v3/company/stock/list";
		HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest request) -> {
	        request.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		FinancialModelingPrepUrl url = new FinancialModelingPrepUrl(urlRaw);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = (HttpResponse)request.execute();
		String resString = res.parseAsString();
		JSONObject jsonObject = new JSONObject(resString);
		JSONArray array = jsonObject.getJSONArray("symbolsList");
		DB db = new DB();
		int round = db.getRound();
		int batch = 80;
		if ((round + batch) > rows) {
			batch = rows - round;
			db.setRound(0);
		} else {
			db.setRound(round + batch);
		}
		
		
		log.info("round: " + round);
		int temp = round + batch +1;
		log.info("final: " + temp);
		
		for (int i = round; i < round + batch +1; i++) {
			log.info("i: " + i);
			if (i < rows)  {
				db.setRound(i + 1);
			} else {
				db.setRound(0);
			}
			
			DB db2 = new DB();
			JSONObject json = new JSONObject(array.get(i).toString());
			String Name = json.getString("name").replaceAll("'", "");
			//String Sector = json.getString("Sector");		
			String Symbol = json.getString("symbol");
			
			BalanceSheet bs = new BalanceSheet();
			IncomeStatement is = new IncomeStatement();
			CashFlowStatement cs = new CashFlowStatement();
			CompanyInformation ci = new CompanyInformation();
			
			Company com = new Company();
			//try {
			List<Object> reports = com.getFinancialStatements(Symbol);
		
			if (reports.get(0).equals("out")) {
			} else {
				
			
				switch(Symbol) {
				  case "BEN":  
				    break;
				  case "AIZ":
				    break;
				  case "DTE":
					break;
				  case "EW":
						break;
				  case "GRMN":
						break;
				  case "IPGP":
						break;
				  case "MCD":
						break;
				  case "SCG":
						break;
				  case "ALXN":
						break;
				  case "CVS":
						break;
				  case "HBI":
						break;
				  case "IRM":
						break;
				  case "STX":
						break;
				  case "AGN":
						break;
				  case "BDX":
						break;
				  case "EVHC":
						break;
				  case "HIG":
						break;
				  case "EXC":
						break;
				  case "AMP":
						break;
				  case "IR":
						break;
				  case "MAS":
						break;
				  case "DISCA":
						break;
				  case "FAST":
						break;
				  case "FL":
						break;
				  default:
				
					JSONObject result = new JSONObject();
					bs = (BalanceSheet) reports.get(0);
					is = (IncomeStatement) reports.get(1);
					cs = (CashFlowStatement) reports.get(2);
					ci = (CompanyInformation) reports.get(3);
					
					result = com.analysis(bs, is, cs, ci);
					
					Double FCFYield = (Double) result.getJSONObject("CM").get("FCFYield");
					String Industry = ci.companyInformation.get("industry");
					String Sector = ci.companyInformation.get("sector");
					String Description = ci.companyInformation.get("description");
					
					String SQL = "";
					if (db.companyIncluded(Symbol)) {
						
						SQL = "UPDATE apiadbossDB.CompanyValuation "
								+ "SET analysis = '" + result.toString()
								+ "', FCFYield = '" + FCFYield 
								+ "', Industry = '" + Industry 
								+ "', Sector = '" + Sector 
								+ "', Description = '" + Description 
								+ "', dateAnalized = NOW()" 
								+ " WHERE Symbol = '" + Symbol + "'";
						
						
					} else {
						
						SQL = "INSERT INTO apiadbossDB.CompanyValuation "
								+ "	(Symbol, Sector, Company, Industry, Description, analysis, FCFYield, dateAnalized)"
								+ " VALUES ('" + Symbol + "', '" + Sector + "', '" + Name + "', '" + Industry + "', '" + Description + "', '" + result.toString() + "' , '" +  FCFYield + "', NOW())";
							 
					}
					
					db2.Execute(SQL);	
					
				}
					
			}
		//} catch (Exception e) {
		//      log.info("No data: " + Symbol);
		//}
	}
		
	}
}
