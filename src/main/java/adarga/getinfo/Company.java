package adarga.getinfo;

import java.io.IOException;
import java.util.logging.Logger;

import org.json.JSONObject;

import adarga.ratios.CashManagement;
import adarga.ratios.FinancialManagement;
import adarga.ratios.GlobalManagement;
import adarga.ratios.InvestmentManagement;
import adarga.ratios.OperatingManagement;

public class Company {
	
	private static final Logger log = Logger.getLogger(Company.class.getName());
	
	public void getFinancialStatements(String companyName) throws IOException {
		BalanceSheet bs = new BalanceSheet();
		IncomeStatement is = new IncomeStatement();
		CashFlowStatement cs = new CashFlowStatement();
		CompanyInformation ci = new CompanyInformation();
		//try {
			bs.execute(companyName);
			is.execute(companyName);
			cs.execute(companyName);
			ci.execute(companyName);
			
		//} catch (Exception e) {
		//	log.info("Error: Company. com.google.apphosting.api.DeadlineExceededException | Company: " + companyName);
		//}
	}
	
	public JSONObject analysis(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs, CompanyInformation ci) {
		JSONObject json = new JSONObject();
		OperatingManagement OM = new OperatingManagement(bs, is, cs);
		InvestmentManagement IM = new InvestmentManagement(bs, is, cs);
		FinancialManagement FM = new FinancialManagement(bs, is, cs);
		CashManagement CM = new CashManagement(bs, is, cs);
		GlobalManagement GM = new GlobalManagement(bs, is, cs, ci);
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
	
	
	public static void main(String[] args) throws IOException {
        BalanceSheet bs = new BalanceSheet();
		IncomeStatement is = new IncomeStatement();
		CashFlowStatement cs = new CashFlowStatement();
		CompanyInformation ci = new CompanyInformation();
		Company com = new Company();
		com.getFinancialStatements("AMZN");
		com.analysis(bs, is, cs, ci);
		
    }
	
}
