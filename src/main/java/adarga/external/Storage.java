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
	int batch = 80;
	
	public void store(String symbol, String concept, Double ratio, String finYear, DB db) throws ClassNotFoundException, SQLException, ServletException, IOException {
		
		if (exists(symbol, concept, finYear)) {
		
			update(symbol, ratio, finYear, db, concept);
		} else {
			insert(symbol, ratio, finYear, db, concept);
		}
	}
	
	static public void update(String symbol, Double ratio, String finYear, DB db, String concept) throws ClassNotFoundException, ServletException, IOException, SQLException {
		qreah q = new qreah();
		String SQL = "UPDATE apiadbossDB.adargaConcepts" + 
				" SET symbol='" + symbol + "', value= '" 
				+ ratio + "', concept= '" 
						+ concept + "', finantialDate='" + finYear + "', reportDate='" + q.today() 
				+ "'"
				+ " WHERE "
				+ "symbol='" + symbol + "' AND "
				+ "concept='" + concept + "' AND "
				+ "finantialDate='" + finYear 
				+ "';";
		
		db.Execute(SQL);
		db.close();
	}
	
	static public void insert(String symbol, Double ratio, String finYear, DB db, String concept) throws ClassNotFoundException, ServletException, IOException, SQLException {
		qreah q = new qreah();
		String SQL = "INSERT INTO apiadbossDB.adargaConcepts" + 
				" VALUES ('" + symbol + "', '" + concept + "', '" 
				+ ratio + "', '" + finYear + "', '" + q.today() 
				+ "');";
		
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

	public static void main(String[] args) {
		

	}

}
