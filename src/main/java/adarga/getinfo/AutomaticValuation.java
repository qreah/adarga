package adarga.getinfo;

import java.io.IOException;
import java.sql.SQLException;
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
    
    public void getCompanies() throws IOException, SQLException, ClassNotFoundException, ServletException {	
		
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
		DB db = new DB();
		int round = db.getRound();
		int batch = 50;
		if ((round + batch) > 500) {
			batch = 500 - round;
			db.setRound(0);
		} else {
			db.setRound(round + batch);
		}
		
		
		
		
		for (int i = round; i < round + batch +1; i++) {
			DB db2 = new DB();
			JSONObject json = new JSONObject(array.get(i).toString());
			String Name = json.getString("Name").replaceAll("'", "");
			String Sector = json.getString("Sector");		
			String Symbol = json.getString("Symbol");
			
			BalanceSheet bs = new BalanceSheet();
			IncomeStatement is = new IncomeStatement();
			CashFlowStatement cs = new CashFlowStatement();
			CompanyInformation ci = new CompanyInformation();
			
			Company com = new Company();
			
			List<Object> reports = com.getFinancialStatements(Symbol);
			
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
					
					String SQL = "";
					if (db.companyIncluded(Symbol)) {
						
						SQL = "UPDATE apiadbossDB.CompanyValuation "
								+ "SET analysis = '" + result.toString()
								+ "' WHERE Symbol = '" + Symbol + "'";
						
						
					} else {
						
						SQL = "INSERT INTO apiadbossDB.CompanyValuation "
								+ "	(Symbol, Sector, Company, analysis)"
								+ " VALUES ('" + Symbol + "', '" + Sector + "', '" + Name + "', '" + result.toString() + "')";
							 
					}
					
					db2.Execute(SQL);	
					
			}
		}
		
	}
}
