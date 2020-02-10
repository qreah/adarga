package adarga.external;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.DBOne;
import adarga.utils.TableSet;

public class JustOneCompanyHub {
	private static final Logger log = Logger.getLogger(JustOneCompanyHub.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    
    
@SuppressWarnings("static-access")
public String setCompanies(String symbol) throws ClassNotFoundException, ServletException, IOException, SQLException {
			
			
			String exists = "SELECT distinct symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts "
					+ "where symbol = '" + symbol + "'";

			DBOne one = new DBOne();
			one.ConnectDBOne();
			ResultSet rs = one.ExecuteSELECT(exists);
			
			List<TableSet> companyRegisters = new ArrayList<TableSet>();
			
			while (rs.next()) {
				String sym = rs.getString("symbol");
				String concept = rs.getString("concept");
				String finantialDate = rs.getString("finantialDate");
				TableSet companyRegister = new TableSet();
				companyRegister.setSymbol(sym);
				companyRegister.setConcept(concept);
				companyRegister.setFinantialDate(finantialDate);
				
				companyRegisters.add(companyRegister);

			}
			
			rs.close();
			
			BalanceSheet bs = new BalanceSheet();
			IncomeStatement is = new IncomeStatement();
			CashFlowStatement cs = new CashFlowStatement();
			Profile profile = new CompanyProfile().getProfile(symbol);
			String companyName = profile.getCompanyName().replaceAll("'", "");
			String sector = profile.getSector().replaceAll("'", "");
			String industry = profile.getIndustry().replaceAll("'", "");
			String description = profile.getDescription();
			description = description.replaceAll("'", "");
			String price = profile.getPrice();
			String mktCap = profile.getMktCap();
			
			if ((!sector.equals("")) 
					&& (!sector.equals("Financial Services"))
						) {
				
				// Gets data from the company through the API 
				// only if it is not a financial company
				
				HashMap<String, String> companyData = new HashMap<String, String>();
				companyData.put("symbol", symbol);
				companyData.put("companyName", companyName);
				companyData.put("sector", sector);
				companyData.put("industry", industry);
				companyData.put("description", description);
				companyData.put("price", price);
				companyData.put("mktCap", mktCap);
				
				is.storeReport(companyData, one, companyRegisters);
				//bs.storeReport(companyData, one, companyRegisters);
				//cs.storeReport(companyData, one, companyRegisters);
				
			}	
		
		
		return "ok"; 	
	
	}
	
	public static void main(String[] args) {
		

	}

}
