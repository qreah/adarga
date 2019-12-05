package adarga.external;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

import adarga.analysis.Ratios;
import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.DB;

import adarga.utis.qreah;

public class Storage {
	private static final Logger log = Logger.getLogger(KeyMetrics.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
	int rows = 7936;
	int batch = 2;
	int batchFCFY = 20;
	static ResultSet exist;
	
	
	/**
	 *    DEPRECIATED
	 */
	
	public void store(HashMap<String, String> companyData, 
			String concept, 
			Double ratio, 
			String finYear,
			String type
			) throws ClassNotFoundException, SQLException, ServletException, IOException {
		
		
		String symbol = companyData.get("symbol");
		String companyName = companyData.get("companyName");
		String sector = companyData.get("sector");
		String industry = companyData.get("industry");
		String description = companyData.get("description");
		String price = companyData.get("price");
		String mktCap = companyData.get("mktCap");
		
		
		if (exists(symbol, concept, finYear)) {
		
			update(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		} else {
			insert(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		}
	}
	
	public String storeRow(HashMap<String, String> companyData, 
			String concept, 
			Double ratio, 
			String finYear,
			String type
			
			) throws ClassNotFoundException, SQLException, ServletException, IOException {
		
		
		String symbol = companyData.get("symbol");
		String companyName = companyData.get("companyName");
		String sector = companyData.get("sector");
		String industry = companyData.get("industry");
		String description = companyData.get("description");
		String price = companyData.get("price");
		String mktCap = companyData.get("mktCap");
		
		String SQL = "";
		//SQL = insertSQL(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		
		
		if (exists(symbol, concept, finYear)) {
		
			SQL = updateSQL(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		} else {
			SQL = insertSQL(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		}
		
		
		return SQL;
	}
	
	
	/**
	 *    DEPRECIATED
	 */
	
	static public void update(String symbol, 
			Double ratio, 
			String finYear, 
			String concept,
			String companyName,
			String sector,
			String industry,
			String description,
			String type
			) throws ClassNotFoundException, ServletException, IOException, SQLException {
		qreah q = new qreah();
		String SQL = "UPDATE apiadbossDB.adargaConcepts" + 
				" SET symbol='" + symbol 
				+ "', value= '" + ratio 
				+ "', concept= '" + concept 
				+ "', finantialDate='" + finYear 
				+ "', reportDate='" + q.today() 
				+ "', companyName='" + companyName 
				+ "', sector='" + sector 
				+ "', industry='" + industry 
				+ "', description='" + description 
				+ "', type='" + type 
				+ "'"
				+ " WHERE "
				+ "symbol='" + symbol + "' AND "
				+ "concept='" + concept + "' AND "
				+ "finantialDate='" + finYear 
				+ "';";
		DB db = new DB();
		
		db.Execute(SQL);
		db.close();
	}
	
	
	static public String updateSQL(String symbol, 
			Double ratio, 
			String finYear, 
			String concept,
			String companyName,
			String sector,
			String industry,
			String description,
			String type
			) throws ClassNotFoundException, ServletException, IOException, SQLException {
		qreah q = new qreah();
		String SQL = "UPDATE apiadbossDB.adargaConcepts" + 
				" SET symbol='" + symbol 
				+ "', value= '" + ratio 
				+ "', concept= '" + concept 
				+ "', finantialDate='" + finYear 
				+ "', reportDate='" + q.today() 
				+ "', companyName='" + companyName 
				+ "', sector='" + sector 
				+ "', industry='" + industry 
				+ "', description='" + description 
				+ "', type='" + type 
				+ "'"
				+ " WHERE "
				+ "symbol='" + symbol + "' AND "
				+ "concept='" + concept + "' AND "
				+ "finantialDate='" + finYear 
				+ "';";
		
		return SQL;
	}
	
	
	/**
	 *    DEPRECIATED
	 */
	
	
	static public void insert(String symbol, 
			Double ratio, 
			String finYear, 
			String concept,
			String companyName,
			String sector,
			String industry,
			String description,
			String type
			) throws ClassNotFoundException, ServletException, IOException, SQLException {
		qreah q = new qreah();
		String SQL = "INSERT INTO apiadbossDB.adargaConcepts" + 
				" VALUES ('" 
				+ symbol + "', '" 
				+ concept + "', '" 
				+ ratio + "', '" 
				+ finYear + "', '" 
				+ q.today()  + "', '" 
				+ companyName  + "', '" 
				+ sector  + "', '" 
				+ industry  + "', '" 
				+ description  + "', '" 
				+ type  
				+ "');";
		DB db = new DB();
		
		db.Execute(SQL);
		db.close();
	}
	
	
	static public String insertSQL(String symbol, 
			Double ratio, 
			String finYear, 
			String concept,
			String companyName,
			String sector,
			String industry,
			String description,
			String type
			) throws ClassNotFoundException, ServletException, IOException, SQLException {
		qreah q = new qreah();
		String SQL = "INSERT INTO apiadbossDB.adargaConcepts" + 
				" VALUES ('" 
				+ symbol + "', '" 
				+ concept + "', '" 
				+ ratio + "', '" 
				+ finYear + "', '" 
				+ q.today()  + "', '" 
				+ companyName  + "', '" 
				+ sector  + "', '" 
				+ industry  + "', '" 
				+ description  + "', '" 
				+ type  
				+ "');";
		
		return SQL;
	}
	
	
	
	static public boolean exists(String symbol, String concept, String finantialDate) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		//DB db = new DB();
		/*
		String SQL = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts"
				+ " where concept = '" + concept + "' AND symbol = '"
				+ symbol + "'  AND finantialDate = '" + finantialDate + "'";
		
		ResultSet rs = db.ExecuteSELECT(SQL);
		*/
		
		while (exist.next()) {
			String symbolStr = exist.getString("symbol");
			if (symbolStr.equals(symbol)) {
				String conceptStr = exist.getString("concept");
				if (conceptStr.equals(symbol)) {
					String finantialDateStr = exist.getString("finantialDate");
					if (finantialDateStr.equals(symbol)) {
						result = true;
					}
				}
			}
		}
		
		if (exist.next()) {
			
		}
		//db.close();
		return result;
	}
	
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
	public String getCompanies() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		JSONArray array = getCompaniesList();
		
		DB db = new DB();		
		int round = db.getRound();
		if ((round + batch) > rows) {
			batch = rows - round;
		} 
		db.setRound(round + 1);
		db.close();
		
		
		// Para cada empresa en el batch
		for (int i = round; i < round + batch; i++) {
			
			log.info("Nº de Empresa: " + round);
			round = i;
			JSONObject json = new JSONObject(array.get(i).toString());
			String Name = json.getString("name").replaceAll("'", "");	
			String symbol = json.getString("symbol");
			
			FinantialRatios fr = new FinantialRatios();
			KeyMetrics km = new KeyMetrics();
			Growth g = new Growth();
			BalanceSheet bs = new BalanceSheet();
			IncomeStatement is = new IncomeStatement();
			CashFlowStatement cs = new CashFlowStatement();
			Ratios ratio = new Ratios();
			qreah q = new qreah();
			
			Profile profile = new CompanyProfile().getProfile(symbol);
			String companyName = profile.getCompanyName().replaceAll("'", "");
			String sector = profile.getSector().replaceAll("'", "");
			String industry = profile.getIndustry().replaceAll("'", "");
			String description = profile.getDescription();
			description = description.replaceAll("'", "");
			String price = profile.getPrice();
			String mktCap = profile.getMktCap();
			
			String exists = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts group by symbol, concept, finantialDate";
			exist = db.ExecuteSELECT(exists);
			
			HashMap<String, String> companyData = new HashMap<String, String>();
			companyData.put("symbol", symbol);
			companyData.put("companyName", companyName);
			companyData.put("sector", sector);
			companyData.put("industry", industry);
			companyData.put("description", description);
			companyData.put("price", price);
			companyData.put("mktCap", mktCap);
			
			is.storeReport(companyData);
			bs.storeReport(companyData);
			cs.storeReport(companyData);
			//ratio.setFCFYield(companyData);
			fr.storeReport(companyData);
			km.storeReport(companyData);
			g.storeReport(companyData);
			
		}
		
		
		
		return "ok";
	
	}
	
	@SuppressWarnings("static-access")
	public String storeFCFY() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		JSONArray array = getCompaniesList();
		
		DB db = new DB();		
		int round = db.getRoundFCFY();
		if ((round + batchFCFY) > rows) {
			batchFCFY = rows - round;
		} 
		db.setRoundFCFY(round + 1);
		db.close();
		
		
		// Para cada empresa en el batch
		for (int i = round; i < round + batchFCFY; i++) {
			
			log.info("Nº de Empresa FCFY: " + round);
			round = i;
			JSONObject json = new JSONObject(array.get(i).toString());
			String Name = json.getString("name").replaceAll("'", "");	
			String symbol = json.getString("symbol");
			
			Ratios ratio = new Ratios();
			qreah q = new qreah();
			
			Profile profile = new CompanyProfile().getProfile(symbol);
			String companyName = profile.getCompanyName().replaceAll("'", "");
			String sector = profile.getSector().replaceAll("'", "");
			String industry = profile.getIndustry().replaceAll("'", "");
			String description = profile.getDescription();
			description = description.replaceAll("'", "");
			String price = profile.getPrice();
			String mktCap = profile.getMktCap();
			
			String exists = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts group by symbol, concept, finantialDate";
			exist = db.ExecuteSELECT(exists);
			
			HashMap<String, String> companyData = new HashMap<String, String>();
			companyData.put("symbol", symbol);
			companyData.put("companyName", companyName);
			companyData.put("sector", sector);
			companyData.put("industry", industry);
			companyData.put("description", description);
			companyData.put("price", price);
			companyData.put("mktCap", mktCap);
			
			ratio.setFCFYield(companyData);
			
		}
		
		
		
		return "ok";
	
	}
	
	public String finDateConversion(String finDate) {
		String result = "";
		int len = finDate.length();
		if (len > 5) {
			
			if (len == 7) {
				int year = Integer.parseInt(finDate.substring(0, 4));
				int mounth = Integer.parseInt(finDate.substring(len-2, len));
				
				if (mounth < 6) {
					result = Integer.toString(year - 1);
				} else {
					result = Integer.toString(year);
				}
				
			}
			
			if (len == 10) {
				
				boolean guion = finDate.substring(4, 5).contains("-");
				if (guion) {
					int mounth = Integer.parseInt(finDate.substring(5, 7));
					int year = Integer.parseInt(finDate.substring(0, 4));
					
					if (mounth < 6) {
						
						result = Integer.toString(year-1);
					} else {
						result = Integer.toString(year);
					}
					
				} else {
					int mounth = Integer.parseInt(finDate.substring(3, 5));
					int year = Integer.parseInt(finDate.substring(len-4, len));
					
					if (mounth < 6) {
						
						result = Integer.toString(year-1);
					} else {
						result = Integer.toString(year);
					}
				}
				
				
			}
			
		} else {
			result = finDate;
		}
		
		
		return result;
	}
	
	public static class FinancialModelingPrepUrl extends GenericUrl {
		 
	    public FinancialModelingPrepUrl(String encodedUrl) {
	        super(encodedUrl);
	    }
	}
	
	

	public static void main(String[] args) {
		Storage st = new Storage();
		String result = st.finDateConversion("2012-10-31");
		System.out.println(result);

	}

}
