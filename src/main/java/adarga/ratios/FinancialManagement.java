package adarga.ratios;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;

public class FinancialManagement {
	
	Item netInterestEarningsAfterTaxes;
	Item longTermDebt;
	Item netDebt;
	Item equity;
	Item debtToCapitalRatio;
	Item netDebtToNetCapitalRatio;
	Item spread;
	Item netFinancialLeverage;
	Item financialLeverage;
	Item currentRatio;
	Item quickRatio;
	Item cashRatio;
	Item interestCoverage_operatingIncomeVSinterestexpense;
	Item interestCoverage_operatingCashFlowVSinterestexpense;
	
	
	public FinancialManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs) {
		Item provisionForIncomeTaxes = is.get("Provision for income taxes");
		Item taxRate = provisionForIncomeTaxes.divide(is.get("Net income")); 
		Item temp = taxRate.substractNumberAnte(1.0);
		netInterestEarningsAfterTaxes = is.get("Interest Expense").multiply(temp);
		longTermDebt = bs.get("Long-term debt").sum(bs.get("Capital leases"));
		netDebt = longTermDebt.sum(bs.get("Short-term debt"));
		netDebt = netDebt.substract(bs.get("Total cash"));
		equity = bs.get("Total stockholders' equity");
		debtToCapitalRatio = netDebt;
		temp = netDebt.sum(equity);
		debtToCapitalRatio = debtToCapitalRatio.divide(temp);
		temp = netDebt.sum(equity);
		netDebtToNetCapitalRatio = netDebt.divide(temp);
		Item revenue = is.get("Revenue");
		Item salesOverAssets = is.get("Revenue").divide(bs.get("Total assets"));
		Item NOPAT = is.get("Net income").sum(is.get("Interest Expense").multiply(taxRate.substractNumberAnte(1.0)));
		Item NOPATMargin = NOPAT.divide(revenue);
		Item OperatingROA = NOPATMargin.multiply(salesOverAssets);
		Item afterTaxCostOfDebt = netInterestEarningsAfterTaxes.divide(netDebt);
		spread = OperatingROA.substract(afterTaxCostOfDebt);
		netFinancialLeverage = netDebt.divide(equity);
		financialLeverage = spread.multiply(netFinancialLeverage);
		Item currentAssets = bs.get("Total current assets");
		Item currentLiabilities = bs.get("Total current liabilities");
		currentRatio = currentAssets.divide(currentLiabilities);
		Item accountsReceivable = bs.get("Receivables");
		Item cashAndMarketableSecurities = bs.get("Total cash");
		quickRatio = accountsReceivable.sum(cashAndMarketableSecurities);
		quickRatio = quickRatio.divide(currentLiabilities);
		cashRatio = cashAndMarketableSecurities.divide(currentLiabilities);
		Item operatingIncome = is.get("Operating income");
		Item operatingCashFlow = cs.get("Net cash provided by operating activities");
		Item interestExpense = is.get("Interest Expense");
		interestCoverage_operatingIncomeVSinterestexpense = operatingIncome.divide(interestExpense);
		interestCoverage_operatingCashFlowVSinterestexpense = operatingCashFlow.divide(interestExpense);
		
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("netInterestEarningsAfterTaxes", netInterestEarningsAfterTaxes.toString());
		json.put("longTermDebt", longTermDebt.toString());
		json.put("netDebt", netDebt.toString());
		json.put("equity", equity.toString());
		json.put("debtToCapitalRatio", debtToCapitalRatio.toString());
		json.put("netDebtToNetCapitalRatio", netDebtToNetCapitalRatio.toString());
		json.put("spread", spread.toString());
		json.put("netFinancialLeverage", netFinancialLeverage.toString());
		json.put("financialLeverage", financialLeverage.toString());
		
		json.put("currentRatio", currentRatio.toString());
		json.put("quickRatio", quickRatio.toString());
		json.put("cashRatio", cashRatio.toString());
		json.put("interestCoverage_operatingIncomeVSinterestexpense", interestCoverage_operatingIncomeVSinterestexpense.toString());
		json.put("interestCoverage_operatingCashFlowVSinterestexpense", interestCoverage_operatingCashFlowVSinterestexpense.toString());
		
				
		return json;
		
	}
	
	public String toString() {
		return toJSON().toString();
	}

	public Item getNetInterestEarningsAfterTaxes() {
		return netInterestEarningsAfterTaxes;
	}

	public void setNetInterestEarningsAfterTaxes(Item netInterestEarningsAfterTaxes) {
		this.netInterestEarningsAfterTaxes = netInterestEarningsAfterTaxes;
	}

	public Item getLongTermDebt() {
		return longTermDebt;
	}

	public void setLongTermDebt(Item longTermDebt) {
		this.longTermDebt = longTermDebt;
	}

	public Item getNetDebt() {
		return netDebt;
	}

	public void setNetDebt(Item netDebt) {
		this.netDebt = netDebt;
	}

	public Item getEquity() {
		return equity;
	}

	public void setEquity(Item equity) {
		this.equity = equity;
	}

	public Item getDebtToCapitalRatio() {
		return debtToCapitalRatio;
	}

	public void setDebtToCapitalRatio(Item debtToCapitalRatio) {
		this.debtToCapitalRatio = debtToCapitalRatio;
	}

	public Item getNetDebtToNetCapitalRatio() {
		return netDebtToNetCapitalRatio;
	}

	public void setNetDebtToNetCapitalRatio(Item netDebtToNetCapitalRatio) {
		this.netDebtToNetCapitalRatio = netDebtToNetCapitalRatio;
	}

	public Item getSpread() {
		return spread;
	}

	public void setSpread(Item spread) {
		this.spread = spread;
	}

	public Item getNetFinancialLeverage() {
		return netFinancialLeverage;
	}

	public void setNetFinancialLeverage(Item netFinancialLeverage) {
		this.netFinancialLeverage = netFinancialLeverage;
	}

	public Item getFinancialLeverage() {
		return financialLeverage;
	}

	public void setFinancialLeverage(Item financialLeverage) {
		this.financialLeverage = financialLeverage;
	}

	public Item getCurrentRatio() {
		return currentRatio;
	}

	public void setCurrentRatio(Item currentRatio) {
		this.currentRatio = currentRatio;
	}

	public Item getQuickRatio() {
		return quickRatio;
	}

	public void setQuickRatio(Item quickRatio) {
		this.quickRatio = quickRatio;
	}

	public Item getCashRatio() {
		return cashRatio;
	}

	public void setCashRatio(Item cashRatio) {
		this.cashRatio = cashRatio;
	}

	public Item getInterestCoverage_operatingIncomeVSinterestexpense() {
		return interestCoverage_operatingIncomeVSinterestexpense;
	}

	public void setInterestCoverage_operatingIncomeVSinterestexpense(
			Item interestCoverage_operatingIncomeVSinterestexpense) {
		this.interestCoverage_operatingIncomeVSinterestexpense = interestCoverage_operatingIncomeVSinterestexpense;
	}

	public Item getInterestCoverage_operatingCashFlowVSinterestexpense() {
		return interestCoverage_operatingCashFlowVSinterestexpense;
	}

	public void setInterestCoverage_operatingCashFlowVSinterestexpense(
			Item interestCoverage_operatingCashFlowVSinterestexpense) {
		this.interestCoverage_operatingCashFlowVSinterestexpense = interestCoverage_operatingCashFlowVSinterestexpense;
	}

	
	
	
}
