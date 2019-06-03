package adarga.ratios;

import java.util.logging.Logger;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;
import utils.Utils;

public class InvestmentManagement {
	private static final Logger log = Logger.getLogger(InvestmentManagement.class.getName());
	
	Item accountsReceivable;
	Item accountsReceivableGrowth;
	Item inventory;
	Item inventoryGrowth;
	Item accountsPayable;
	Item currentAssets;
	Item currentLiabilities;
	Item cashAndMarketableSecurities;
	Item shortTermDebtAndCurrentPortionOfLongTermDebt;
	Item netWorkingCapital;
	Item LTAssets;
	Item goodwillAndIntangibles;
	Item nonInterestBearingLTLiabilities;
	Item netLTAssets;
	Item netAssets;
	Item longTermDebt;
	Item accountsReceivableOverSales;
	Item inventorySales;
	Item accountsPayableSales;
	Item daysReceivables;
	Item daysInventory;
	Item daysPayables;
	Item salesOverWorkingCapital;
	Item salesOverNetLTAssets;
	Item salesOverNetAssets;
	Item cashOverNetAssets;
	Item cashOverReceivables;
	
	@SuppressWarnings("static-access")
	public InvestmentManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs) {
		Item revenue = is.Revenue();
		Item COGS = is.costOfRevenue();
		accountsReceivable = bs.Receivables();
		accountsReceivableGrowth = accountsReceivable.changeInItem();
		inventory = bs.Inventories();
		inventoryGrowth = inventory.changeInItem();
		accountsPayable = bs.accountsPayable();
		currentAssets = bs.totalCurrentAssets();
		currentLiabilities = bs.totalCurrentLiabilities();
		cashAndMarketableSecurities = bs.totalCash();
		shortTermDebtAndCurrentPortionOfLongTermDebt = bs.shortTermDebt();
		netWorkingCapital = currentAssets.substract(currentLiabilities);
		
		netWorkingCapital = netWorkingCapital.substract(cashAndMarketableSecurities);
		netWorkingCapital = netWorkingCapital.sum(shortTermDebtAndCurrentPortionOfLongTermDebt);
		LTAssets = bs.totalAssets().substract(currentAssets);
		goodwillAndIntangibles = bs.Goodwill().sum(bs.IntagibleAssets());
		
		longTermDebt = bs.longTermDebt().sum(bs.capitalLeases());
		nonInterestBearingLTLiabilities = bs.totalAssets().substract(longTermDebt);
		netLTAssets = LTAssets.substract(nonInterestBearingLTLiabilities);
		netAssets = LTAssets.sum(netWorkingCapital);
		accountsReceivableOverSales = accountsReceivable.divide(revenue);
		inventorySales = inventory.divide(revenue);
		accountsPayableSales = accountsPayable.divide(revenue);
		daysReceivables = accountsReceivable.divide(revenue).multiplyNumber(365.0);
		daysInventory = inventory.divide(COGS).multiplyNumber(365.0);
		daysPayables = accountsPayable.divide(COGS).multiplyNumber(365.0);
		salesOverWorkingCapital = revenue.divide(netWorkingCapital);
		salesOverNetLTAssets = revenue.divide(netLTAssets);
		salesOverNetAssets = revenue.divide(netAssets);
		cashOverNetAssets = cashAndMarketableSecurities.divide(netAssets);
		cashOverReceivables = cashAndMarketableSecurities.divide(accountsReceivable);
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("accountsReceivable", accountsReceivable.toJSON());
		json.put("accountsReceivableGrowth", accountsReceivableGrowth.toJSON());
		json.put("inventory", inventory.toJSON());
		json.put("inventoryGrowth", inventoryGrowth.toJSON());
		json.put("accountsPayable", accountsPayable.toJSON());
		json.put("currentAssets", currentAssets.toJSON());
		json.put("currentLiabilities", currentLiabilities.toJSON());
		json.put("cashAndMarketableSecurities", cashAndMarketableSecurities.toJSON());
		json.put("shortTermDebtAndCurrentPortionOfLongTermDebt", shortTermDebtAndCurrentPortionOfLongTermDebt.toJSON());
		json.put("netWorkingCapital", netWorkingCapital.toJSON());
		json.put("LTAssets", LTAssets.toJSON());
		json.put("goodwillAndIntangibles", goodwillAndIntangibles.toJSON());
		json.put("nonInterestBearingLTLiabilities", nonInterestBearingLTLiabilities.toJSON());
		json.put("netLTAssets", netLTAssets.toJSON());
		json.put("netAssets", netAssets.toJSON());
		json.put("longTermDebt", longTermDebt.toJSON());
		json.put("accountsReceivableOverSales", accountsReceivableOverSales.toJSON());
		json.put("inventorySales", inventorySales.toJSON());
		json.put("accountsPayableSales", accountsPayableSales.toJSON());
		json.put("daysReceivables", daysReceivables.toJSON());
		json.put("daysInventory", daysInventory.toJSON());
		json.put("daysPayables", daysPayables.toJSON());
		json.put("salesOverWorkingCapital", salesOverWorkingCapital.toJSON());
		json.put("salesOverNetLTAssets", salesOverNetLTAssets.toJSON());
		json.put("salesOverNetAssets", salesOverNetAssets.toJSON());
		json.put("cashOverNetAssets", cashOverNetAssets.toJSON());
		json.put("cashOverReceivables", cashOverReceivables.toJSON());
		
		return json;
		
	}
	
	public String toString() {
		return toJSON().toString();
	}

	public Item getAccountsReceivable() {
		return accountsReceivable;
	}

	public void setAccountsReceivable(Item accountsReceivable) {
		this.accountsReceivable = accountsReceivable;
	}

	public Item getAccountsReceivableGrowth() {
		return accountsReceivableGrowth;
	}

	public void setAccountsReceivableGrowth(Item accountsReceivableGrowth) {
		this.accountsReceivableGrowth = accountsReceivableGrowth;
	}

	public Item getInventory() {
		return inventory;
	}

	public void setInventory(Item inventory) {
		this.inventory = inventory;
	}

	public Item getInventoryGrowth() {
		return inventoryGrowth;
	}

	public void setInventoryGrowth(Item inventoryGrowth) {
		this.inventoryGrowth = inventoryGrowth;
	}

	public Item getAccountsPayable() {
		return accountsPayable;
	}

	public void setAccountsPayable(Item accountsPayable) {
		this.accountsPayable = accountsPayable;
	}

	public Item getCurrentAssets() {
		return currentAssets;
	}

	public void setCurrentAssets(Item currentAssets) {
		this.currentAssets = currentAssets;
	}

	public Item getCurrentLiabilities() {
		return currentLiabilities;
	}

	public void setCurrentLiabilities(Item currentLiabilities) {
		this.currentLiabilities = currentLiabilities;
	}

	public Item getCashAndMarketableSecurities() {
		return cashAndMarketableSecurities;
	}

	public void setCashAndMarketableSecurities(Item cashAndMarketableSecurities) {
		this.cashAndMarketableSecurities = cashAndMarketableSecurities;
	}

	public Item getShortTermDebtAndCurrentPortionOfLongTermDebt() {
		return shortTermDebtAndCurrentPortionOfLongTermDebt;
	}

	public void setShortTermDebtAndCurrentPortionOfLongTermDebt(Item shortTermDebtAndCurrentPortionOfLongTermDebt) {
		this.shortTermDebtAndCurrentPortionOfLongTermDebt = shortTermDebtAndCurrentPortionOfLongTermDebt;
	}

	public Item getNetWorkingCapital() {
		return netWorkingCapital;
	}

	public void setNetWorkingCapital(Item netWorkingCapital) {
		this.netWorkingCapital = netWorkingCapital;
	}

	public Item getLTAssets() {
		return LTAssets;
	}

	public void setLTAssets(Item lTAssets) {
		LTAssets = lTAssets;
	}

	public Item getGoodwillAndIntangibles() {
		return goodwillAndIntangibles;
	}

	public void setGoodwillAndIntangibles(Item goodwillAndIntangibles) {
		this.goodwillAndIntangibles = goodwillAndIntangibles;
	}

	public Item getNonInterestBearingLTLiabilities() {
		return nonInterestBearingLTLiabilities;
	}

	public void setNonInterestBearingLTLiabilities(Item nonInterestBearingLTLiabilities) {
		this.nonInterestBearingLTLiabilities = nonInterestBearingLTLiabilities;
	}

	public Item getNetLTAssets() {
		return netLTAssets;
	}

	public void setNetLTAssets(Item netLTAssets) {
		this.netLTAssets = netLTAssets;
	}

	public Item getNetAssets() {
		return netAssets;
	}

	public void setNetAssets(Item netAssets) {
		this.netAssets = netAssets;
	}

	public Item getLongTermDebt() {
		return longTermDebt;
	}

	public void setLongTermDebt(Item longTermDebt) {
		this.longTermDebt = longTermDebt;
	}

	public Item getAccountsReceivableOverSales() {
		return accountsReceivableOverSales;
	}

	public void setAccountsReceivableOverSales(Item accountsReceivableOverSales) {
		this.accountsReceivableOverSales = accountsReceivableOverSales;
	}

	public Item getInventorySales() {
		return inventorySales;
	}

	public void setInventorySales(Item inventorySales) {
		this.inventorySales = inventorySales;
	}

	public Item getAccountsPayableSales() {
		return accountsPayableSales;
	}

	public void setAccountsPayableSales(Item accountsPayableSales) {
		this.accountsPayableSales = accountsPayableSales;
	}

	public Item getDaysReceivables() {
		return daysReceivables;
	}

	public void setDaysReceivables(Item daysReceivables) {
		this.daysReceivables = daysReceivables;
	}

	public Item getDaysInventory() {
		return daysInventory;
	}

	public void setDaysInventory(Item daysInventory) {
		this.daysInventory = daysInventory;
	}

	public Item getDaysPayables() {
		return daysPayables;
	}

	public void setDaysPayables(Item daysPayables) {
		this.daysPayables = daysPayables;
	}

	public Item getSalesOverWorkingCapital() {
		return salesOverWorkingCapital;
	}

	public void setSalesOverWorkingCapital(Item salesOverWorkingCapital) {
		this.salesOverWorkingCapital = salesOverWorkingCapital;
	}

	public Item getSalesOverNetLTAssets() {
		return salesOverNetLTAssets;
	}

	public void setSalesOverNetLTAssets(Item salesOverNetLTAssets) {
		this.salesOverNetLTAssets = salesOverNetLTAssets;
	}

	public Item getSalesOverNetAssets() {
		return salesOverNetAssets;
	}

	public void setSalesOverNetAssets(Item salesOverNetAssets) {
		this.salesOverNetAssets = salesOverNetAssets;
	}

	public Item getCashOverNetAssets() {
		return cashOverNetAssets;
	}

	public void setCashOverNetAssets(Item cashOverNetAssets) {
		this.cashOverNetAssets = cashOverNetAssets;
	}

	public Item getCashOverReceivables() {
		return cashOverReceivables;
	}

	public void setCashOverReceivables(Item cashOverReceivables) {
		this.cashOverReceivables = cashOverReceivables;
	}
	
	
	

}
