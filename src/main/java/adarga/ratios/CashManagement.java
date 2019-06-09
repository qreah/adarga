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
	
	Item operatingCashFlow = null;
	Item ChangeOperatingCashFlow = null;
	Item investingCashFlow = null;
	Item ChangeInvestingCashFlow = null;
	Item CAPEX = null;
	Item changeInCAPEX = null;
	Item adquisitions = null;
	Item changeInAdquisitions = null;
	Item securitiesNet = null;
	Item changeInSecurities = null;
	Item financingCashFlow = null;
	Item investing = null; //CAPEX and adquisitions
	Item changeInInvesting = null;
	Item FCF = null;
	Item changeInFCF = null;
	Item dividends = null;
	Item changeInDividends = null;
	Item stockRepurchase = null;
	Item changeInStockRepurchase = null;
	Item debtRepayment = null;
	Item changeInDebtRepayment = null;
	Item CommonStockRepurchased = null;
	Item CommonStockIssued = null;
	Item plant = null;
	Item plantReductions = null;
	
	public CashManagement() {
		
	}
	
	@SuppressWarnings("static-access")
	public void loadCashManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs) {
		
		Item revenue = is.Revenue();
		
		operatingCashFlow = cs.NetCashProvidedByOperatingActivities();
		ChangeOperatingCashFlow = operatingCashFlow.changeInItem();
		investingCashFlow = cs.NetCashUsedForInvestingActivities();
		ChangeInvestingCashFlow = investingCashFlow.changeInItem();
		
		plant = cs.InvestmentsInPropertyPlantAndEquipment();
		plantReductions = cs.PropertyPlantAndEquipmentReductions();
		CAPEX = plant.substract(plantReductions);
		changeInCAPEX = CAPEX.changeInItem();
		adquisitions = cs.AcquisitionsNet();
		changeInAdquisitions = adquisitions.changeInItem();
		
		securitiesNet = cs.SecuritiesInvestment().substract(cs.PurchasesOfInvestments());
		changeInSecurities = securitiesNet.changeInItem();
		financingCashFlow = cs.NetCashProvidedByUsedForFinancingActivities();
		investing = CAPEX.sum(adquisitions); //CAPEX and adquisitions
		
		changeInInvesting = investing.changeInItem();
		FCF = cs.FCF();
		
		changeInFCF = FCF.changeInItem();
		dividends = cs.DividendPaid();
		
		changeInDividends = dividends.changeInItem();
		
		CommonStockRepurchased = cs.CommonStockRepurchased();
		CommonStockIssued = cs.CommonStockIssued();
		stockRepurchase = CommonStockRepurchased.substract(CommonStockIssued);
		changeInStockRepurchase = stockRepurchase.changeInItem();
		debtRepayment = cs.DebtRepayment().substract(cs.DebtIssued());
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
