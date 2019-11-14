package adarga.ratios;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.CompanyInformation;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;
import utils.Utils;

public class CashManagement {
	
	private static final Logger log = Logger.getLogger(CashManagement.class.getName());
	
	Item operatingCashFlow = null;
	Item ChangeOperatingCashFlow = null;
	Item investingCashFlow = null;
	Item ChangeInvestingCashFlow = null;
	Item financingCashFlow = null;
	Item ChangeFinancingCashFlow = null;
	Item CAPEX = null;
	Item changeInCAPEX = null;
	Item adquisitions = null;
	Item changeInAdquisitions = null;
	Item securitiesNet = null;
	Item changeInSecurities = null;
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
	
	Double FCFYield = null;  
	Double DividendYield = null;		
	Double RepurchasesYield = null;		
	Double SecuritiesSaleYield = null; 
	Double DebtBuyingYield = null; 
	Double CAPEXYield = null; 
	Double AdquisitionsYield = null; 
	Double InvestingYield = null;
	
	public CashManagement() {
		
	}
	
	@SuppressWarnings("static-access")
	public void loadCashManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs, CompanyInformation ci) throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		Item revenue = is.Revenue();
		
		
		operatingCashFlow = cs.NetCashProvidedByOperatingActivities();
		ChangeOperatingCashFlow = operatingCashFlow.changeInItem();
		investingCashFlow = cs.NetCashUsedForInvestingActivities();
		ChangeInvestingCashFlow = investingCashFlow.changeInItem();
		financingCashFlow = cs.NetCashProvidedByUsedForFinancingActivities();
		ChangeFinancingCashFlow = financingCashFlow.changeInItem();
		
		plant = cs.InvestmentsInPropertyPlantAndEquipment();
		plantReductions = cs.PropertyPlantAndEquipmentReductions();
		CAPEX = plant.substract(plantReductions);
		changeInCAPEX = CAPEX.changeInItem();
		adquisitions = cs.AcquisitionsNet();
		changeInAdquisitions = adquisitions.changeInItem();
		
		securitiesNet = cs.SecuritiesSale().substract(cs.PurchasesOfInvestments());
		changeInSecurities = securitiesNet.changeInItem();
		
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
		debtRepayment = cs.DebtIssued().substract(cs.DebtRepayment());
		changeInDebtRepayment = debtRepayment.changeInItem();
		
		Double MktCap = ci.getMktCap();
		int year = FCF.lastYear();
		FCFYield = FCF.getValue(year) / MktCap;  
		DividendYield = dividends.getValue(year) / MktCap;
		RepurchasesYield = (CommonStockRepurchased.getValue(year) - CommonStockIssued.getValue(year)) / MktCap;	
		
		SecuritiesSaleYield = securitiesNet.getValue(year)  / MktCap; 
		DebtBuyingYield = debtRepayment.getValue(year)  / MktCap; 
		CAPEXYield = CAPEX.getValue(year)  / MktCap; 
		AdquisitionsYield = adquisitions.getValue(year)  / MktCap; 
		InvestingYield = (CAPEXYield + AdquisitionsYield);
	
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		json.put("FCFYield", FCFYield);
		json.put("DividendYield", DividendYield);
		json.put("RepurchasesYield", RepurchasesYield);
		json.put("SecuritiesSaleYield", SecuritiesSaleYield);
		json.put("DebtBuyingYield", DebtBuyingYield);
		json.put("CAPEXYield", CAPEXYield);
		json.put("AdquisitionsYield", AdquisitionsYield);
		json.put("InvestingYield", InvestingYield);
		
		json.put("operatingCashFlow", operatingCashFlow.toJSON());
		json.put("ChangeOperatingCashFlow", ChangeOperatingCashFlow.toJSON());
		json.put("investingCashFlow", investingCashFlow.toJSON());
		json.put("ChangeInvestingCashFlow", ChangeInvestingCashFlow.toJSON());
		json.put("financingCashFlow", financingCashFlow.toJSON());
		json.put("ChangeFinancingCashFlow", ChangeInvestingCashFlow.toJSON());
		
		json.put("CAPEX", CAPEX.toJSON());
		json.put("changeInCAPEX", changeInCAPEX.toJSON());
		json.put("adquisitions", adquisitions.toJSON());
		json.put("changeInAdquisitions", changeInAdquisitions.toJSON());
		
		json.put("securitiesNet", securitiesNet.toJSON());
		json.put("changeInSecurities", changeInSecurities.toJSON());
		
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
	
	public Double getFCFYield() {
		return FCFYield;
	}

	public void setFCFYield(Double fCFYield) {
		FCFYield = fCFYield;
	}

	public Double getDividendYield() {
		return DividendYield;
	}

	public void setDividendYield(Double dividendYield) {
		DividendYield = dividendYield;
	}

	public Double getRepurchasesYield() {
		return RepurchasesYield;
	}

	public void setRepurchasesYield(Double repurchasesYield) {
		RepurchasesYield = repurchasesYield;
	}

	public Double getSecuritiesSaleYield() {
		return SecuritiesSaleYield;
	}

	public void setSecuritiesSaleingYield(Double securitiesSaleYield) {
		SecuritiesSaleYield = securitiesSaleYield;
	}

	public Double getDebtBuyingYield() {
		return DebtBuyingYield;
	}

	public void setDebtBuyingYield(Double debtBuyingYield) {
		DebtBuyingYield = debtBuyingYield;
	}

	public Double getCAPEXYield() {
		return CAPEXYield;
	}

	public void setCAPEXYield(Double cAPEXYield) {
		CAPEXYield = cAPEXYield;
	}

	public Double getAdquisitionsYield() {
		return AdquisitionsYield;
	}

	public void setAdquisitionsYield(Double adquisitionsYield) {
		AdquisitionsYield = adquisitionsYield;
	}

	public Double getInvestingYield() {
		return InvestingYield;
	}

	public void setInvestingYield(Double investingYield) {
		InvestingYield = investingYield;
	}

	public Item getChangeFinancingCashFlow() {
		return ChangeFinancingCashFlow;
	}

	public void setChangeFinancingCashFlow(Item changeFinancingCashFlow) {
		ChangeFinancingCashFlow = changeFinancingCashFlow;
	}

	public Item getCommonStockRepurchased() {
		return CommonStockRepurchased;
	}

	public void setCommonStockRepurchased(Item commonStockRepurchased) {
		CommonStockRepurchased = commonStockRepurchased;
	}

	public Item getCommonStockIssued() {
		return CommonStockIssued;
	}

	public void setCommonStockIssued(Item commonStockIssued) {
		CommonStockIssued = commonStockIssued;
	}

	public Item getPlant() {
		return plant;
	}

	public void setPlant(Item plant) {
		this.plant = plant;
	}

	public Item getPlantReductions() {
		return plantReductions;
	}

	public void setPlantReductions(Item plantReductions) {
		this.plantReductions = plantReductions;
	}
	
	
	
	
}
