package adarga.getinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONObject;

import adarga.ratios.CashManagement;
import adarga.ratios.FinancialManagement;
import adarga.ratios.GlobalManagement;
import adarga.ratios.InvestmentManagement;
import adarga.ratios.OperatingManagement;

public class Company {
	
	private static final Logger log = Logger.getLogger(Company.class.getName());
	
	public List<Object> getFinancialStatements(String companyName) throws IOException, ClassNotFoundException, ServletException, SQLException {
		List<Object> result = new ArrayList<Object>();
		BalanceSheet bs = new BalanceSheet();
		IncomeStatement is = new IncomeStatement();
		CashFlowStatement cs = new CashFlowStatement();
		CompanyInformation ci = new CompanyInformation();
		//try {
			log.info("Company: " + companyName);
			switch(companyName) {
			  case "BRK.B":
				result = null;
			    break;
			  
			  default:
				  
				boolean bsb = bs.execute(companyName);
				boolean isb = is.execute(companyName);
				boolean csb = cs.execute(companyName);
				ci.execute(companyName);
				
				if (!bsb) {result.add("out"); return result;}
				if (!isb) {result.add("out"); return result;} 
				if (!csb) {result.add("out"); return result;} 
					
				result.add(bs);
				result.add(is);
				result.add(cs);
				result.add(ci);
			}
			
		
			return result;
			
			
		//} catch (Exception e) {
		//	log.info("Error: Company. com.google.apphosting.api.DeadlineExceededException | Company: " + companyName);
		//}
	}
	
	public JSONObject analysis(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs, CompanyInformation ci) throws ClassNotFoundException, ServletException, IOException, SQLException {
		JSONObject json = new JSONObject();
		OperatingManagement OM = new OperatingManagement();
		OM.loadOperatingManagement(bs, is, cs);
		InvestmentManagement IM = new InvestmentManagement();
		IM.loadInvestmentManagement(bs, is, cs);
		
		FinancialManagement FM = new FinancialManagement();
		FM.loadFinancialManagement(bs, is, cs);
		CashManagement CM = new CashManagement();
		CM.loadCashManagement(bs, is, cs, ci);
		
		GlobalManagement GM = new GlobalManagement();
		GM.loadGlobalManagement(bs, is, cs, ci);
		json = chain(OM, IM, FM, CM, GM);
		return json;
		
	}
	
	public JSONObject chain(OperatingManagement OM, 
			InvestmentManagement IM, FinancialManagement FM, CashManagement CM, GlobalManagement GM) {
		
		JSONObject json = new JSONObject();
		json.put("OM", OM.toJSON());
		json.put("IM", IM.toJSON());
		json.put("FM", FM.toJSON());
		json.put("CM", CM.toJSON());
		json.put("GM", GM.toJSON());
		return json;
		
	}
	
	
	
	public double growthFirst10Years(double g) {
		double G = 0.0;
		if (g < 0.02) {
			G = 0.02;
		}
		if ((g >= 0.02) && (g < 0.0215)) {
			G = 0.05;
		}
		if ((g >= 0.0215) && (g < 0.0239)) {
			G = 0.10;
		}
		if ((g >= 0.0239) && (g < 0.0261)) {
			G = 0.15;
		}
		if ((g >= 0.0261) && (g < 0.0283)) {
			G = 0.20;
		}
		if ((g >= 0.0283) && (g < 0.0324)) {
			G = 0.30;
		}
		if ((g >= 0.0324) && (g < 0.0363)) {
			G = 0.40;
		}
		if ((g >= 0.0363) && (g < 0.0399)) {
			G = 0.50;
		}
		if ((g >= 0.0399) && (g < 0.0549)) {
			G = 1.00;
		}
		if ((g >= 0.0549) && (g < 0.0921)) {
			G = 3.00;
		}
		return G;
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, ServletException, SQLException {
        BalanceSheet bs = new BalanceSheet();
		IncomeStatement is = new IncomeStatement();
		CashFlowStatement cs = new CashFlowStatement();
		CompanyInformation ci = new CompanyInformation();
		Company com = new Company();
		com.getFinancialStatements("AMZN");
		com.analysis(bs, is, cs, ci);
		
    }
	
}
