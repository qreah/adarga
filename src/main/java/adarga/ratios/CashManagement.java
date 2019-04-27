package adarga.ratios;

import java.util.logging.Logger;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;
import utils.Utils;

public class CashManagement {
	
	private static final Logger log = Logger.getLogger(CashManagement.class.getName());
	
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
		
	
	@SuppressWarnings("static-access")
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
		Utils utils = new Utils();
		
		log.info("Purchases: " + cs.get("Purchases of investments"));
		securitiesNet = cs.get("Sales/Maturities of investments").substract(cs.get("Purchases of investments"));
		changeInSecurities = securitiesNet.changeInItem();
		financingCashFlow = cs.get("Net cash provided by (used for) financing activities");
		investing = CAPEX.sum(adquisitions); //CAPEX and adquisitions
		
		changeInInvesting = investing.changeInItem();
		FCF = cs.get("Free cash flow");
		
		changeInFCF = FCF.changeInItem();
		dividends = utils.controlNull(cs.get("Dividend paid"), FCF.lastYear());
		log.info("dividends: " + dividends);
		changeInDividends = dividends.changeInItem();
		stockRepurchase = utils.controlNull(cs.get("Common stock repurchased"), FCF.lastYear()).substract(utils.controlNull(cs.get("Common stock issued"), FCF.lastYear()));
		changeInStockRepurchase = stockRepurchase.changeInItem();
		debtRepayment = utils.controlNull(cs.get("Debt repayment"), FCF.lastYear()).substract(utils.controlNull(cs.get("Debt issued"), FCF.lastYear()));
		changeInDebtRepayment = debtRepayment.changeInItem();
	
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("operatingCashFlow", operatingCashFlow.toJSON());
		json.put("ChangeOperatingCashFlow", ChangeOperatingCashFlow.toJSON());
		json.put("investingCashFlow", investingCashFlow.toJSON());
		json.put("ChangeInvestingCashFlow", ChangeInvestingCashFlow.toJSON());
		
		json.put("CAPEX", CAPEX.toJSON());
		json.put("changeInCAPEX", changeInCAPEX.toJSON());
		json.put("adquisitions", adquisitions.toJSON());
		json.put("changeInAdquisitions", changeInAdquisitions.toJSON());
		
		json.put("securitiesNet", securitiesNet.toJSON());
		json.put("changeInSecurities", changeInSecurities.toJSON());
		json.put("financingCashFlow", financingCashFlow.toJSON());
		json.put("investing", investing.toJSON());
		
		json.put("changeInInvesting", changeInInvesting.toJSON());
		json.put("FCF", FCF.toJSON());
		json.put("changeInFCF", changeInFCF.toJSON());
		json.put("dividends", dividends.toJSON());
		
		json.put("changeInDividends", changeInDividends.toJSON());
		json.put("stockRepurchase", stockRepurchase.toJSON());
		json.put("changeInStockRepurchase", changeInStockRepurchase.toJSON());
		json.put("debtRepayment", debtRepayment.toJSON());
		json.put("changeInDebtRepayment", changeInDebtRepayment.toJSON());	
		
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
