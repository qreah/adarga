package adarga.ratios;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.Company;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;
import utils.Utils;

public class FinancialManagement {
	
	private static final Logger log = Logger.getLogger(FinancialManagement.class.getName());
	
	Item netInterestEarningsAfterTaxes = null;
	Item longTermDebt = null;
	Item netDebt = null;
	Item equity = null;
	Item debtToCapitalRatio = null;
	Item netDebtToNetCapitalRatio = null;
	Item spread = null;
	Item netFinancialLeverage = null;
	Item financialLeverage = null;
	Item currentRatio = null;
	Item quickRatio = null;
	Item cashRatio = null;
	Item interestCoverage_operatingIncomeVSinterestexpense = null;
	Item interestCoverage_operatingCashFlowVSinterestexpense = null;
	
	
	@SuppressWarnings("static-access")
	public void loadFinancialManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs) throws ClassNotFoundException, ServletException, IOException, SQLException {
		Item provisionForIncomeTaxes = is.provisionForIncomeTaxes();
		Item taxRate = provisionForIncomeTaxes.divide(is.netIncome()); 
		Item temp = taxRate.substractNumberAnte(1.0);
		netInterestEarningsAfterTaxes = is.InterestExpense().multiply(temp);
		equity = bs.Equity();
		
		Utils utils = new Utils();
		int lastYear = equity.lastYear();
		longTermDebt = bs.longTermDebt().sum(bs.capitalLeases());
		
		Item shortDebt = bs.shortTermDebt();
		
		netDebt = longTermDebt.sum(shortDebt);
		netDebt = netDebt.substract(bs.totalAssets());
		equity = bs.Equity();
		debtToCapitalRatio = netDebt;
		temp = netDebt.sum(equity);
		debtToCapitalRatio = debtToCapitalRatio.divide(temp);
		temp = netDebt.sum(equity);
		netDebtToNetCapitalRatio = netDebt.divide(temp);
		Item revenue = is.Revenue();
		Item salesOverAssets = is.Revenue().divide(bs.totalAssets());
		Item NOPAT = is.netIncome().sum(is.InterestExpense().multiply(taxRate.substractNumberAnte(1.0)));
		Item NOPATMargin = NOPAT.divide(revenue);
		Item OperatingROA = NOPATMargin.multiply(salesOverAssets);
		Item afterTaxCostOfDebt = netInterestEarningsAfterTaxes.divide(netDebt);
		spread = OperatingROA.substract(afterTaxCostOfDebt);
		netFinancialLeverage = netDebt.divide(equity);
		financialLeverage = spread.multiply(netFinancialLeverage);
		Item currentAssets = bs.totalCurrentAssets();
		Item currentLiabilities = bs.totalCurrentLiabilities();
		currentRatio = currentAssets.divide(currentLiabilities);
		Item accountsReceivable = bs.Receivables();
		Item cashAndMarketableSecurities = bs.totalCash();
		quickRatio = accountsReceivable.sum(cashAndMarketableSecurities);
		quickRatio = quickRatio.divide(currentLiabilities);
		cashRatio = cashAndMarketableSecurities.divide(currentLiabilities);
		Item operatingIncome = is.OperatingIncome();
		Item operatingCashFlow = cs.NetCashProvidedByOperatingActivities();
		Item interestExpense = is.InterestExpense();
		
		interestCoverage_operatingIncomeVSinterestexpense = operatingIncome.divide(interestExpense);
		interestCoverage_operatingCashFlowVSinterestexpense = operatingCashFlow.divide(interestExpense);
		
	}
	
	public FinancialManagement() {
		// TODO Auto-generated constructor stub
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("netInterestEarningsAfterTaxes", netInterestEarningsAfterTaxes.toJSON());
		json.put("longTermDebt", longTermDebt.toJSON());
		json.put("netDebt", netDebt.toJSON());
		json.put("equity", equity.toJSON());
		json.put("debtToCapitalRatio", debtToCapitalRatio.toJSON());
		json.put("netDebtToNetCapitalRatio", netDebtToNetCapitalRatio.toJSON());
		json.put("spread", spread.toJSON());
		json.put("netFinancialLeverage", netFinancialLeverage.toJSON());
		json.put("financialLeverage", financialLeverage.toJSON());
		
		json.put("currentRatio", currentRatio.toJSON());
		json.put("quickRatio", quickRatio.toJSON());
		json.put("cashRatio", cashRatio.toJSON());
		json.put("interestCoverage_operatingIncomeVSinterestexpense", interestCoverage_operatingIncomeVSinterestexpense.toJSON());
		json.put("interestCoverage_operatingCashFlowVSinterestexpense", interestCoverage_operatingCashFlowVSinterestexpense.toJSON());
		
				
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
