package adarga.ratios;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;

public class CashManagement {
	
	Item operatingCashFlow;
	Item ChangeOperatingCashFlow;
	Item investingCashFlow;
	Item ChangeInvestingCashFlow;
	Item CAPEX;
	Item changeInCAPEX;
	Item adquisitions;
	Item changeInAdquisitions;
	Item securitiesNet;
	Item changeInSecurities;
	Item financingCashFlow;
	Item investing; //CAPEX and adquisitions
	Item changeInInvesting;
	Item FCF;
	Item changeInFCF;
	Item dividends;
	Item changeInDividends;
	Item stockRepurchase;
	Item changeInStockRepurchase;
	Item debtRepayment;
	Item changeInDebtRepayment;
		
	
	public CashManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs) {
		Item revenue = is.get("Revenue");
		operatingCashFlow = cs.get("Net cash provided by operating activities");
		ChangeOperatingCashFlow = operatingCashFlow.changeInItem();
		investingCashFlow = cs.get("Net cash used for investing activities");
		ChangeInvestingCashFlow = investingCashFlow.changeInItem();
		CAPEX = cs.get("Investments in property, plant, and equipment").substract(cs.get("Property, plant, and equipment reductions"));
		changeInCAPEX = CAPEX.changeInItem();
		adquisitions = cs.get("Acquisitions, net");
		changeInAdquisitions = adquisitions.changeInItem();
		securitiesNet = cs.get("Sales\\/Maturities of investments").substract(cs.get("Purchases of investments"));
		changeInSecurities = changeInSecurities.changeInItem();
		financingCashFlow = cs.get("Net cash provided by (used for) financing activities");
		investing = CAPEX.sum(adquisitions); //CAPEX and adquisitions
		changeInInvesting = investing.changeInItem();
		FCF = cs.get("Free cash flow");
		changeInFCF = FCF.changeInItem();
		dividends = cs.get("Dividend paid");
		changeInDividends = dividends.changeInItem();
		stockRepurchase = cs.get("Common stock repurchased").substract(cs.get("Common stock issued"));
		changeInStockRepurchase = stockRepurchase.changeInItem();
		debtRepayment = cs.get("Debt repayment").substract(cs.get("Debt issued"));
		changeInDebtRepayment = debtRepayment.changeInItem();
	
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("operatingCashFlow", operatingCashFlow.toString());
		json.put("ChangeOperatingCashFlow", ChangeOperatingCashFlow.toString());
		json.put("investingCashFlow", investingCashFlow.toString());
		json.put("ChangeInvestingCashFlow", ChangeInvestingCashFlow.toString());
		
		json.put("CAPEX", CAPEX.toString());
		json.put("changeInCAPEX", changeInCAPEX.toString());
		json.put("adquisitions", adquisitions.toString());
		json.put("changeInAdquisitions", changeInAdquisitions.toString());
		
		json.put("securitiesNet", securitiesNet.toString());
		json.put("changeInSecurities", changeInSecurities.toString());
		json.put("financingCashFlow", financingCashFlow.toString());
		json.put("investing", investing.toString());
		
		json.put("changeInInvesting", changeInInvesting.toString());
		json.put("FCF", FCF.toString());
		json.put("changeInFCF", changeInFCF.toString());
		json.put("dividends", dividends.toString());
		
		json.put("changeInDividends", changeInDividends.toString());
		json.put("stockRepurchase", stockRepurchase.toString());
		json.put("changeInStockRepurchase", changeInStockRepurchase.toString());
		json.put("debtRepayment", debtRepayment.toString());
		json.put("changeInDebtRepayment", changeInDebtRepayment.toString());	
		
		return json;
		
	}
	
	public String toString() {
		return toJSON().toString();
	}

	public Item getOperatingCashFlow() {
		return operatingCashFlow;
	}

	public void setOperatingCashFlow(Item operatingCashFlow) {
		this.operatingCashFlow = operatingCashFlow;
	}

	public Item getChangeOperatingCashFlow() {
		return ChangeOperatingCashFlow;
	}

	public void setChangeOperatingCashFlow(Item changeOperatingCashFlow) {
		ChangeOperatingCashFlow = changeOperatingCashFlow;
	}

	public Item getInvestingCashFlow() {
		return investingCashFlow;
	}

	public void setInvestingCashFlow(Item investingCashFlow) {
		this.investingCashFlow = investingCashFlow;
	}

	public Item getChangeInvestingCashFlow() {
		return ChangeInvestingCashFlow;
	}

	public void setChangeInvestingCashFlow(Item changeInvestingCashFlow) {
		ChangeInvestingCashFlow = changeInvestingCashFlow;
	}

	public Item getCAPEX() {
		return CAPEX;
	}

	public void setCAPEX(Item cAPEX) {
		CAPEX = cAPEX;
	}

	public Item getChangeInCAPEX() {
		return changeInCAPEX;
	}

	public void setChangeInCAPEX(Item changeInCAPEX) {
		this.changeInCAPEX = changeInCAPEX;
	}

	public Item getAdquisitions() {
		return adquisitions;
	}

	public void setAdquisitions(Item adquisitions) {
		this.adquisitions = adquisitions;
	}

	public Item getChangeInAdquisitions() {
		return changeInAdquisitions;
	}

	public void setChangeInAdquisitions(Item changeInAdquisitions) {
		this.changeInAdquisitions = changeInAdquisitions;
	}

	public Item getSecuritiesNet() {
		return securitiesNet;
	}

	public void setSecuritiesNet(Item securitiesNet) {
		this.securitiesNet = securitiesNet;
	}

	public Item getChangeInSecurities() {
		return changeInSecurities;
	}

	public void setChangeInSecurities(Item changeInSecurities) {
		this.changeInSecurities = changeInSecurities;
	}

	public Item getFinancingCashFlow() {
		return financingCashFlow;
	}

	public void setFinancingCashFlow(Item financingCashFlow) {
		this.financingCashFlow = financingCashFlow;
	}

	public Item getInvesting() {
		return investing;
	}

	public void setInvesting(Item investing) {
		this.investing = investing;
	}

	public Item getChangeInInvesting() {
		return changeInInvesting;
	}

	public void setChangeInInvesting(Item changeInInvesting) {
		this.changeInInvesting = changeInInvesting;
	}

	public Item getFCF() {
		return FCF;
	}

	public void setFCF(Item fCF) {
		FCF = fCF;
	}

	public Item getChangeInFCF() {
		return changeInFCF;
	}

	public void setChangeInFCF(Item changeInFCF) {
		this.changeInFCF = changeInFCF;
	}

	public Item getDividends() {
		return dividends;
	}

	public void setDividends(Item dividends) {
		this.dividends = dividends;
	}

	public Item getChangeInDividends() {
		return changeInDividends;
	}

	public void setChangeInDividends(Item changeInDividends) {
		this.changeInDividends = changeInDividends;
	}

	public Item getStockRepurchase() {
		return stockRepurchase;
	}

	public void setStockRepurchase(Item stockRepurchase) {
		this.stockRepurchase = stockRepurchase;
	}

	public Item getChangeInStockRepurchase() {
		return changeInStockRepurchase;
	}

	public void setChangeInStockRepurchase(Item changeInStockRepurchase) {
		this.changeInStockRepurchase = changeInStockRepurchase;
	}

	public Item getDebtRepayment() {
		return debtRepayment;
	}

	public void setDebtRepayment(Item debtRepayment) {
		this.debtRepayment = debtRepayment;
	}

	public Item getChangeInDebtRepayment() {
		return changeInDebtRepayment;
	}

	public void setChangeInDebtRepayment(Item changeInDebtRepayment) {
		this.changeInDebtRepayment = changeInDebtRepayment;
	}
	
	
}
