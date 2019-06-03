package adarga.ratios;

import java.util.logging.Logger;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.CompanyInformation;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;
import utils.Utils;

public class GlobalManagement {
	
	private static final Logger log = Logger.getLogger(GlobalManagement.class.getName());
	
	Item operatingROA;
	Item salesOverAssets;
	
	Item financialLeverageGain;
	Item ROE;
	Item returnOnTangibleEquity;
	Item payOut;
	Item dividendYield;
	Item FCFOverEquity;
	Item FCFPerShare;
	Item earningsPerShare;
	Item operatingIncomePerShare;
	Item growthRate;
	
	Item salesGrowthRate;
	Item NOPATMargin;
	Item beginningNetOperatingWCOverSales;
	Item beginningNetOperatingLTAssetsOverSales;
	Item beginningNetDebt2CapitalRatio;
	Item afterTaxCostOfDebt;
	
	@SuppressWarnings("static-access")
	public GlobalManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs, CompanyInformation ci) {
		
		Item revenue = is.Revenue();
		Item provisionForIncomeTaxes = is.provisionForIncomeTaxes();
		Item taxRate = provisionForIncomeTaxes.divide(is.netIncome()); 
		Item NOPAT = is.netIncome().sum(is.InterestExpense().multiply(taxRate.substractNumberAnte(1.0)));
		NOPATMargin = NOPAT.divide(revenue);
		salesOverAssets = is.Revenue().divide(bs.totalAssets());
		operatingROA = NOPATMargin.multiply(salesOverAssets);
		Item longTermDebt = bs.longTermDebt().sum(bs.capitalLeases());
		
		Item netDebt = longTermDebt.sum(bs.shortTermDebt());
		netDebt = netDebt.substract(bs.totalCash());
		Item temp = taxRate.substractNumberAnte(1.0);
		Item netInterestEarningsAfterTaxes = is.InterestExpense().multiply(temp);
		afterTaxCostOfDebt = netInterestEarningsAfterTaxes.divide(netDebt);
		Item equity = bs.Equity();
		Item spread = operatingROA.substract(afterTaxCostOfDebt);
		Item netFinancialLeverage = netDebt.divide(equity);
		financialLeverageGain = spread.multiply(netFinancialLeverage);
		Item netIncome = is.netIncome();
		ROE = netIncome.divide(equity);
		Item goodwillAndIntangibles = bs.Goodwill().sum(bs.IntagibleAssets());
		Item temp2 = equity.substract(goodwillAndIntangibles);
		returnOnTangibleEquity = netIncome.divide(temp2);
		Item FCF = cs.FCF();
		Item dividends = cs.DividendPaid();
		
		Double numberOfShares = ci.numberOfShares();
		payOut = dividends.divideNumber(numberOfShares);
		
		
		dividendYield = dividends.divideNumber(ci.getStockPrice());
		
		FCFOverEquity = FCF.divide(equity);
		FCFPerShare = FCF.divideNumber(numberOfShares);
		earningsPerShare = netIncome.divideNumber(numberOfShares);
		Item operatingIncome = is.OperatingIncome();
		operatingIncomePerShare = operatingIncome.divideNumber(numberOfShares);
		
		growthRate = ROE.multiply(payOut.substractNumberAnte(1.0));
		salesGrowthRate = revenue.changeInItem();
		Item currentAssets = bs.totalCurrentAssets();
		Item currentLiabilities = bs.totalCurrentLiabilities();
		Item cashAndMarketableSecurities = bs.totalCash();
		Item shortTermDebtAndCurrentPortionOfLongTermDebt = bs.shortTermDebt();
		Item netWorkingCapital = currentAssets.substract(currentLiabilities);
		netWorkingCapital = netWorkingCapital.substract(cashAndMarketableSecurities);
		int YearB = netWorkingCapital.lastYear();
		netWorkingCapital = netWorkingCapital.sum(shortTermDebtAndCurrentPortionOfLongTermDebt);
		beginningNetOperatingWCOverSales = netWorkingCapital.divide(revenue);
		Item LTAssets = bs.totalAssets().substract(currentAssets);
		Item temp4 = LTAssets.substract(goodwillAndIntangibles);
		beginningNetOperatingLTAssetsOverSales = temp4.divide(revenue);
		Item netAssets = LTAssets.sum(netWorkingCapital);
		beginningNetDebt2CapitalRatio = netAssets.divide(netDebt);
		
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("operatingROA", operatingROA.toJSON());
		json.put("salesOverAssets", salesOverAssets.toJSON());
		json.put("financialLeverageGain", financialLeverageGain.toJSON());
		json.put("ROE", ROE.toJSON());
		json.put("returnOnTangibleEquity", returnOnTangibleEquity.toJSON());
		
		json.put("payOut", payOut.toJSON());
		json.put("dividendYield", dividendYield.toJSON());
		json.put("FCFOverEquity", FCFOverEquity.toJSON());
		json.put("FCFPerShare", FCFPerShare.toJSON());
		json.put("earningsPerShare", earningsPerShare.toJSON());
		
		json.put("operatingIncomePerShare", operatingIncomePerShare.toJSON());
		json.put("growthRate", growthRate.toJSON());
		json.put("salesGrowthRate", salesGrowthRate.toJSON());
		json.put("NOPATMargin", NOPATMargin.toJSON());
		json.put("beginningNetOperatingWCOverSales", beginningNetOperatingWCOverSales.toJSON());
		
		json.put("beginningNetOperatingLTAssetsOverSales", beginningNetOperatingLTAssetsOverSales.toJSON());
		json.put("beginningNetDebt2CapitalRatio", beginningNetDebt2CapitalRatio.toJSON());
		json.put("afterTaxCostOfDebt", afterTaxCostOfDebt.toJSON());
		
		return json;
		
	}

	public Item getOperatingROA() {
		return operatingROA;
	}

	public void setOperatingROA(Item operatingROA) {
		this.operatingROA = operatingROA;
	}

	public Item getSalesOverAssets() {
		return salesOverAssets;
	}

	public void setSalesOverAssets(Item salesOverAssets) {
		this.salesOverAssets = salesOverAssets;
	}

	public Item getFinancialLeverageGain() {
		return financialLeverageGain;
	}

	public void setFinancialLeverageGain(Item financialLeverageGain) {
		this.financialLeverageGain = financialLeverageGain;
	}

	public Item getROE() {
		return ROE;
	}

	public void setROE(Item rOE) {
		ROE = rOE;
	}

	public Item getReturnOnTangibleEquity() {
		return returnOnTangibleEquity;
	}

	public void setReturnOnTangibleEquity(Item returnOnTangibleEquity) {
		this.returnOnTangibleEquity = returnOnTangibleEquity;
	}

	public Item getPayOut() {
		return payOut;
	}

	public void setPayOut(Item payOut) {
		this.payOut = payOut;
	}

	public Item getDividendYield() {
		return dividendYield;
	}

	public void setDividendYield(Item dividendYield) {
		this.dividendYield = dividendYield;
	}

	public Item getFCFOverEquity() {
		return FCFOverEquity;
	}

	public void setFCFOverEquity(Item fCFOverEquity) {
		FCFOverEquity = fCFOverEquity;
	}

	public Item getFCFPerShare() {
		return FCFPerShare;
	}

	public void setFCFPerShare(Item fCFPerShare) {
		FCFPerShare = fCFPerShare;
	}

	public Item getEarningsPerShare() {
		return earningsPerShare;
	}

	public void setEarningsPerShare(Item earningsPerShare) {
		this.earningsPerShare = earningsPerShare;
	}

	public Item getOperatingIncomePerShare() {
		return operatingIncomePerShare;
	}

	public void setOperatingIncomePerShare(Item operatingIncomePerShare) {
		this.operatingIncomePerShare = operatingIncomePerShare;
	}

	public Item getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(Item growthRate) {
		this.growthRate = growthRate;
	}

	public Item getSalesGrowthRate() {
		return salesGrowthRate;
	}

	public void setSalesGrowthRate(Item salesGrowthRate) {
		this.salesGrowthRate = salesGrowthRate;
	}

	public Item getNOPATMargin() {
		return NOPATMargin;
	}

	public void setNOPATMargin(Item nOPATMargin) {
		NOPATMargin = nOPATMargin;
	}

	public Item getBeginningNetOperatingWCOverSales() {
		return beginningNetOperatingWCOverSales;
	}

	public void setBeginningNetOperatingWCOverSales(Item beginningNetOperatingWCOverSales) {
		this.beginningNetOperatingWCOverSales = beginningNetOperatingWCOverSales;
	}

	public Item getBeginningNetOperatingLTAssetsOverSales() {
		return beginningNetOperatingLTAssetsOverSales;
	}

	public void setBeginningNetOperatingLTAssetsOverSales(Item beginningNetOperatingLTAssetsOverSales) {
		this.beginningNetOperatingLTAssetsOverSales = beginningNetOperatingLTAssetsOverSales;
	}

	public Item getBeginningNetDebt2CapitalRatio() {
		return beginningNetDebt2CapitalRatio;
	}

	public void setBeginningNetDebt2CapitalRatio(Item beginningNetDebt2CapitalRatio) {
		this.beginningNetDebt2CapitalRatio = beginningNetDebt2CapitalRatio;
	}

	public Item getAfterTaxCostOfDebt() {
		return afterTaxCostOfDebt;
	}

	public void setAfterTaxCostOfDebt(Item afterTaxCostOfDebt) {
		this.afterTaxCostOfDebt = afterTaxCostOfDebt;
	}
	
	
	
}
