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
import adarga.getinfo.DBOne;
import adarga.utis.qreah;

public class Storage {
	private static final Logger log = Logger.getLogger(KeyMetrics.class.getName());
	
	static ResultSet existFCF;
	static DB db;
	
	public Storage() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		
	}
	
	
	
	public void close() throws SQLException {
		db.close();
	}
	
	public void reviveDB() throws ClassNotFoundException, ServletException, IOException, SQLException {
		db = new DB();
	}
	
	public boolean statementClosed() throws SQLException {
		return db.StatementClosed();
	}
	
	public void addBatch(String SQL) throws ClassNotFoundException, SQLException, ServletException, IOException {		
		db.addBatch(SQL);
	}
	
	public int[] executeBatch() throws SQLException, ClassNotFoundException, ServletException, IOException {
		
		int[] result = db.executeBatch();
		return result;
	}
	
	public void addBatch(String SQL, DBOne one) throws ClassNotFoundException, SQLException, ServletException, IOException {		
		one.addBatch(SQL);
	}
	
	public int[] executeBatch(DBOne one) throws SQLException, ClassNotFoundException, ServletException, IOException {
		
		int[] result = one.executeBatch();
		return result;
	}
	
	public String SQLAddRow(HashMap<String, String> companyData, 
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
		boolean existInDB = exists(symbol, concept, finYear);
		
		if (existInDB) {
			SQL = updateSQL(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		} else {
			SQL = insertSQL(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		}	
		
		return SQL;
	}
	
	
	public String SQLAddRow(HashMap<String, String> companyData, 
			String concept, 
			Double ratio, 
			String finYear,
			String type,
			DBOne one
			
			) throws ClassNotFoundException, SQLException, ServletException, IOException {
		
		
		String symbol = companyData.get("symbol");
		String companyName = companyData.get("companyName");
		String sector = companyData.get("sector");
		String industry = companyData.get("industry");
		String description = companyData.get("description");
		String price = companyData.get("price");
		String mktCap = companyData.get("mktCap");
		
		String SQL = "";
		boolean existInDB = exists(symbol, concept, finYear, one);
		
		if (existInDB) {
			SQL = updateSQL(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		} else {
			SQL = insertSQL(symbol, ratio, finYear, concept, companyName, sector, industry, description, type);
		}	
		
		return SQL;
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
	
	
	/*
	 * 	Checks if the company, concept and date exists in the database
	 */
	
	static public boolean exists(String symbol, String concept, String finantialDate) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		
		String exists = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts "
				+ "where symbol = '" + symbol + "' and concept = '" + concept + "' and finantialDate = '" + finantialDate + "'";
		ResultSet exist = db.ExecuteSELECT(exists);
		
		while (exist.next()) {
			String symbolStr = exist.getString("symbol");
			if (symbolStr.equals(symbol)) {
				String conceptStr = exist.getString("concept");
				if (conceptStr.equals(concept)) {
					String finantialDateStr = exist.getString("finantialDate");
					if (finantialDateStr.equals(finantialDate)) {
						result = true;
					}
				}
			}
		}
		exist.close();
		return result;
	}
	
	static public boolean exists(String symbol, String concept, String finantialDate, DBOne one) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		
		String exists = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts "
				+ "where symbol = '" + symbol + "' and concept = '" + concept + "' and finantialDate = '" + finantialDate + "'";
		ResultSet exist = one.ExecuteSELECT(exists);
		
		while (exist.next()) {
			String symbolStr = exist.getString("symbol");
			if (symbolStr.equals(symbol)) {
				String conceptStr = exist.getString("concept");
				if (conceptStr.equals(concept)) {
					String finantialDateStr = exist.getString("finantialDate");
					if (finantialDateStr.equals(finantialDate)) {
						result = true;
					}
				}
			}
		}
		return result;
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
	
	

	public static void main(String[] args) throws ClassNotFoundException, ServletException, IOException, SQLException {
		Storage st = new Storage();
		String result = st.finDateConversion("2012-10-31");
		System.out.println(result);

	}

}
