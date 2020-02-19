package adarga.external;

import java.io.IOException;
import java.sql.Connection;
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

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import adarga.analysis.Ratios;
import adarga.external.CompanyProfile.Profile;
import adarga.external.Storage.FinancialModelingPrepUrl;
import adarga.getinfo.DB;
import adarga.getinfo.DBOne;
import adarga.utils.TableSet;

public class CompanyHub {
	private static final Logger log = Logger.getLogger(CompanyHub.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    int rows = 7936;
	int batch = 1;
	int batchFCFY = 500;
	
	/*
	 * 	Get the list of companies that you can analyze provided by
	 * 	financialmodelingprep.com
	 */
	
	public JSONArray getCompaniesList() throws IOException {
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
		return array;
	}
	

	
@SuppressWarnings("static-access")
public String setCompanies(DBOne one) throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		JSONArray companies = getCompaniesList();
		
		// Get 'round' that is the cursor that selects the company that
		// you will analyze from the complete list
			
		int round = one.getRound();
		if ((round + batch) > rows) {
			round = 0;
		} 
		
		// For each company inside the batch batch
		
		for (int i = round; i < round + batch; i++) {
			
			log.info("Nº de Empresa: " + i);
			
			JSONObject json = new JSONObject(companies.get(i).toString());
			String symbol = json.getString("symbol");
			
			
			// Create a table with all the registers from the company
			// that already exists
			
			String exists = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts "
					+ "where symbol = '" + symbol + "'";
			ResultSet rs = one.ExecuteSELECT(exists);
			
			List<TableSet> companyRegisters = new ArrayList();
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
				
				// companyData is the profile data from the company to get data from
				// companyRegisters are all registers from the database
				
				is.storeReport(companyData, one, companyRegisters);
				//bs.storeReport(companyData, one, companyRegisters);
				//cs.storeReport(companyData, one, companyRegisters);
			}	
			
			one.setRound(i + 1);
		}
		
		
		
		return "ok";
	
	}
	
	
	
	@SuppressWarnings("static-access")
	public String setFCFY(DBOne one) throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		JSONArray array = getCompaniesList();
		
		
		int round = one.getRoundFCFY();
		if ((round + batch) > rows) {
			round = 0;
		} 
			
		List<String> SQLList = new ArrayList<String>();
		// Para cada empresa en el batch
		for (int i = round; i < round + batchFCFY; i++) {
			
			log.info("Nº de Empresa FCFY: " + i);
			
			JSONObject json = new JSONObject(array.get(i).toString());
			String symbol = json.getString("symbol");
			
			String exists = "SELECT symbol, concept, finantialDate FROM apiadbossDB.FCFYield "
					+ "where symbol = '" + symbol + "'";
			ResultSet rs = one.ExecuteSELECT(exists);
			TableSet companyRegister = new TableSet();
			List<TableSet> companyRegisters = new ArrayList();
			while (rs.next()) {
				companyRegister.setSymbol(rs.getString("symbol"));
				companyRegister.setConcept(rs.getString("concept"));
				companyRegister.setFinantialDate(rs.getString("finantialDate"));
				
				companyRegisters.add(companyRegister);
			}
			rs.close();
			Ratios ratio = new Ratios();
			
			Profile profile = new CompanyProfile().getProfile(symbol);
			String companyName = profile.getCompanyName().replaceAll("'", "");
			String sector = profile.getSector().replaceAll("'", "");
			String industry = profile.getIndustry().replaceAll("'", "");
			String description = profile.getDescription();
			description = description.replaceAll("'", "");
			String price = profile.getPrice();
			String mktCap = profile.getMktCap();
			
			if ((!sector.equals("")) && (!sector.equals("Financial Services"))) {
				
				HashMap<String, String> companyData = new HashMap<String, String>();
				companyData.put("symbol", symbol);
				companyData.put("companyName", companyName);
				companyData.put("sector", sector);
				companyData.put("industry", industry);
				companyData.put("description", description);
				companyData.put("price", price);
				companyData.put("mktCap", mktCap);
				
				String SQL = ratio.setFCFYield(companyData, one, companyRegisters);
				SQLList.add(SQL);
			}
			one.setRoundFCFY(round + 1);	
		}
		one.close();
		
		DB db2 = new DB();
		Iterator<String> iter = SQLList.iterator();
		while (iter.hasNext()) {
			String SQL = iter.next();
			log.info(SQL);
			db2.addBatch(SQL);	
		}
		int[] result = db2.executeBatch();
		db2.close();	
		
		return "ok";
	
	}
	
	public static void main(String[] args) {
		

	}

}
