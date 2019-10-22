package adarga.analysis;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONObject;

import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.DB;

public class QualityTest {
	private static final Logger log = Logger.getLogger(QualityTest.class.getName());
	
	public void uploadRareCases(String companySymbol, List<String> concepts, String report) throws ClassNotFoundException, ServletException, IOException, SQLException {
		DB db = new DB();
		
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");  
		Date date = new Date(System.currentTimeMillis());  
		String dateS = formatter.format(date).substring(0, 10);
		String SQL;
		int len = concepts.size();
		for (int i=0; i<len; i++) {
			if (!checkIfIsAlreadyInTheDB(dateS, companySymbol, concepts.get(i), report) && (!filter(companySymbol, concepts.get(i)))) {
				SQL = "INSERT INTO apiadbossDB.qualityTest ("
						+ "actualDate, "
						+ "companySymbol, "
						+ "concept, "
						+ "report "
						+ ") VALUES ('"
						+ dateS + "', '"
						+ companySymbol + "', '"
						+ concepts.get(i).replace("'", "") + "', '"
						+ report + "'"
						+ ");";
				db.Execute(SQL);
				
			}
			
		}
		db.close();
		
	}
	
	public boolean filter(String companySymbol, String concept) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		String caseString = companySymbol + ' ' + concept;
		
		switch(caseString) {
		
			//Cases that are OK and we will filter
		  case "ACN Inventories":
		    result = true;
		    break;
		  case "ADBE Inventories":
			    result = true;
			    break;
		  case "ADBE Accounts payable":
			    result = true;
			    break;
		  case "AMG Inventories":
			    result = true;
			    break;
		  case "AMG Short-term debt":
			    result = true;
			    break;
		  case "CCL Acquisitions, net":
			    result = true;
			    break;
		  case "CHTR Acquisitions, net":
			    result = true;
			    break;
		  case "CMG Acquisitions, net":
			    result = true;
			    break;
		  case "COG Acquisitions, net":
			    result = true;
			    break;
		  case "COP Acquisitions, net":
			    result = true;
			    break;
		  case "COST Acquisitions, net":
			    result = true;
			    break;
		  case "CVX Acquisitions, net":
			    result = true;
			    break;
		  case "CXO Acquisitions, net":
			    result = true;
			    break;
		  case "DG Acquisitions, net":
			    result = true;
			    break;
		  case "DISH Acquisitions, net":
			    result = true;
			    break;
		  case "DVN Acquisitions, net":
			    result = true;
			    break;
		  case "EOG Acquisitions, net":
			    result = true;
			    break;
		  case "EXPD Acquisitions, net":
			    result = true;
			    break;
		  case "KMX Acquisitions, net":
			    result = true;
			    break;
		  case "XEC Acquisitions, net":
			    result = true;
			    break;
		  case "XLN Acquisitions, net":
			    result = true;
			    break;
		  case "EL Capital leases":
			    result = true;
			    break;
		  case "EL Common stock issued":
			    result = true;
			    break;
		  case "EQIX Goodwill":
			    result = true;
			    break;
		  case "EQIX Inventories":
			    result = true;
			    break;
		  case "EQIX Property, plant, and equipment reductions":
			    result = true;
			    break;
		  case "EQR Capital leases":
			    result = true;
			    break;
		  case "EQR Goodwill":
			    result = true;
			    break;
		  case "EQR Intangible assets":
			    result = true;
			    break;
		  case "EQR Inventories":
			    result = true;
			    break;
		  case "Luk Receivables":
			    result = true;
			    break;
		  case "Luk Long-term debt":
			    result = true;
			    break;  
		  case "XL Short-term debt":
			    result = true;
			    break;
		  case "XL Total current liabilities":
			    result = true;
			    break;
		  case "XL Payables and accrued expenses":
			    result = true;
			    break;
		  case "XL Intangible assets":
			    result = true;
			    break;
		  case "ALGN Long-term debt":
			    result = true;
			    break;
		  case "A Capital leases":
			    result = true;
			    break;
		  
			    
			    
			    
		  default:
		    
		}
		
		// Companies that are so special that you cannot analize it like the rest
		switch(companySymbol) {
		  case "AET":
		    result = true;
		    break;
		  case "AFL":
			    result = true;
			    break;
		  case "AIG":
			    result = true;
			    break;
		  case "AIZ":
			    result = true;
			    break;
		  case "ALL":
			    result = true;
			    break;
		  case "ANTM":
			    result = true;
			    break;
		  case "BAC":
			    result = true;
			    break;
		  case "BBT":
			    result = true;
			    break;
		  case "BHF":
			    result = true;
			    break;
		  case "C":
			    result = true;
			    break;
		  case "CB":
			    result = true;
			    break;
		  case "CFG":
			    result = true;
			    break;
		  case "CMA":
			    result = true;
			    break;
		  case "CVS":
			    result = true;
			    break;
		  case "EQR":
			    result = true;
			    break;
		  case "ESRX":
			    result = true;
			    break;
		  case "FITB":
			    result = true;
			    break;
		  case "GS":
			    result = true;
			    break;
		  case "HBAN":
			    result = true;
			    break;
		  case "HIG":
			    result = true;
			    break;
		  case "HUM":
			    result = true;
			    break;
		  case "L":
			    result = true;
			    break;
		  case "LNC":
			    result = true;
			    break;
		  case "JPM":
			    result = true;
			    break;
		  case "KEY":
			    result = true;
			    break;
		  case "NTRS":
			    result = true;
			    break;
		  case "MCO":
			    result = true;
			    break;
		  case "MET":
			    result = true;
			    break;
		  case "MTB":
			    result = true;
			    break;
		  case "MS":
			    result = true;
			    break;  
		  case "PBCT":
			    result = true;
			    break;
		  case "PFG":
			    result = true;
			    break;
		  case "PGR":
			    result = true;
			    break;
		  case "PRU":
			    result = true;
			    break;
		  case "RF":
			    result = true;
			    break;
		  case "RJF":
			    result = true;
			    break;
		  case "SCHW":
			    result = true;
			    break;
		  case "SIVB":
			    result = true;
			    break;
		  case "STI":
			    result = true;
			    break;
		  case "SPGI":
			    result = true;
			    break;
		  case "TMK":
			    result = true;
			    break;
		  case "TRV":
			    result = true;
			    break;
		  case "UNH":
			    result = true;
			    break;
		  case "UNM":
			    result = true;
			    break;
		  case "XL":
			    result = true;
			    break;
		  case "WFC":
			    result = true;
			    break;	  
		  default:
		    
		}
		
		
		// Concepts that are so common that is easy that a company doesn't use it
		switch(concept) {
		  case "Capital leases":
		    result = true;
		    break;
		  case "Cash dividends paid":
			    result = true;
			    break;
		  case "Cost of revenue":
			    result = true;
			    break;
			    
		  default:
		}
		
		
		
		return result;
	}
	
	
	public boolean checkIfIsAlreadyInTheDB(String actualDate, String companySymbol, String concept, String report) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		DB db = new DB();
		concept = concept.replace("'", "");
		String SQL = "SELECT actualDate, companySymbol, concept, report "
				+ "FROM apiadbossDB.qualityTest"
				+ " WHERE "
				+ "actualDate = '" + actualDate + "' AND "
				+ "companySymbol = '" + companySymbol + "' AND "
				+ "concept = '" + concept + "' AND "
				+ "report = '" + report + "';"
				;
		
		ResultSet resultSet = db.ExecuteSELECT(SQL);
		if (resultSet != null) {
			result = false;
		} else {
			result = true;
		}
		db.close();
		return result;
	}
	
	public void getConcepts(String key, String company, String report) throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		DB db = new DB();
		//String SQLDelete = "DELETE FROM apiadbossDB.conceptsTable;";
		//db.Execute(SQLDelete);
		String SQLInsert = "INSERT INTO apiadbossDB.conceptsTable (concept, company, report) VALUES ('" + key + "', '" + company + "', '" + report + "');";
		log.info(SQLInsert);
		db.Execute(SQLInsert);
		db.close();
		
	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, ServletException, IOException, SQLException {
		

	}

}
