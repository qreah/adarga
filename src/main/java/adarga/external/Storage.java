package adarga.external;

import java.io.IOException;
import java.sql.ResultSet;
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

import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.Company;
import adarga.getinfo.CompanyInformation;
import adarga.getinfo.DB;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.BalanceSheet.FinancialModelingPrepUrl;
import adarga.utis.qreah;

public class Storage {
	private static final Logger log = Logger.getLogger(KeyMetrics.class.getName());
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
	int rows = 7936;
	int batch = 10;
	
	public void store(String symbol, 
			String concept, 
			Double ratio, 
			String finYear
			) throws ClassNotFoundException, SQLException, ServletException, IOException {
		
		
		Profile profile = new CompanyProfile().getProfile(symbol);
		String companyName = profile.getCompanyName();
		String sector = profile.getSector();
		String industry = profile.getIndustry();
		String description = profile.getDescription();
		
		if (exists(symbol, concept, finYear)) {
		
			update(symbol, ratio, finYear, concept, companyName, sector, industry, description);
		} else {
			insert(symbol, ratio, finYear, concept, companyName, sector, industry, description);
		}
	}
	
	
	
	static public void update(String symbol, 
			Double ratio, 
			String finYear, 
			String concept,
			String companyName,
			String sector,
			String industry,
			String description
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
	
	static public void insert(String symbol, 
			Double ratio, 
			String finYear, 
			String concept,
			String companyName,
			String sector,
			String industry,
			String description
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
				+ description  
				+ "');";
		DB db = new DB();
		db.Execute(SQL);
		db.close();
	}
	
	static public boolean exists(String symbol, String concept, String finantialDate) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		DB db = new DB();
		String SQL = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts"
				+ " where concept = '" + concept + "' AND symbol = '"
				+ symbol + "'  AND finantialDate = '" + finantialDate + "'";
		
		ResultSet rs = db.ExecuteSELECT(SQL);
		
		if (rs.next()) {
			result = true;
		}
		db.close();
		return result;
	}
	
	public void getCompanies() throws ClassNotFoundException, ServletException, IOException, SQLException {
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
		
		if ((round + batch) > rows) {
			batch = rows - round;
			db.setRound(0);
		} else {
			db.setRound(round + batch);
		}
		db.close();
		int temp = round + batch +1;
		
		for (int i = round; i < round + batch +1; i++) {
			DB db2 = new DB();
			if (i < rows)  {
				db.setRound(i + 1);
			} else {
				db.setRound(0);
			}
			db2.close();
			JSONObject json = new JSONObject(array.get(i).toString());
			String Name = json.getString("name").replaceAll("'", "");	
			String symbol = json.getString("symbol");
			log.info(Name);
			log.info(symbol);
			FinantialRatios fr = new FinantialRatios();
			KeyMetrics km = new KeyMetrics();
			fr.storeReport(symbol);
			km.storeReport(symbol);
			
			
		}
	
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

	public static void main(String[] args) {
		Storage st = new Storage();
		String result = st.finDateConversion("2012-10-31");
		System.out.println(result);

	}

}
