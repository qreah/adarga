package adarga.analysis;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import adarga.external.CashFlowStatement;
import adarga.external.CashFlowStatement.CS;
import adarga.external.CompanyProfile;
import adarga.external.KeyMetrics;
import adarga.external.CompanyProfile.Profile;
import adarga.external.Storage;
import adarga.getinfo.DB;
import adarga.utis.qreah;

public class Ratios {
	
	private static final Logger log = Logger.getLogger(Ratios.class.getName());
	
	public String setFCFYield(HashMap<String, String> companyData) throws IOException, ClassNotFoundException, ServletException, SQLException {
		String symbol = companyData.get("symbol");
		CompanyProfile cp = new CompanyProfile();
		Profile profile = cp.getProfile(symbol);
		Double mrkCap = Double.valueOf(profile.getMktCap());
		String concept = "FreeCashFlow";	
		CashFlowStatement cs = new CashFlowStatement();
		cs.execute(symbol);
		String finDate = cs.getYears().keySet().iterator().next();
		String FCF = cs.getYears().get(finDate).getFreeCashFlow();
		Double freeCashFlow = Double.valueOf(FCF);
		Double FCFYield = freeCashFlow / mrkCap;
		log.info("FCFYield: " + FCFYield);
		Storage st = new Storage();
		String SQL = st.storeRowFCFY(companyData, "FCFY", FCFYield, finDate, "Valuation");	
		return SQL;
		
				
		/*	
		//DB db = new DB();
		//String finDate = db.getLastFinDate(symbol);
		log.info(finDate);
		if (!finDate.equals("ko")) {
			log.info(db.getRatio(symbol, concept, finDate));
			if (!db.getRatio(symbol, concept, finDate).equals("")) {
				Double freeCashFlow = Double.valueOf(db.getRatio(symbol, concept, finDate));
				Double FCFYield = freeCashFlow / mrkCap;
				Storage st = new Storage();
				st.storeFCFY(companyData, "FCFY", FCFYield, finDate, "Valuation");
			}
			
		}
		*/
		
		
		
	}

	public static void main(String[] args) {
		qreah q = new qreah();
		String finDate = q.today().substring(0, 4);
		System.out.println(finDate);
	}

}
