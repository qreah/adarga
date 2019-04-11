package adarga.ratios;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;

public class OperatingManagement {
	
	Item revenue;
	Item salesGrowth;
	Item COGS;
	Item operatingIncome;
	Item grossMargin;
	Item SGA;
	Item SGAOverSales;
	Item NOPAT;
	Item NOPATGrowth;
	Item NOPATMargin;
	Item operatingMargin;
	Item provisionForIncomeTaxes;
	Item taxRate; 
	Item interestExpense;
	Item netIncome;
	Item incomeOverRevenue;
	Item operatingCashFlow;
	Item operatingCashFlowOverIncome;
	
	public OperatingManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs) {
		revenue = is.get("Revenue");
		salesGrowth = revenue.changeInItem();
		COGS = is.get("Cost of revenue");
		operatingIncome = is.get("Operating income");
		operatingMargin = operatingIncome.divide(revenue);
		grossMargin = is.get("Gross profit").divide(revenue);
		SGA = is.get("Sales, General and administrative");
		SGAOverSales = SGA.divide(revenue);
		interestExpense = is.get("Interest Expense");
		netIncome = is.get("Net income");
		incomeOverRevenue = netIncome.divide(revenue);
		operatingCashFlow = cs.get("Net cash provided by operating activities");
		operatingCashFlowOverIncome = operatingCashFlow.divide(revenue);
		provisionForIncomeTaxes = is.get("Provision for income taxes");
		taxRate = provisionForIncomeTaxes.divide(is.get("Net income")); 
		NOPAT = is.get("Net income").sum(is.get("Interest Expense").multiply(taxRate.substractNumberAnte(1.0)));
		NOPATGrowth = NOPAT.changeInItem();
		NOPATMargin = NOPAT.divide(revenue);
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("revenue", revenue.toString());
		json.put("salesGrowth", salesGrowth.toString());
		json.put("COGS", COGS.toString());
		json.put("operatingIncome", operatingIncome.toString());
		json.put("grossMargin", grossMargin.toString());
		json.put("SGA", SGA.toString());
		json.put("SGAOverSales", SGAOverSales.toString());
		json.put("NOPAT", NOPAT.toString());
		json.put("NOPATGrowth", NOPATGrowth.toString());
		json.put("NOPATMargin", NOPATMargin.toString());
		json.put("operatingMargin", operatingMargin.toString());
		json.put("provisionForIncomeTaxes", provisionForIncomeTaxes.toString());
		json.put("taxRate", taxRate.toString());
		json.put("interestExpense", interestExpense.toString());
		json.put("netIncome", netIncome.toString());
		json.put("incomeOverRevenue", incomeOverRevenue.toString());
		json.put("operatingCashFlow", operatingCashFlow.toString());
		json.put("operatingCashFlowOverIncome", operatingCashFlowOverIncome.toString());

		return json;
		
	}
	
	public String toString() {
		return toJSON().toString();
	}

	
	
	public Item getRevenue() {
		return revenue;
	}

	public void setRevenue(Item revenue) {
		this.revenue = revenue;
	}

	public Item getSalesGrowth() {
		return salesGrowth;
	}

	public void setSalesGrowth(Item salesGrowth) {
		this.salesGrowth = salesGrowth;
	}

	public Item getCOGS() {
		return COGS;
	}

	public void setCOGS(Item cOGS) {
		COGS = cOGS;
	}

	public Item getOperatingIncome() {
		return operatingIncome;
	}

	public void setOperatingIncome(Item operatingIncome) {
		this.operatingIncome = operatingIncome;
	}

	public Item getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(Item grossMargin) {
		this.grossMargin = grossMargin;
	}

	public Item getSGA() {
		return SGA;
	}

	public void setSGA(Item sGA) {
		SGA = sGA;
	}

	public Item getSGAOverSales() {
		return SGAOverSales;
	}

	public void setSGAOverSales(Item sGAOverSales) {
		SGAOverSales = sGAOverSales;
	}

	public Item getNOPAT() {
		return NOPAT;
	}

	public void setNOPAT(Item nOPAT) {
		NOPAT = nOPAT;
	}

	public Item getNOPATGrowth() {
		return NOPATGrowth;
	}

	public void setNOPATGrowth(Item nOPATGrowth) {
		NOPATGrowth = nOPATGrowth;
	}

	public Item getNOPATMargin() {
		return NOPATMargin;
	}

	public void setNOPATMargin(Item nOPATMargin) {
		NOPATMargin = nOPATMargin;
	}

	public Item getOperatingMargin() {
		return operatingMargin;
	}

	public void setOperatingMargin(Item operatingMargin) {
		this.operatingMargin = operatingMargin;
	}

	public Item getProvisionForIncomeTaxes() {
		return provisionForIncomeTaxes;
	}

	public void setProvisionForIncomeTaxes(Item provisionForIncomeTaxes) {
		this.provisionForIncomeTaxes = provisionForIncomeTaxes;
	}

	public Item getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Item taxRate) {
		this.taxRate = taxRate;
	}

	public Item getInterestExpense() {
		return interestExpense;
	}

	public void setInterestExpense(Item interestExpense) {
		this.interestExpense = interestExpense;
	}

	public Item getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(Item netIncome) {
		this.netIncome = netIncome;
	}

	public Item getIncomeOverRevenue() {
		return incomeOverRevenue;
	}

	public void setIncomeOverRevenue(Item incomeOverRevenue) {
		this.incomeOverRevenue = incomeOverRevenue;
	}

	public Item getOperatingCashFlow() {
		return operatingCashFlow;
	}

	public void setOperatingCashFlow(Item operatingCashFlow) {
		this.operatingCashFlow = operatingCashFlow;
	}

	public Item getOperatingCashFlowOverIncome() {
		return operatingCashFlowOverIncome;
	}

	public void setOperatingCashFlowOverIncome(Item operatingCashFlowOverIncome) {
		this.operatingCashFlowOverIncome = operatingCashFlowOverIncome;
	}

	
}
