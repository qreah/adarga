package adarga.analysis;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.DB;
import adarga.getinfo.Item;

public class OpportunityIdentification {
	private static final Logger log = Logger.getLogger(BalanceSheet.class.getName());
	
	public JSONObject Level1() throws ClassNotFoundException, SQLException, ServletException, IOException {
		JSONObject json = new JSONObject();
		DB db = new DB();
		// Get the information from CompayValuation table
		
		String SQL = "SELECT * FROM apiadbossDB.CompanyValuation";
		ResultSet rs = db.ExecuteSELECT(SQL);
		while (rs.next()) {
			String sg = rs.getString("salesGrowth");
			if (sg != null) {
				
				JSONObject jsonSG = new JSONObject(sg);
				
				Item itemSG = new Item();
				itemSG.setValues(jsonSG);
				
				String NOPAT = rs.getString("NOPATGrowth");
				JSONObject jsonNOPAT = new JSONObject(NOPAT);
				Item itemNOPAT = new Item();
				itemNOPAT.setValues(jsonNOPAT);
				
				String ROTE = rs.getString("returnOnTangibleEquity");
				JSONObject jsonROTE = new JSONObject(ROTE);
				Item itemROTE = new Item();
				itemROTE.setValues(jsonROTE);
				
				double g10Years = Double.parseDouble(rs.getString("g10Years"));
				
				if (growthCriteriaLevel(itemSG, 0.05) && growthCriteriaLevel(itemNOPAT, 0.05) 
						&& returnLevel(itemROTE, 0.05) && g10YearsLevel(g10Years, 0.02)){
					json.put(rs.getString("Company"), rs.getString("returnOnTangibleEquity"));
				}
			
			}
		}
				
		return json;
		
	}
	
	
	public JSONObject Level2() throws ClassNotFoundException, SQLException, ServletException, IOException {
		JSONObject json = new JSONObject();
		DB db = new DB();
		// Get the information from CompayValuation table
		
		String SQL = "SELECT * FROM apiadbossDB.CompanyValuation";
		ResultSet rs = db.ExecuteSELECT(SQL);
		while (rs.next()) {
			String sg = rs.getString("salesGrowth");
			if (sg != null) {
				
				JSONObject jsonSG = new JSONObject(sg);
				
				Item itemSG = new Item();
				itemSG.setValues(jsonSG);
				
				String NOPAT = rs.getString("NOPATGrowth");
				JSONObject jsonNOPAT = new JSONObject(NOPAT);
				Item itemNOPAT = new Item();
				itemNOPAT.setValues(jsonNOPAT);
				
				String ROTE = rs.getString("returnOnTangibleEquity");
				JSONObject jsonROTE = new JSONObject(ROTE);
				Item itemROTE = new Item();
				itemROTE.setValues(jsonROTE);
				
				double g10Years = Double.parseDouble(rs.getString("g10Years"));
				
				if (g10YearsLevel(g10Years, 0.02)){
					json.put(rs.getString("Company"), rs.getString("returnOnTangibleEquity"));
				}
			
			}
		}
				
		return json;
		
	}
	
	public boolean growthCriteriaLevel(Item item, Double threshold) {
		boolean result = false;
		double changeGoal = threshold;
		double growthLastYear = item.getValue(item.lastYear());
		double growthLastYearMinusOne = item.getValue(item.lastYear()-1);
		if ((growthLastYear > changeGoal) && (growthLastYearMinusOne > changeGoal)) {
				result = true;
		}
		return result;
	}
	
	public boolean returnLevel(Item item, Double threshold) {
		boolean result = false;
		double returnGoal = threshold;
		int temp = 1;
		int size = item.size();
		for (int i=0; i < size; i++) {
			double retorno = item.getValue(item.lastYear()-i);
			if (retorno > returnGoal) {
				temp = temp * 1;
			} else {
				temp = temp * 0;
			}
		}
		if (temp == 1) {
			result = true;
		}
		return result;
	}
	
	public boolean g10YearsLevel(Double g10Years, Double threshold) {
		boolean result = false;
		if (g10Years < threshold) {
			result = true;
		}
		return result;
	}
	
	
}
