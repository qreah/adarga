package adarga.ratios;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.IncomeStatement;
import adarga.getinfo.Item;

public class InvestmentManagement {
	
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
	
	public InvestmentManagement(BalanceSheet bs, IncomeStatement is, CashFlowStatement cs) {
		Item revenue = is.get("Revenue");
		Item COGS = is.get("Cost of revenue");
		accountsReceivable = bs.get("Receivables");
		accountsReceivableGrowth = accountsReceivable.changeInItem();
		inventory = bs.get("Inventories");
		inventoryGrowth = inventory.changeInItem();
		accountsPayable = bs.get("Accounts payable");
		currentAssets = bs.get("Total current assets");
		currentLiabilities = bs.get("Total current liabilities");
		cashAndMarketableSecurities = bs.get("Total cash");
		shortTermDebtAndCurrentPortionOfLongTermDebt = bs.get("Short-term debt");
		netWorkingCapital = currentAssets.substract(currentLiabilities);
		netWorkingCapital = netWorkingCapital.substract(cashAndMarketableSecurities);
		netWorkingCapital = netWorkingCapital.sum(shortTermDebtAndCurrentPortionOfLongTermDebt);
		LTAssets = bs.get("Total assets").substract(currentAssets);
		goodwillAndIntangibles = bs.get("Goodwill").sum(bs.get("Intangible assets"));
		longTermDebt = bs.get("Long-term debt").sum(bs.get("Capital leases"));
		nonInterestBearingLTLiabilities = bs.get("Total assets").substract(longTermDebt);
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
		json.put("accountsReceivable", accountsReceivable.toString());
		json.put("accountsReceivableGrowth", accountsReceivableGrowth.toString());
		json.put("inventory", inventory.toString());
		json.put("inventoryGrowth", inventoryGrowth.toString());
		json.put("accountsPayable", accountsPayable.toString());
		json.put("currentAssets", currentAssets.toString());
		json.put("currentLiabilities", currentLiabilities.toString());
		json.put("cashAndMarketableSecurities", cashAndMarketableSecurities.toString());
		json.put("shortTermDebtAndCurrentPortionOfLongTermDebt", shortTermDebtAndCurrentPortionOfLongTermDebt.toString());
		json.put("netWorkingCapital", netWorkingCapital.toString());
		json.put("LTAssets", LTAssets.toString());
		json.put("goodwillAndIntangibles", goodwillAndIntangibles.toString());
		json.put("nonInterestBearingLTLiabilities", nonInterestBearingLTLiabilities.toString());
		json.put("netLTAssets", netLTAssets.toString());
		json.put("netAssets", netAssets.toString());
		json.put("longTermDebt", longTermDebt.toString());
		json.put("accountsReceivableOverSales", accountsReceivableOverSales.toString());
		json.put("inventorySales", inventorySales.toString());
		json.put("accountsPayableSales", accountsPayableSales.toString());
		json.put("daysReceivables", daysReceivables.toString());
		json.put("daysInventory", daysInventory.toString());
		json.put("daysPayables", daysPayables.toString());
		json.put("salesOverWorkingCapital", salesOverWorkingCapital.toString());
		json.put("salesOverNetLTAssets", salesOverNetLTAssets.toString());
		json.put("salesOverNetAssets", salesOverNetAssets.toString());
		json.put("cashOverNetAssets", cashOverNetAssets.toString());
		json.put("cashOverReceivables", cashOverReceivables.toString());
		
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
