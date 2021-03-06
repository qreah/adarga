package adarga.analysis;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import adarga.external.CashFlowStatement;
import adarga.external.CashFlowStatement.CS;
import adarga.external.CompanyProfile;
import adarga.external.CompanyProfile.Profile;
import adarga.external.Storage;
import adarga.getinfo.DB;
import adarga.getinfo.DBOne;
import adarga.utils.TableSet;
import adarga.utils.qreah;

public class Ratios {
	
	private static final Logger log = Logger.getLogger(Ratios.class.getName());
	
	public String setFCFYield(HashMap<String, String> companyData, DBOne one, List<TableSet> companyRegisters) throws IOException, ClassNotFoundException, ServletException, SQLException {
		String symbol = companyData.get("symbol");
		CompanyProfile cp = new CompanyProfile();
		Profile profile = cp.getProfile(symbol);
		Double mrkCap = Double.valueOf(profile.getMktCap());
		String concept = "FreeCashFlow";	
		CashFlowStatement cs = new CashFlowStatement();
		cs.execute(symbol);
		String finDate = cs.getYears().keySet().iterator().next();
		Iterator<String> iter = cs.getYears().keySet().iterator();
		String finDateTemp = "";
		int finDateInt = Integer.parseInt(finDate);
		while (iter.hasNext()) {
			finDateTemp = iter.next();
			int temp = Integer.parseInt(finDateTemp);
			if (temp > finDateInt) {
				finDateInt = temp;
			}
		}
		finDate = String.valueOf(finDateInt);
		String FCF = cs.getYears().get(finDate).getFreeCashFlow();
		String SQL = "";
		if (FCF.isEmpty()) {
			log.info("FCFY empty");
		} else {
			Double freeCashFlow = Double.valueOf(FCF);
			Double FCFYield = freeCashFlow / mrkCap;
			String FCFYieldStr = Double. toString(FCFYield).replace(".", ",");
			log.info("FCFYield: " + FCFYield);
			Storage st = new Storage();
			SQL = st.SQLAddRow(companyData, "FCFY", FCFYieldStr, finDate, "Valuation", one, companyRegisters);	
		}
		
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
