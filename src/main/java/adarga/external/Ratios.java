package adarga.external;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.DB;
import adarga.utils.qreah;

public class Ratios {
	
	private static final Logger log = Logger.getLogger(CompanyProfile.class.getName());
	private static String symbol;
	private static String APIKey = "9b587440587b5542b0567b8ac89092d2";
	
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    
    
    public void store(String symbol) throws IOException, ClassNotFoundException, SQLException, ServletException {
    	Profile pr = getProfile(symbol);
    	FinRatios fr = getFinantialRatios(symbol);
    	KeyMetrics km = getKeyMetrics(symbol);
        GrowthMetrics gm = getGrowthMetrics(symbol);
        String SQL = "";
        boolean existInDB = exists(symbol);
        qreah q = new qreah();
        String date = q.today();
        if (existInDB) {
			SQL = "UPDATE apiadbossDB.adargaRatios" + 
					" SET "
					+ "date='" + date 
					+ "symbol='" + symbol 
					+ "', price = '" + pr.getPrice() 
					+ "', mktCap= '" + pr.getMktCap() 
					+ "', companyName ='" + pr.getCompanyName() 
					+ "', industry ='" + pr.getIndustry() 
					+ "', sector ='" + pr.getSector() 
					+ "', description ='" + pr.getDescription() 
					
					+ "', priceBookValueRatio ='" + fr.getPriceBookValueRatio() 
					+ "', priceToSalesRatio ='" + fr.getPriceToSalesRatio() 
					+ "', priceEarningsRatio ='" + fr.getPriceEarningsRatio()
					+ "', priceToFreeCashFlowsRatio ='" + fr.getPriceToFreeCashFlowsRatio()
					+ "', dividendYield ='" + fr.getDividendYield()
					+ "', grossProfitMargin ='" + fr.getGrossProfitMargin()
					+ "', operatingProfitMargin ='" + fr.getOperatingProfitMargin()
					+ "', netProfitMargin ='" + fr.getNetProfitMargin()
					+ "', returnOnAssets ='" + fr.getReturnOnAssets()
					+ "', returnOnEquity ='" + fr.getReturnOnEquity()
					+ "', returnOnCapitalEmployed ='" + fr.getReturnOnCapitalEmployed()
					+ "', interestCoverage ='" + fr.getInterestCoverage()
					+ "', longtermDebtToCapitalization ='" + fr.getLongtermDebtToCapitalization()
					+ "', totalDebtToCapitalization ='" + fr.getTotalDebtToCapitalization()
					+ "', currentRatio ='" + fr.getCurrentRatio()
					+ "', quickRatio ='" + fr.getQuickRatio()
					+ "', cashRatio ='" + fr.getCashRatio()
					+ "', daysOfSalesOutstanding ='" + fr.getDaysOfSalesOutstanding()
					+ "', daysOfInventoryOutstanding ='" + fr.getDaysOfInventoryOutstanding()
					+ "', operatingCycle ='" + fr.getOperatingCycle()
					+ "', daysOfPayablesOutstanding ='" + fr.getDaysOfPayablesOutstanding()
					+ "', cashConversionCycle ='" + fr.getCashConversionCycle()
					
					+ "', revenuePerShare ='" + km.getRevenuePerShare()
					+ "', OCFperShare ='" + km.getOCFperShare()
					+ "', FCFperShare ='" + km.getFCFperShare()
					+ "', CashperShare ='" + km.getCashperShare()
					+ "', EarningsYield ='" + km.getEarningsYield()
					+ "', FCFYield ='" + km.getFCFYield()
					+ "', DebttoAssets ='" + km.getDebttoAssets()
					+ "', DividendYield ='" + km.getDividendYield()
					+ "', SGAtoRevenue ='" + km.getSGAtoRevenue()
					+ "', RDtoRevenue ='" + km.getRDtoRevenue()
					+ "', IntangiblestoTotalAssets ='" + km.getIntangiblestoTotalAssets()
					+ "', CapextoOperatingCashFlow ='" + km.getCapextoOperatingCashFlow()
					+ "', StockBasedCompensationtoRevenue ='" + km.getStockBasedCompensationtoRevenue()
					+ "', GrahamNumber ='" + km.getGrahamNumber()
					+ "', ROIC ='" + km.getROIC()
					+ "', ReturnOnTangibleAssets ='" + km.getReturnOnTangibleAssets()
					+ "', GrahamNetNet ='" + km.getGrahamNetNet()
					+ "', CapexPerShare ='" + km.getCapexPerShare()
					
					+ "', RGperShare10Y ='" + gm.getRGperShare10Y()
					+ "', RGperShare5Y ='" + gm.getRGperShare5Y()
					+ "', RGperShare3Y ='" + gm.getRGperShare3Y()
					
					+ "', OCFGperShare10Y ='" + gm.getOCFGperShare10Y()
					+ "', OCFGperShare5Y ='" + gm.getOCFGperShare5Y()
					+ "', OCFGperShare3Y ='" + gm.getOCFGperShare3Y()
					
					+ "', DivperShare10Y ='" + gm.getDivperShare10Y()
					+ "', DivperShare5Y ='" + gm.getDivperShare5Y()
					+ "', DivperShare3Y ='" + gm.getDivperShare3Y()
			    	
					+ "'"
					+ " WHERE "
					+ "symbol='" + symbol
					+ "';";
		} else {
			log.info(fr.getPriceBookValueRatio());
			SQL = "INSERT INTO apiadbossDB.adargaRatios" + 
					" VALUES ('" 
					+ date + "', '" 
					+ symbol + "', '" 
					+ pr.getPrice()  + "', '"
					+ pr.getMktCap()  + "', '"
					+ pr.getCompanyName()  + "', '"
					+ pr.getIndustry()  + "', '"
					+ pr.getSector()  + "', '"
					+ pr.getDescription() 
					
					+ fr.getPriceBookValueRatio()  + "', '"
					+ fr.getPriceToSalesRatio()  + "', '"
					+ fr.getPriceEarningsRatio() + "', '"
					+ fr.getPriceToFreeCashFlowsRatio() + "', '"
					+ fr.getDividendYield() + "', '"
					+ fr.getGrossProfitMargin() + "', '"
					+ fr.getOperatingProfitMargin() + "', '"
					+ fr.getNetProfitMargin() + "', '"
					+ fr.getReturnOnAssets() + "', '"
					+ fr.getReturnOnEquity() + "', '"
					+ fr.getReturnOnCapitalEmployed() + "', '"
					+ fr.getInterestCoverage() + "', '"
					+ fr.getLongtermDebtToCapitalization() + "', '"
					+ fr.getTotalDebtToCapitalization() + "', '"
					+ fr.getCurrentRatio() + "', '"
					+ fr.getQuickRatio() + "', '"
					+ fr.getCashRatio() + "', '"
					+ fr.getDaysOfSalesOutstanding() + "', '"
					+ fr.getDaysOfInventoryOutstanding() + "', '"
					+ fr.getOperatingCycle() + "', '"
					+ fr.getDaysOfPayablesOutstanding() + "', '"
					+ fr.getCashConversionCycle() + "', '"
					
					+ km.getRevenuePerShare() + "', '"
					+ km.getOCFperShare() + "', '"
					+ km.getFCFperShare() + "', '"
					+ km.getCashperShare() + "', '"
					+ km.getEarningsYield() + "', '"
					+ km.getFCFYield() + "', '"
					+ km.getDebttoAssets() + "', '"
					+ km.getDividendYield() + "', '"
					+ km.getSGAtoRevenue() + "', '"
					+ km.getRDtoRevenue() + "', '"
					+ km.getIntangiblestoTotalAssets() + "', '"
					+ km.getCapextoOperatingCashFlow() + "', '"
					+ km.getStockBasedCompensationtoRevenue() + "', '"
					+ km.getGrahamNumber() + "', '"
					+ km.getROIC() + "', '"
					+ km.getReturnOnTangibleAssets() + "', '"
					+ km.getGrahamNetNet() + "', '"
					+ km.getCapexPerShare() + "', '"
					
					+ gm.getRGperShare10Y() + "', '"
					+ gm.getRGperShare5Y() + "', '"
					+ gm.getRGperShare3Y() + "', '"
					
					+ gm.getOCFGperShare10Y() + "', '"
					+ gm.getOCFGperShare5Y() + "', '"
					+ gm.getOCFGperShare3Y() + "', '"
					
					+ gm.getDivperShare10Y() + "', '"
					+ gm.getDivperShare5Y() + "', '"
					+ gm.getDivperShare3Y() + "', '"
					+ "');";
		}
        log.info(SQL);
        new DB().Execute(SQL);
    }
    
    static public boolean exists(String symbol) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		DB db = new DB();
		String exists = "SELECT symbol FROM apiadbossDB.adargaRatios "
				+ "where symbol = '" + symbol + "'";
		ResultSet exist = db.ExecuteSELECT(exists);
		
		while (exist.next()) {
			String symbolStr = exist.getString("symbol");
			if (symbolStr.equals(symbol)) {
				result = true;
			}		
		}
		exist.close();
		return result;
	}
    
    public Profile getProfile(String symbol) throws IOException {
    	String urlEndpoint = "https://financialmodelingprep.com/api/v3/company/profile/";
    	String urlEndpointComposer = urlEndpoint + symbol + "?datatype=json" + "&apikey=" + APIKey;
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		log.info(json);
		JSONObject jsonO = new JSONObject(json);
		JSONObject prof = jsonO.getJSONObject("profile");
		Gson gson = new Gson();
		Profile profile = gson.fromJson(prof.toString(), Profile.class);
		return profile;
    }
    
    public FinRatios getFinantialRatios(String symbol) throws IOException {
    	String urlEndpoint = "https://financialmodelingprep.com/api/v3/financial-ratios/";
    	String urlEndpointComposer = urlEndpoint + symbol + "?datatype=json" + "&apikey=" + APIKey;
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		log.info(json);
		FinRatios FR = new FinRatios();
		JSONObject jsonO = new JSONObject(json);
		JSONObject ratios = (JSONObject) jsonO.getJSONArray("ratios").get(0);
		JSONObject investmentValuationRatios = ratios.getJSONObject("investmentValuationRatios");
			
			FR.setDividendYield(investmentValuationRatios.getString("dividendYield"));
			FR.setPriceBookValueRatio(investmentValuationRatios.getString("priceBookValueRatio"));
			log.info(investmentValuationRatios.getString("priceBookValueRatio"));
			FR.setPriceEarningsRatio(investmentValuationRatios.getString("priceEarningsRatio"));
			FR.setPriceToFreeCashFlowsRatio(investmentValuationRatios.getString("priceToFreeCashFlowsRatio"));
			FR.setPriceToSalesRatio(investmentValuationRatios.getString("priceToSalesRatio"));
			
			
		JSONObject profitabilityIndicatorRatios = ratios.getJSONObject("profitabilityIndicatorRatios");
			FR.setGrossProfitMargin(profitabilityIndicatorRatios.getString("grossProfitMargin"));
			FR.setNetProfitMargin(profitabilityIndicatorRatios.getString("netProfitMargin"));
			FR.setOperatingProfitMargin(profitabilityIndicatorRatios.getString("operatingProfitMargin"));
			FR.setReturnOnAssets(profitabilityIndicatorRatios.getString("returnOnAssets"));
			FR.setReturnOnCapitalEmployed(profitabilityIndicatorRatios.getString("returnOnCapitalEmployed"));
			FR.setReturnOnEquity(profitabilityIndicatorRatios.getString("returnOnEquity"));
		
		
		JSONObject liquidityMeasurementRatios = ratios.getJSONObject("liquidityMeasurementRatios");
			FR.setCashConversionCycle(liquidityMeasurementRatios.getString("cashConversionCycle"));
			FR.setCashRatio(liquidityMeasurementRatios.getString("cashRatio"));
			FR.setCurrentRatio(liquidityMeasurementRatios.getString("currentRatio"));
			FR.setDaysOfInventoryOutstanding(liquidityMeasurementRatios.getString("daysOfInventoryOutstanding"));
			FR.setDaysOfPayablesOutstanding(liquidityMeasurementRatios.getString("daysOfPayablesOutstanding"));
			FR.setDaysOfSalesOutstanding(liquidityMeasurementRatios.getString("daysOfSalesOutstanding"));
			FR.setOperatingCycle(liquidityMeasurementRatios.getString("operatingCycle"));
			FR.setQuickRatio(liquidityMeasurementRatios.getString("quickRatio"));
		
		JSONObject debtRatios = ratios.getJSONObject("debtRatios");
			FR.setInterestCoverage(debtRatios.getString("interestCoverage"));
			FR.setLongtermDebtToCapitalization(debtRatios.getString("longtermDebtToCapitalization"));
			FR.setTotalDebtToCapitalization(debtRatios.getString("totalDebtToCapitalization"));
			
		return FR;
    	
    }
    
    static public class FinRatios {
		
    	private String priceBookValueRatio;
    	private String priceToSalesRatio;
    	private String priceEarningsRatio;
    	private String priceToFreeCashFlowsRatio;
    	private String dividendYield;
    	private String grossProfitMargin;
    	private String operatingProfitMargin;
    	private String netProfitMargin;
    	
    	private String returnOnAssets;
    	private String returnOnEquity;
    	private String returnOnCapitalEmployed;
    	private String interestCoverage;
    	private String longtermDebtToCapitalization;
    	private String totalDebtToCapitalization;
    	private String currentRatio;
    	private String quickRatio;
    	
    	private String cashRatio;
    	private String daysOfSalesOutstanding;
    	private String daysOfInventoryOutstanding;
    	private String operatingCycle;
    	private String daysOfPayablesOutstanding;
    	private String cashConversionCycle;
    	
    	
    	
		public String getPriceBookValueRatio() {
			return priceBookValueRatio;
		}
		public void setPriceBookValueRatio(String priceBookValueRatio) {
			this.priceBookValueRatio = priceBookValueRatio;
		}
		public String getPriceToSalesRatio() {
			return priceToSalesRatio;
		}
		public void setPriceToSalesRatio(String priceToSalesRatio) {
			this.priceToSalesRatio = priceToSalesRatio;
		}
		public String getPriceEarningsRatio() {
			return priceEarningsRatio;
		}
		public void setPriceEarningsRatio(String priceEarningsRatio) {
			this.priceEarningsRatio = priceEarningsRatio;
		}
		public String getPriceToFreeCashFlowsRatio() {
			return priceToFreeCashFlowsRatio;
		}
		public void setPriceToFreeCashFlowsRatio(String priceToFreeCashFlowsRatio) {
			this.priceToFreeCashFlowsRatio = priceToFreeCashFlowsRatio;
		}
		public String getDividendYield() {
			return dividendYield;
		}
		public void setDividendYield(String dividendYield) {
			this.dividendYield = dividendYield;
		}
		public String getGrossProfitMargin() {
			return grossProfitMargin;
		}
		public void setGrossProfitMargin(String grossProfitMargin) {
			this.grossProfitMargin = grossProfitMargin;
		}
		public String getOperatingProfitMargin() {
			return operatingProfitMargin;
		}
		public void setOperatingProfitMargin(String operatingProfitMargin) {
			this.operatingProfitMargin = operatingProfitMargin;
		}
		public String getNetProfitMargin() {
			return netProfitMargin;
		}
		public void setNetProfitMargin(String netProfitMargin) {
			this.netProfitMargin = netProfitMargin;
		}
		public String getReturnOnAssets() {
			return returnOnAssets;
		}
		public void setReturnOnAssets(String returnOnAssets) {
			this.returnOnAssets = returnOnAssets;
		}
		public String getReturnOnEquity() {
			return returnOnEquity;
		}
		public void setReturnOnEquity(String returnOnEquity) {
			this.returnOnEquity = returnOnEquity;
		}
		public String getReturnOnCapitalEmployed() {
			return returnOnCapitalEmployed;
		}
		public void setReturnOnCapitalEmployed(String returnOnCapitalEmployed) {
			this.returnOnCapitalEmployed = returnOnCapitalEmployed;
		}
		public String getInterestCoverage() {
			return interestCoverage;
		}
		public void setInterestCoverage(String interestCoverage) {
			this.interestCoverage = interestCoverage;
		}
		public String getLongtermDebtToCapitalization() {
			return longtermDebtToCapitalization;
		}
		public void setLongtermDebtToCapitalization(String longtermDebtToCapitalization) {
			this.longtermDebtToCapitalization = longtermDebtToCapitalization;
		}
		public String getTotalDebtToCapitalization() {
			return totalDebtToCapitalization;
		}
		public void setTotalDebtToCapitalization(String totalDebtToCapitalization) {
			this.totalDebtToCapitalization = totalDebtToCapitalization;
		}
		public String getCurrentRatio() {
			return currentRatio;
		}
		public void setCurrentRatio(String currentRatio) {
			this.currentRatio = currentRatio;
		}
		public String getQuickRatio() {
			return quickRatio;
		}
		public void setQuickRatio(String quickRatio) {
			this.quickRatio = quickRatio;
		}
		public String getCashRatio() {
			return cashRatio;
		}
		public void setCashRatio(String cashRatio) {
			this.cashRatio = cashRatio;
		}
		public String getDaysOfSalesOutstanding() {
			return daysOfSalesOutstanding;
		}
		public void setDaysOfSalesOutstanding(String daysOfSalesOutstanding) {
			this.daysOfSalesOutstanding = daysOfSalesOutstanding;
		}
		public String getDaysOfInventoryOutstanding() {
			return daysOfInventoryOutstanding;
		}
		public void setDaysOfInventoryOutstanding(String daysOfInventoryOutstanding) {
			this.daysOfInventoryOutstanding = daysOfInventoryOutstanding;
		}
		public String getOperatingCycle() {
			return operatingCycle;
		}
		public void setOperatingCycle(String operatingCycle) {
			this.operatingCycle = operatingCycle;
		}
		public String getDaysOfPayablesOutstanding() {
			return daysOfPayablesOutstanding;
		}
		public void setDaysOfPayablesOutstanding(String daysOfPayablesOutstanding) {
			this.daysOfPayablesOutstanding = daysOfPayablesOutstanding;
		}
		public String getCashConversionCycle() {
			return cashConversionCycle;
		}
		public void setCashConversionCycle(String cashConversionCycle) {
			this.cashConversionCycle = cashConversionCycle;
		}


    	
    	
    }
    
    public KeyMetrics getKeyMetrics(String symbol) throws IOException {
    	String urlEndpoint = "https://financialmodelingprep.com/api/v3/company-key-metrics/";
    	String urlEndpointComposer = urlEndpoint + symbol + "?datatype=json" + "&apikey=" + APIKey;
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		
		
		KeyMetrics mt = new KeyMetrics();
		JSONObject jsonO = new JSONObject(json);
		JSONObject ratios = (JSONObject) jsonO.getJSONArray("metrics").get(0);
		mt.setCashperShare(ratios.getString("Cash per Share"));
		mt.setDebttoAssets(ratios.getString("Debt to Assets"));
		mt.setDividendYield(ratios.getString("Dividend Yield"));
		mt.setEarningsYield(ratios.getString("Earnings Yield"));
		mt.setFCFperShare(ratios.getString("Free Cash Flow per Share"));
		mt.setFCFYield(ratios.getString("Free Cash Flow Yield"));
		mt.setOCFperShare(ratios.getString("Operating Cash Flow per Share"));
		mt.setRevenuePerShare(ratios.getString("Revenue per Share"));
		mt.setSGAtoRevenue(ratios.getString("SG&A to Revenue"));
		
		mt.setRDtoRevenue(ratios.getString("R&D to Revenue"));
		mt.setIntangiblestoTotalAssets(ratios.getString("Intangibles to Total Assets"));
		mt.setCapextoOperatingCashFlow(ratios.getString("Capex to Operating Cash Flow"));
		mt.setStockBasedCompensationtoRevenue(ratios.getString("Stock-based compensation to Revenue"));
		mt.setGrahamNumber(ratios.getString("Graham Number"));
		mt.setGrahamNetNet(ratios.getString("Graham Net-Net"));
		mt.setROIC(ratios.getString("ROIC"));
		mt.setReturnOnTangibleAssets(ratios.getString("Return on Tangible Assets"));
		
		return mt;
    	
    }
    
    static public class KeyMetrics {
		
    	private String revenuePerShare;
    	private String OCFperShare;
    	private String FCFperShare;
    	private String CashperShare;
    	private String EarningsYield;
    	private String FCFYield;
    	private String DebttoAssets;
    	private String DividendYield;
    	private String SGAtoRevenue;
    	
    	private String RDtoRevenue;
    	private String IntangiblestoTotalAssets;
    	private String CapextoOperatingCashFlow;
    	private String StockBasedCompensationtoRevenue;
    	private String GrahamNumber;
    	private String ROIC;
    	private String ReturnOnTangibleAssets;
    	private String GrahamNetNet;
    	private String CapexPerShare;
    	
    	
    	
    	
		public String getRDtoRevenue() {
			return RDtoRevenue;
		}
		public void setRDtoRevenue(String rDtoRevenue) {
			RDtoRevenue = rDtoRevenue;
		}
		public String getIntangiblestoTotalAssets() {
			return IntangiblestoTotalAssets;
		}
		public void setIntangiblestoTotalAssets(String intangiblestoTotalAssets) {
			IntangiblestoTotalAssets = intangiblestoTotalAssets;
		}
		public String getCapextoOperatingCashFlow() {
			return CapextoOperatingCashFlow;
		}
		public void setCapextoOperatingCashFlow(String capextoOperatingCashFlow) {
			CapextoOperatingCashFlow = capextoOperatingCashFlow;
		}
		public String getStockBasedCompensationtoRevenue() {
			return StockBasedCompensationtoRevenue;
		}
		public void setStockBasedCompensationtoRevenue(String stockBasedCompensationtoRevenue) {
			StockBasedCompensationtoRevenue = stockBasedCompensationtoRevenue;
		}
		public String getGrahamNumber() {
			return GrahamNumber;
		}
		public void setGrahamNumber(String grahamNumber) {
			GrahamNumber = grahamNumber;
		}
		public String getROIC() {
			return ROIC;
		}
		public void setROIC(String rOIC) {
			ROIC = rOIC;
		}
		public String getReturnOnTangibleAssets() {
			return ReturnOnTangibleAssets;
		}
		public void setReturnOnTangibleAssets(String returnOnTangibleAssets) {
			ReturnOnTangibleAssets = returnOnTangibleAssets;
		}
		public String getGrahamNetNet() {
			return GrahamNetNet;
		}
		public void setGrahamNetNet(String grahamNetNet) {
			GrahamNetNet = grahamNetNet;
		}
		public String getCapexPerShare() {
			return CapexPerShare;
		}
		public void setCapexPerShare(String capexPerShare) {
			CapexPerShare = capexPerShare;
		}
		public String getRevenuePerShare() {
			return revenuePerShare;
		}
		public void setRevenuePerShare(String revenuePerShare) {
			this.revenuePerShare = revenuePerShare;
		}
		public String getOCFperShare() {
			return OCFperShare;
		}
		public void setOCFperShare(String oCFperShare) {
			OCFperShare = oCFperShare;
		}
		public String getFCFperShare() {
			return FCFperShare;
		}
		public void setFCFperShare(String fCFperShare) {
			FCFperShare = fCFperShare;
		}
		public String getCashperShare() {
			return CashperShare;
		}
		public void setCashperShare(String cashperShare) {
			CashperShare = cashperShare;
		}
		public String getEarningsYield() {
			return EarningsYield;
		}
		public void setEarningsYield(String earningsYield) {
			EarningsYield = earningsYield;
		}
		public String getFCFYield() {
			return FCFYield;
		}
		public void setFCFYield(String fCFYield) {
			FCFYield = fCFYield;
		}
		public String getDebttoAssets() {
			return DebttoAssets;
		}
		public void setDebttoAssets(String debttoAssets) {
			DebttoAssets = debttoAssets;
		}
		public String getDividendYield() {
			return DividendYield;
		}
		public void setDividendYield(String dividendYield) {
			DividendYield = dividendYield;
		}
		public String getSGAtoRevenue() {
			return SGAtoRevenue;
		}
		public void setSGAtoRevenue(String sGAtoRevenue) {
			SGAtoRevenue = sGAtoRevenue;
		}
    	
    
    }
    
    
    
    public GrowthMetrics getGrowthMetrics(String symbol) throws IOException {
    	String urlEndpoint = "https://financialmodelingprep.com/api/v3/financial-statement-growth/";
    	String urlEndpointComposer = urlEndpoint + symbol + "?datatype=json" + "&apikey=" + APIKey;
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
    	GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		String json = res.parseAsString();
		
		GrowthMetrics mt = new GrowthMetrics();
		JSONObject jsonO = new JSONObject(json);
		JSONObject ratios = (JSONObject) jsonO.getJSONArray("growth").get(0);
		mt.setDivperShare10Y(ratios.getString("10Y Dividend per Share Growth (per Share)"));
		mt.setDivperShare3Y(ratios.getString("5Y Dividend per Share Growth (per Share)"));
		mt.setDivperShare5Y(ratios.getString("3Y Dividend per Share Growth (per Share)"));
		mt.setOCFGperShare10Y(ratios.getString("10Y Operating CF Growth (per Share)"));
		mt.setOCFGperShare3Y(ratios.getString("5Y Operating CF Growth (per Share)"));
		mt.setOCFGperShare5Y(ratios.getString("3Y Operating CF Growth (per Share)"));
		mt.setRGperShare10Y(ratios.getString("10Y Revenue Growth (per Share)"));
		mt.setRGperShare3Y(ratios.getString("5Y Revenue Growth (per Share)"));
		mt.setRGperShare5Y(ratios.getString("3Y Revenue Growth (per Share)"));
		
		return mt;
    	
    }
    
    
    static public class GrowthMetrics {
		
    	private String RGperShare10Y;
    	private String RGperShare5Y;
    	private String RGperShare3Y;
    	
    	private String OCFGperShare10Y;
    	private String OCFGperShare5Y;
    	private String OCFGperShare3Y;
    	
    	private String DivperShare10Y;
    	private String DivperShare5Y;
    	private String DivperShare3Y;
    	
    	
		public String getRGperShare10Y() {
			return RGperShare10Y;
		}
		public void setRGperShare10Y(String rGperShare10Y) {
			RGperShare10Y = rGperShare10Y;
		}
		public String getRGperShare5Y() {
			return RGperShare5Y;
		}
		public void setRGperShare5Y(String rGperShare5Y) {
			RGperShare5Y = rGperShare5Y;
		}
		public String getRGperShare3Y() {
			return RGperShare3Y;
		}
		public void setRGperShare3Y(String rGperShare3Y) {
			RGperShare3Y = rGperShare3Y;
		}
		public String getOCFGperShare10Y() {
			return OCFGperShare10Y;
		}
		public void setOCFGperShare10Y(String oCFGperShare10Y) {
			OCFGperShare10Y = oCFGperShare10Y;
		}
		public String getOCFGperShare5Y() {
			return OCFGperShare5Y;
		}
		public void setOCFGperShare5Y(String oCFGperShare5Y) {
			OCFGperShare5Y = oCFGperShare5Y;
		}
		public String getOCFGperShare3Y() {
			return OCFGperShare3Y;
		}
		public void setOCFGperShare3Y(String oCFGperShare3Y) {
			OCFGperShare3Y = oCFGperShare3Y;
		}
		public String getDivperShare10Y() {
			return DivperShare10Y;
		}
		public void setDivperShare10Y(String divperShare10Y) {
			DivperShare10Y = divperShare10Y;
		}
		public String getDivperShare5Y() {
			return DivperShare5Y;
		}
		public void setDivperShare5Y(String divperShare5Y) {
			DivperShare5Y = divperShare5Y;
		}
		public String getDivperShare3Y() {
			return DivperShare3Y;
		}
		public void setDivperShare3Y(String divperShare3Y) {
			DivperShare3Y = divperShare3Y;
		}
    	
    	
    	
    	
    }
    
	public static void main(String[] args) {
		

	}

}
