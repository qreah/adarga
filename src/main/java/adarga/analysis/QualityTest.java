package adarga.analysis;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.DB;

public class QualityTest {
	private static final Logger log = Logger.getLogger(QualityTest.class.getName());
	
	public void uploadRareCases(String companySymbol, List<String> concepts, String report) throws ClassNotFoundException, ServletException, IOException, SQLException {
		DB db = new DB();
		
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");  
		Date date = new Date(System.currentTimeMillis());  
		String dateS = formatter.format(date);
		String SQL;
		int len = concepts.size();
		for (int i=0; i<len; i++) {
			if (checkIfIsAlreadyInTheDB(dateS, companySymbol, concepts.get(i), report)) {
				SQL = "INSERT INTO apiadbossDB.qualityTest ("
						+ "actualDate, "
						+ "companySymbol, "
						+ "concept, "
						+ "report, "
						+ ") VALUES ("
						+ dateS + ", "
						+ companySymbol + ", "
						+ concepts.get(i) + ", "
						+ report + ", "
						+ ");";
				log.info(SQL);
				db.Execute(SQL);
			}
			
		}
		
		
	}
	
	
	public boolean checkIfIsAlreadyInTheDB(String actualDate, String companySymbol, String concept, String report) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		DB db = new DB();
		String SQL = "SELECT actualDate, companySymbol, concept, report "
				+ "FROM apiadbossDB.qualityTest"
				+ " WHERE "
				+ "actualDate = '" + actualDate + "' AND "
				+ "companySymbol = '" + companySymbol + "' AND "
				+ "concept = '" + concept + "' AND "
				+ "report = '" + report + "';"
				;
		log.info(SQL);
		ResultSet resultSet = db.ExecuteSELECT(SQL);
		if (resultSet != null) {
			result = false;
		} else {
			result = true;
		}
		db.close();
		log.info("resultSet: " + resultSet.getString(companySymbol));
		log.info("result: " + result);
		return result;
	}
	
	
	public static void main(String[] args) {
		

	}

}
