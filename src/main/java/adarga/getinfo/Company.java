package adarga.getinfo;

import java.io.IOException;
import java.util.logging.Logger;

import org.json.JSONObject;

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
	  //try {
		int lastYear = is.get("Revenue").lastYear();
		
		OperatingManagement OM = new OperatingManagement(bs, is, cs);
		InvestmentManagement IM = new InvestmentManagement(bs, is, cs);
		
		
		Item revenue = is.get("Revenue");
		Item salesGrowth = revenue.changeInItem();
		Item provisionForIncomeTaxes = is.get("Provision for income taxes");
		Item taxRate = provisionForIncomeTaxes.divide(is.get("Net income")); 
		Item NOPAT = is.get("Net income").sum(is.get("Interest Expense").multiply(taxRate.substractNumberAnte(1.0)));
		Item NOPATGrowth = NOPAT.changeInItem();
		Item NOPATMargin = NOPAT.divide(revenue);
		
		// Net Working Capital
		
		Item netCurrentAssets = bs.get("Total current assets").substract(bs.get("Total cash"));
		
		Item netCurrentLiabilities;
		Item shortTermDebt;
		if (bs.get("Short-term debt") == null) {
			shortTermDebt = bs.get("Total current liabilities").multiplyNumber(0.0);
		} else {
			shortTermDebt = bs.get("Short-term debt");
		}
		netCurrentLiabilities = bs.get("Total current liabilities").substract(shortTermDebt);
		Item netWorkingCapital = netCurrentAssets.substract(netCurrentLiabilities);
		
		Item netWorkingCapitalOverRevenues = netWorkingCapital.divide(revenue);
		
		// Net Long-term Assets
		
		Item nonInterestBearingLTLiabilities = bs.get("Total non-current liabilities").substract(controlNull(bs.get("Long-term debt"), lastYear));
		nonInterestBearingLTLiabilities = nonInterestBearingLTLiabilities.substract(controlNull(bs.get("Capital leases"), lastYear));
		Item netLongTermAssets = bs.get("Total non-current assets").substract(nonInterestBearingLTLiabilities);
		Item netLongTermAssetsOverRevenue = netLongTermAssets.divide(revenue);
		
		// Net debt to capital ratio = Net Debt / Net Assets
		
		// Net Debt = Debt LT - Cash + Short term debt
		Item LTDebt = controlNull(bs.get("Long-term debt"), lastYear).sum(controlNull(bs.get("Capital leases"), lastYear));
		
		Item netDebt = LTDebt.substract(bs.get("Total cash"));
		netDebt = netDebt.sum(shortTermDebt);
		
		// Net Assets = Networking Capital + Net Long term Assets
		Item netAssets = netWorkingCapital.sum(netLongTermAssets);
	
		Item netDebtToCapitalRatio = netDebt.divide(netAssets);
		
		
		// afterTaxCostOfDebt = Net interest earnings after taxes  / Net  Debt
		
		// Net interest earnings after taxes = Interest Expense * ( 1 - TaxRate)
		Item temp = taxRate.substractNumberAnte(1.0);
		Item netInterestEarningsAfterTaxes = is.get("Interest Expense").multiply(temp);
		
		Item afterTaxCostOfDebt = netInterestEarningsAfterTaxes.divide(netDebt);
		
		// Valuation
		
		Double WACC = 0.1;
		double EPSIncome = (is.get("Net income").getValue(lastYear) * 1000000) / ci.numberOfShares();
		double EPSOperatingIncome = (is.get("Operating income").getValue(lastYear) * 1000000) / ci.numberOfShares();
		double price = ci.getStockPrice();
		double gIncome = (price * WACC - EPSIncome) / (price - EPSIncome);
		double gOperatingIncome = (price * WACC - EPSOperatingIncome) / (price - EPSOperatingIncome);
		double g10Years = growthFirst10Years(gOperatingIncome);
		
		
		
		
		
		//Investment Management
		
		
		//Financial Management
		
		//Financial Distress Analysis
		
	
		// Global Analysis
		
		Item salesOverAssets = is.get("Revenue").divide(bs.get("Total assets"));
		Item OperatingROA = NOPATMargin.multiply(salesOverAssets);
		Item salesOverNetAssets = revenue.divide(netAssets);
		Item ROE = is.get("Net income").divide(bs.get("Total stockholders' equity"));
		temp = bs.get("Total stockholders' equity").substract(controlNull(bs.get("Goodwill"), lastYear));
		Item returnOnTangibleEquity = is.get("Net income").divide(temp);
		
		//Cash Management
		
		
		// Display results
		
		json.put("salesGrowth", salesGrowth.toString());
		json.put("NOPATGrowth", NOPATGrowth.toString());
		json.put("NOPATMargin", NOPATMargin.toString());
		json.put("netWorkingCapitalOverRevenues", netWorkingCapitalOverRevenues.toString());
		json.put("netLongTermAssetsOverRevenue", netLongTermAssetsOverRevenue.toString());
		json.put("netDebtToCapitalRatio", netDebtToCapitalRatio.toString());
		json.put("afterTaxCostOfDebt", afterTaxCostOfDebt.toString());
		json.put("OperatingROA", OperatingROA.toString());
		json.put("salesOverNetAssets", salesOverNetAssets.toString());
		json.put("ROE", ROE.toString());
		json.put("returnOnTangibleEquity", returnOnTangibleEquity.toString());
		json.put("growthIncome", Double.toString(gIncome));
		json.put("growthOperatingIncome", Double.toString(gOperatingIncome));
		json.put("g10Years", Double.toString(g10Years));
	 
		/*
	  } catch (Exception e) {
		 
          json = emptyResults();
      }  
      */
		
		return json;
		
	}
	
	public JSONObject emptyResults() {
		JSONObject json = new JSONObject();
		json.put("salesGrowth", "");
		json.put("NOPATGrowth", "");
		json.put("NOPATMargin", "");
		json.put("netWorkingCapitalOverRevenues", "");
		json.put("netLongTermAssetsOverRevenue", "");
		json.put("netDebtToCapitalRatio", "");
		json.put("afterTaxCostOfDebt", "");
		json.put("OperatingROA", "");
		json.put("salesOverNetAssets", "");
		json.put("ROE", "");
		json.put("returnOnTangibleEquity", "");
		json.put("growthIncome", "");
		json.put("growthOperatingIncome", "");
		json.put("g10Years", "");
		return json;
	}
	
	
	public Item controlNull(Item inputItem, int lastYear) {
		Item item = new Item();
		item.setZero(5, lastYear);
		if (inputItem != null) {
			item = inputItem;
		} else {
			item = item.multiplyNumber(0.0);
		}
		return item;
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
