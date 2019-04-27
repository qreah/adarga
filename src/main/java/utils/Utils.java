package utils;

import java.util.logging.Logger;

import org.json.JSONObject;

import adarga.getinfo.Item;

public class Utils {
	
	private static final Logger log = Logger.getLogger(Utils.class.getName());
	
	public JSONObject emptyResults() {
		JSONObject json = new JSONObject();
		json.put("salesGrowth", "");
		json.put("NOPATGrowth", "");
		json.put("NOPATMargin", "");
		json.put("netWorkingCapitalOverRevenues", "");
		json.put("netLongTermAssetsOverRevenue", "");
		json.put("netDebtToCapitalRatio", "");
		json.put("afterTaxCostOfDebt", "");
		json.put("OperatingROA", "");
		json.put("salesOverNetAssets", "");
		json.put("ROE", "");
		json.put("returnOnTangibleEquity", "");
		json.put("growthIncome", "");
		json.put("growthOperatingIncome", "");
		json.put("g10Years", "");
		return json;
	}
	
	
	public Item controlNull(Item inputItem, int lastYear) {
		Item item = new Item();
		item.setZero(5, lastYear);
		log.info("years zero: " + item.toString());
		log.info("years zero last year: " + item.lastYear());
		if (inputItem != null) {
			item = inputItem;
		} else {
			item = item.multiplyNumber(0.0);
		}
		return item;
	}
}
