package adarga.external;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import adarga.external.FinantialRatios.Ratios;
import adarga.external.FinantialRatios.Series;
import adarga.getinfo.DB;
import adarga.utis.qreah;



public class FinantialRatios {
	private static final Logger log = Logger.getLogger(FinantialRatios.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/financial-ratios/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    private static HashMap<String, Ratios> years = new HashMap<String, Ratios>();
	

	public static void execute(String symbol) throws IOException {
		
		String urlEndpointComposer = urlEndpoint + symbol + "?datatype=json";
		Storage st = new Storage();
		
		HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		JSONObject j = new JSONObject(json);
		symbol = j.getString("symbol");
		JSONObject series = j.getJSONObject("financialRatios");
		Gson gson = new Gson();
		Iterator<String> iter = series.keys();
		while (iter.hasNext()) {
			String finDate = iter.next();
			String jsonInString = series.getJSONObject(finDate).toString();
			Ratios ratios = gson.fromJson(jsonInString, Ratios.class);
			years.put(st.finDateConversion(finDate), ratios);
		}
		
		
	}
	
	static public HashMap<String, Double> getRatioList(Ratios ratios) {
		HashMap<String, Double> list = new HashMap<String, Double>();
		
		list.put("debtRatio", ratios.getDebtRatios().getDebtRatio());
		list.put("longtermDebtToCapitalization", ratios.getDebtRatios().getlongtermDebtToCapitalization());
		list.put("interestCoverageRatio", ratios.getDebtRatios().getInterestCoverageRatio());
		list.put("cashFlowToDebtRatio", ratios.getDebtRatios().getCashFlowToDebtRatio());
		
		list.put("currentRatio", ratios.getLiquidityMeasurementRatios().getCurrentRatio());
		list.put("quickRatio", ratios.getLiquidityMeasurementRatios().getQuickRatio());
		list.put("cashRatio", ratios.getLiquidityMeasurementRatios().getCashRatio());
		list.put("daysofSalesOutstanding", ratios.getLiquidityMeasurementRatios().getDaysofSalesOutstanding());
		list.put("daysofInventoryOutstanding", ratios.getLiquidityMeasurementRatios().getDaysofInventoryOutstanding());
		list.put("operatingCycle", ratios.getLiquidityMeasurementRatios().getOperatingCycle());
		list.put("daysofPayablesOutstanding", ratios.getLiquidityMeasurementRatios().getDaysofPayablesOutstanding());
		list.put("cashConversionCycle", ratios.getLiquidityMeasurementRatios().getCashConversionCycle());
		
		list.put("grossProfitMargin", ratios.getProfitabilityIndicatorRatios().getGrossProfitMargin());
		list.put("operatingProfitMargin", ratios.getProfitabilityIndicatorRatios().getOperatingProfitMargin());
		list.put("netProfitMargin", ratios.getProfitabilityIndicatorRatios().getNetProfitMargin());
		list.put("returnOnEquity", ratios.getProfitabilityIndicatorRatios().getReturnOnEquity());
		list.put("returnOnAssets", ratios.getProfitabilityIndicatorRatios().getReturnOnAssets());
		list.put("returnOnCapitalEmployed", ratios.getProfitabilityIndicatorRatios().getReturnOnCapitalEmployed());
		list.put("pretaxProfitMargin", ratios.getProfitabilityIndicatorRatios().getPretaxProfitMargin());
		list.put("effectiveTaxRate", ratios.getProfitabilityIndicatorRatios().getEffectiveTaxRate());
		
		list.put("fixedAssetTurnover", ratios.getOperatingPerformanceRatios().getFixedAssetTurnover());
		list.put("assetTurnover", ratios.getOperatingPerformanceRatios().getAssetTurnover());
		
		list.put("operatingCashFlowSalesRatio", ratios.getCashFlowIndicatorRatios().getOperatingCashFlowSalesRatio());
		list.put("freeCashFlowOperatingCashFlowRatio", ratios.getCashFlowIndicatorRatios().getFreeCashFlowOperatingCashFlowRatio());
		list.put("cashFlowCoverageRatios", ratios.getCashFlowIndicatorRatios().getCashFlowCoverageRatios());
		list.put("shortTermCoverageRatios", ratios.getCashFlowIndicatorRatios().getShortTermCoverageRatios());
		list.put("capitalExpenditureCoverageRatios", ratios.getCashFlowIndicatorRatios().getCapitalExpenditureCoverageRatios());
		list.put("dividendpaidAndCapexCoverageRatios", ratios.getCashFlowIndicatorRatios().getDividendpaidAndCapexCoverageRatios());
		list.put("dividendPayoutRatio", ratios.getCashFlowIndicatorRatios().getDividendPayoutRatio());
		
		list.put("priceSalesRatio", ratios.getInvestmentValuationRatios().getPriceSalesRatio());
		list.put("priceCashFlowRatio", ratios.getInvestmentValuationRatios().getPriceCashFlowRatio());
		list.put("priceBookValueRatio", ratios.getInvestmentValuationRatios().getPriceBookValueRatio());
		list.put("priceEarningsRatio", ratios.getInvestmentValuationRatios().getPriceEarningsRatio());
		list.put("priceEarningsToGrowthRatio", ratios.getInvestmentValuationRatios().getPriceEarningsToGrowthRatio());
		list.put("dividendYield", ratios.getInvestmentValuationRatios().getDividendYield());
		list.put("enterpriseValueMultiple", ratios.getInvestmentValuationRatios().getEnterpriseValueMultiple());
		list.put("priceFairValue", ratios.getInvestmentValuationRatios().getPriceFairValue());
	
		return list;
	}
	
	public static void storeReport(HashMap<String, String> companyData) throws IOException, ClassNotFoundException, ServletException, SQLException {
		String symbol = companyData.get("symbol");
		execute(symbol);
		Storage st = new Storage();
		Set<String> keys = years.keySet();
		
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Ratios ratios = years.get(key);
			HashMap<String, Double> ratiosList = getRatioList(ratios);
			Set<String> keysSet = ratiosList.keySet();
			DB db = new DB();
			Iterator<String> keyRatio = keysSet.iterator();
			while (keyRatio.hasNext()) {
				String concept = keyRatio.next();
				Double ratio = ratiosList.get(concept);
				String SQL = st.storeRow(companyData, concept, ratio, key, category(concept));	
				log.info(SQL);
				db.addBatch(SQL);
			}
			db.executeBatch();
			db.close();
		}
	}
	
	

	
	public static String category(String ratio) {
		String result = "";
		switch(ratio) {
			case "priceFairValue":
				result = "Valuation";
				break;
			case "enterpriseValueMultiple":
				result = "Valuation";
				break;
			case "dividendYield":
				result = "Shareholder return";
				break;
			case "priceSalesRatio":
				result = "Valuation";
				break;
			case "priceEarningsToGrowthRatio":
				result = "Valuation";
				break;
			case "priceEarningsRatio":
				result = "Valuation";
				break;				
			case "priceCashFlowRatio":
				result = "Valuation";
				break;
			case "priceBookValueRatio":
				result = "Valuation";
				break;
			case "dividendPayoutRatio":
				result = "Shareholder return";
				break;
			case "dividendpaidAndCapexCoverageRatios":
				result = "Shareholder return";
				break;
			case "capitalExpenditureCoverageRatios":
				result = "Shareholder return";
				break;
			case "shortTermCoverageRatios":
				result = "Debt";
				break;				
			case "cashFlowCoverageRatios":
				result = "Shareholder return";
				break;
			case "freeCashFlowOperatingCashFlowRatio":
				result = "Shareholder return";
				break;
			case "operatingCashFlowSalesRatio":
				result = "Shareholder return";
				break;
			case "assetTurnover":
				result = "Profitability";
				break;
			case "fixedAssetTurnover":
				result = "Profitability";
				break;
			case "companyEquityMultiplier":
				result = "Valuation";
				break;
			case "cashFlowToDebtRatio":
				result = "Debt";
				break;
			case "interestCoverageRatio":
				result = "Debt";
				break;
			case "totalDebtToCapitalization":
				result = "Debt";
				break;							
			case "longtermDebtToCapitalization":
				result = "Debt";
				break;
			case "debtEquityRatio":
				result = "Debt";
				break;
			case "debtRatio":
				result = "Debt";
				break;
			case "eBITperRevenue":
				result = "Profitability";
				break;
			case "eBTperEBIT":
				result = "Profitability";
				break;
			case "nIperEBT":
				result = "Profitability";
				break;
			case "returnOnCapitalEmployed":
				result = "Shareholder return";
				break;				
			case "returnOnEquity":
				result = "Shareholder return";
				break;
			case "returnOnAssets":
				result = "Shareholder return";
				break;
			case "effectiveTaxRate":
				result = "Profitability";
				break;
			case "netProfitMargin":
				result = "Profitability";
				break;
			case "cashConversionCycle":
				result = "Cash Management";
				break;
			case "daysofPayablesOutstanding":
				result = "Cash Management";
				break;
			case "operatingCycle":
				result = "Cash Management";
				break;
			case "daysofInventoryOutstanding":
				result = "Cash Management";
				break;
			case "daysofSalesOutstanding":
				result = "Cash Management";
				break;				
			case "currentRatio":
				result = "Cash Management";
				break;
			case "quickRatio":
				result = "Cash Management";
				break;
			case "cashRatio":
				result = "Cash Management";
				break;

		}
		
		return result;
	}
	
	
	
	static public class FinRatio {
		@SerializedName("symbol") private String symbol;
		@SerializedName("financialRatios") private Series financialRatios;
		
		public String getSymbol() {
			return symbol;
		}
		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}
		public Series getFinancialRatios() {
			return financialRatios;
		}
		public void setFinancialRatios(Series financialRatios) {
			this.financialRatios = financialRatios;
		}
		
		
	}
	
	static public class Series {
		
		@Key private Ratios Year1;
		@Key private Ratios Year2;
		@Key private Ratios Year3;
		@Key private Ratios Year4;
		@Key private Ratios Year5;
		@Key private Ratios TTM;
		
		public Series() {
			
		}
		
		public Ratios getYear1() {
			return Year1;
		}
		public void setYear1(Ratios year1) {
			Year1 = year1;
		}
		public Ratios getYear2() {
			return Year2;
		}
		public void setYear2(Ratios year2) {
			Year2 = year2;
		}
		public Ratios getYear3() {
			return Year3;
		}
		public void setYear3(Ratios year3) {
			Year3 = year3;
		}
		public Ratios getYear4() {
			return Year4;
		}
		public void setYear4(Ratios year4) {
			Year4 = year4;
		}
		
		public Ratios getYear5() {
			return Year5;
		}
		public void setYear5(Ratios year5) {
			Year4 = year5;
		}
		
		public Ratios getTTM() {
			return TTM;
		}
		public void setTTM(Ratios tTM) {
			TTM = tTM;
		}
		
		
		
		
		
	}
	
	
	static public class Ratios {
		
		@Key private LiquidityMeasurementRatios liquidityMeasurementRatios;
		@Key private ProfitabilityIndicatorRatios profitabilityIndicatorRatios;
		@Key private DebtRatios debtRatios;
		@Key private OperatingPerformanceRatios operatingPerformanceRatios;
		@Key private CashFlowIndicatorRatios cashFlowIndicatorRatios;
		@Key private InvestmentValuationRatios investmentValuationRatios;
		
		
		
		
		
		@Override
		public String toString() {
			return "Ratios [liquidityMeasurementRatios=" + liquidityMeasurementRatios
					+ ", profitabilityIndicatorRatios=" + profitabilityIndicatorRatios + ", debtRatios=" + debtRatios
					+ ", operatingPerformanceRatios=" + operatingPerformanceRatios + ", cashFlowIndicatorRatios="
					+ cashFlowIndicatorRatios + ", investmentValuationRatios=" + investmentValuationRatios + "]";
		}
		
		public LiquidityMeasurementRatios getLiquidityMeasurementRatios() {
			return liquidityMeasurementRatios;
		}
		public void setLiquidityMeasurementRatios(LiquidityMeasurementRatios liquidityMeasurementRatios) {
			this.liquidityMeasurementRatios = liquidityMeasurementRatios;
		}
		public ProfitabilityIndicatorRatios getProfitabilityIndicatorRatios() {
			return profitabilityIndicatorRatios;
		}
		public void setProfitabilityIndicatorRatios(ProfitabilityIndicatorRatios profitabilityIndicatorRatios) {
			this.profitabilityIndicatorRatios = profitabilityIndicatorRatios;
		}
		public DebtRatios getDebtRatios() {
			return debtRatios;
		}
		public void setDebtRatios(DebtRatios debtRatios) {
			this.debtRatios = debtRatios;
		}
		public OperatingPerformanceRatios getOperatingPerformanceRatios() {
			return operatingPerformanceRatios;
		}
		public void setOperatingPerformanceRatios(OperatingPerformanceRatios operatingPerformanceRatios) {
			this.operatingPerformanceRatios = operatingPerformanceRatios;
		}
		public CashFlowIndicatorRatios getCashFlowIndicatorRatios() {
			return cashFlowIndicatorRatios;
		}
		public void setCashFlowIndicatorRatios(CashFlowIndicatorRatios cashFlowIndicatorRatios) {
			this.cashFlowIndicatorRatios = cashFlowIndicatorRatios;
		}
		public InvestmentValuationRatios getInvestmentValuationRatios() {
			return investmentValuationRatios;
		}
		public void setInvestmentValuationRatios(InvestmentValuationRatios investmentValuationRatios) {
			this.investmentValuationRatios = investmentValuationRatios;
		}
		
		
	}
	
	static public class LiquidityMeasurementRatios {
		
		@Key private Double currentRatio;
		@Key private Double quickRatio;
		@Key private Double cashRatio;
		@Key private Double daysofSalesOutstanding;
		@Key private Double daysofInventoryOutstanding;
		@Key private Double operatingCycle;
		@Key private Double daysofPayablesOutstanding;
		@Key private Double cashConversionCycle;
		
		
		
		public Double getCashConversionCycle() {
			return cashConversionCycle;
		}
		public void setCashConversionCycle(Double cashConversionCycle) {
			this.cashConversionCycle = cashConversionCycle;
		}
		public Double getCurrentRatio() {
			return currentRatio;
		}
		public void setCurrentRatio(Double currentRatio) {
			this.currentRatio = currentRatio;
		}
		public Double getQuickRatio() {
			return quickRatio;
		}
		public void setQuickRatio(Double quickRatio) {
			this.quickRatio = quickRatio;
		}
		public Double getCashRatio() {
			return cashRatio;
		}
		public void setCashRatio(Double cashRatio) {
			this.cashRatio = cashRatio;
		}
		public Double getDaysofSalesOutstanding() {
			return daysofSalesOutstanding;
		}
		public void setDaysofSalesOutstanding(Double daysofSalesOutstanding) {
			this.daysofSalesOutstanding = daysofSalesOutstanding;
		}
		public Double getDaysofInventoryOutstanding() {
			return daysofInventoryOutstanding;
		}
		public void setDaysofInventoryOutstanding(Double daysofInventoryOutstanding) {
			this.daysofInventoryOutstanding = daysofInventoryOutstanding;
		}
		public Double getOperatingCycle() {
			return operatingCycle;
		}
		public void setOperatingCycle(Double operatingCycle) {
			this.operatingCycle = operatingCycle;
		}
		public Double getDaysofPayablesOutstanding() {
			return daysofPayablesOutstanding;
		}
		public void setDaysofPayablesOutstanding(Double daysofPayablesOutstanding) {
			this.daysofPayablesOutstanding = daysofPayablesOutstanding;
		}
		
		
	}
	
	static public class ProfitabilityIndicatorRatios {
		
		@Key private Double grossProfitMargin;
		@Key private Double operatingProfitMargin;
		@Key private Double pretaxProfitMargin;
		@Key private Double netProfitMargin;
		@Key private Double effectiveTaxRate;
		@Key private Double returnOnAssets;
		@Key private Double returnOnEquity;
		@Key private Double returnOnCapitalEmployed;
		@Key private Double nIperEBT;
		@Key private Double eBTperEBIT;
		@Key private Double eBITperRevenue;
		
		public Double getGrossProfitMargin() {
			return grossProfitMargin;
		}
		public void setGrossProfitMargin(Double grossProfitMargin) {
			this.grossProfitMargin = grossProfitMargin;
		}
		public Double getOperatingProfitMargin() {
			return operatingProfitMargin;
		}
		public void setOperatingProfitMargin(Double operatingProfitMargin) {
			this.operatingProfitMargin = operatingProfitMargin;
		}
		public Double getPretaxProfitMargin() {
			return pretaxProfitMargin;
		}
		public void setPretaxProfitMargin(Double pretaxProfitMargin) {
			this.pretaxProfitMargin = pretaxProfitMargin;
		}
		public Double getNetProfitMargin() {
			return netProfitMargin;
		}
		public void setNetProfitMargin(Double netProfitMargin) {
			this.netProfitMargin = netProfitMargin;
		}
		public Double getEffectiveTaxRate() {
			return effectiveTaxRate;
		}
		public void setEffectiveTaxRate(Double effectiveTaxRate) {
			this.effectiveTaxRate = effectiveTaxRate;
		}
		public Double getReturnOnAssets() {
			return returnOnAssets;
		}
		public void setReturnOnAssets(Double returnOnAssets) {
			this.returnOnAssets = returnOnAssets;
		}
		public Double getReturnOnEquity() {
			return returnOnEquity;
		}
		public void setReturnOnEquity(Double returnOnEquity) {
			this.returnOnEquity = returnOnEquity;
		}
		public Double getReturnOnCapitalEmployed() {
			return returnOnCapitalEmployed;
		}
		public void setReturnOnCapitalEmployed(Double returnOnCapitalEmployed) {
			this.returnOnCapitalEmployed = returnOnCapitalEmployed;
		}
		public Double getnIperEBT() {
			return nIperEBT;
		}
		public void setnIperEBT(Double nIperEBT) {
			this.nIperEBT = nIperEBT;
		}
		public Double geteBTperEBIT() {
			return eBTperEBIT;
		}
		public void seteBTperEBIT(Double eBTperEBIT) {
			this.eBTperEBIT = eBTperEBIT;
		}
		public Double geteBITperRevenue() {
			return eBITperRevenue;
		}
		public void seteBITperRevenue(Double eBITperRevenue) {
			this.eBITperRevenue = eBITperRevenue;
		}
		
		
		
		
	}
	
	
	
	static public class DebtRatios {
		
		@Key private Double debtRatio;
		@Key private Double debtEquityRatio;
		@Key private Double longtermDebtToCapitalization;
		@Key private Double totalDebtToCapitalization;
		@Key private Double interestCoverageRatio;
		@Key private Double cashFlowToDebtRatio;
		@Key private Double companyEquityMultiplier;
		
		public Double getDebtRatio() {
			return debtRatio;
		}
		public void setDebtRatio(Double debtRatio) {
			this.debtRatio = debtRatio;
		}
		public Double getDebtEquityRatio() {
			return debtEquityRatio;
		}
		public void setDebtEquityRatio(Double debtEquityRatio) {
			this.debtEquityRatio = debtEquityRatio;
		}
		public Double getlongtermDebtToCapitalization() {
			return longtermDebtToCapitalization;
		}
		public void setlongtermDebtToCapitalization(Double longtermDebtToCapitalization) {
			this.longtermDebtToCapitalization = longtermDebtToCapitalization;
		}
		public Double getTotalDebtToCapitalization() {
			return totalDebtToCapitalization;
		}
		public void setTotalDebtToCapitalization(Double totalDebtToCapitalization) {
			this.totalDebtToCapitalization = totalDebtToCapitalization;
		}
		public Double getInterestCoverageRatio() {
			return interestCoverageRatio;
		}
		public void setInterestCoverageRatio(Double interestCoverageRatio) {
			this.interestCoverageRatio = interestCoverageRatio;
		}
		public Double getCashFlowToDebtRatio() {
			return cashFlowToDebtRatio;
		}
		public void setCashFlowToDebtRatio(Double cashFlowToDebtRatio) {
			this.cashFlowToDebtRatio = cashFlowToDebtRatio;
		}
		public Double getCompanyEquityMultiplier() {
			return companyEquityMultiplier;
		}
		public void setCompanyEquityMultiplier(Double companyEquityMultiplier) {
			this.companyEquityMultiplier = companyEquityMultiplier;
		}
		
		
		
	}
	
	
	
	static public class OperatingPerformanceRatios {
		
		@Key private Double fixedAssetTurnover;
		@Key private Double assetTurnover;
		
		public Double getFixedAssetTurnover() {
			return fixedAssetTurnover;
		}
		public void setFixedAssetTurnover(Double fixedAssetTurnover) {
			this.fixedAssetTurnover = fixedAssetTurnover;
		}
		public Double getAssetTurnover() {
			return assetTurnover;
		}
		public void setAssetTurnover(Double assetTurnover) {
			this.assetTurnover = assetTurnover;
		}
		
		
		
	}
	
	
	
	static public class CashFlowIndicatorRatios {
		
		@Key private Double operatingCashFlowSalesRatio;
		@Key private Double freeCashFlowOperatingCashFlowRatio;
		@Key private Double cashFlowCoverageRatios;
		@Key private Double shortTermCoverageRatios;
		@Key private Double capitalExpenditureCoverageRatios;
		@Key private Double dividendpaidAndCapexCoverageRatios;
		@Key private Double dividendPayoutRatio;
		
		public Double getOperatingCashFlowSalesRatio() {
			return operatingCashFlowSalesRatio;
		}
		public void setOperatingCashFlowSalesRatio(Double operatingCashFlowSalesRatio) {
			this.operatingCashFlowSalesRatio = operatingCashFlowSalesRatio;
		}
		public Double getFreeCashFlowOperatingCashFlowRatio() {
			return freeCashFlowOperatingCashFlowRatio;
		}
		public void setFreeCashFlowOperatingCashFlowRatio(Double freeCashFlowOperatingCashFlowRatio) {
			this.freeCashFlowOperatingCashFlowRatio = freeCashFlowOperatingCashFlowRatio;
		}
		public Double getCashFlowCoverageRatios() {
			return cashFlowCoverageRatios;
		}
		public void setCashFlowCoverageRatios(Double cashFlowCoverageRatios) {
			this.cashFlowCoverageRatios = cashFlowCoverageRatios;
		}
		public Double getShortTermCoverageRatios() {
			return shortTermCoverageRatios;
		}
		public void setShortTermCoverageRatios(Double shortTermCoverageRatios) {
			this.shortTermCoverageRatios = shortTermCoverageRatios;
		}
		public Double getCapitalExpenditureCoverageRatios() {
			return capitalExpenditureCoverageRatios;
		}
		public void setCapitalExpenditureCoverageRatios(Double capitalExpenditureCoverageRatios) {
			this.capitalExpenditureCoverageRatios = capitalExpenditureCoverageRatios;
		}
		public Double getDividendpaidAndCapexCoverageRatios() {
			return dividendpaidAndCapexCoverageRatios;
		}
		public void setDividendpaidAndCapexCoverageRatios(Double dividendpaidAndCapexCoverageRatios) {
			this.dividendpaidAndCapexCoverageRatios = dividendpaidAndCapexCoverageRatios;
		}
		public Double getDividendPayoutRatio() {
			return dividendPayoutRatio;
		}
		public void setDividendPayoutRatio(Double dividendPayoutRatio) {
			this.dividendPayoutRatio = dividendPayoutRatio;
		}
		
		
		
		
	}
	
	
	
	static public class InvestmentValuationRatios {
		
		@Key private Double priceBookValueRatio;
		@Key private Double priceCashFlowRatio;
		@Key private Double priceEarningsRatio;
		@Key private Double priceEarningsToGrowthRatio;
		@Key private Double priceSalesRatio;
		@Key private Double dividendYield;
		@Key private Double enterpriseValueMultiple;
		@Key private Double priceFairValue;
		
		public Double getPriceBookValueRatio() {
			return priceBookValueRatio;
		}
		public void setPriceBookValueRatio(Double priceBookValueRatio) {
			this.priceBookValueRatio = priceBookValueRatio;
		}
		public Double getPriceCashFlowRatio() {
			return priceCashFlowRatio;
		}
		public void setPriceCashFlowRatio(Double priceCashFlowRatio) {
			this.priceCashFlowRatio = priceCashFlowRatio;
		}
		public Double getPriceEarningsRatio() {
			return priceEarningsRatio;
		}
		public void setPriceEarningsRatio(Double priceEarningsRatio) {
			this.priceEarningsRatio = priceEarningsRatio;
		}
		public Double getPriceEarningsToGrowthRatio() {
			return priceEarningsToGrowthRatio;
		}
		public void setPriceEarningsToGrowthRatio(Double priceEarningsToGrowthRatio) {
			this.priceEarningsToGrowthRatio = priceEarningsToGrowthRatio;
		}
		public Double getPriceSalesRatio() {
			return priceSalesRatio;
		}
		public void setPriceSalesRatio(Double priceSalesRatio) {
			this.priceSalesRatio = priceSalesRatio;
		}
		public Double getDividendYield() {
			return dividendYield;
		}
		public void setDividendYield(Double dividendYield) {
			this.dividendYield = dividendYield;
		}
		public Double getEnterpriseValueMultiple() {
			return enterpriseValueMultiple;
		}
		public void setEnterpriseValueMultiple(Double enterpriseValueMultiple) {
			this.enterpriseValueMultiple = enterpriseValueMultiple;
		}
		public Double getPriceFairValue() {
			return priceFairValue;
		}
		public void setPriceFairValue(Double priceFairValue) {
			this.priceFairValue = priceFairValue;
		}
		
		
		
	}
	
	


	public static void main(String[] args) throws IOException, ClassNotFoundException, ServletException, SQLException {
		//storeReport("MMM");
		
		

	}

}
