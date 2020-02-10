package adarga.cron;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import adarga.external.BalanceSheet;
import adarga.external.CashFlowStatement;
import adarga.external.CompanyHub;
import adarga.external.CompanyProfile;
import adarga.external.IncomeStatement;
import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.DBOne;
import adarga.utils.TableSet;
import adarga.utils.qreah;

/**
 * Servlet implementation class Hub
 */
@WebServlet("/Hub")
public class Hub extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Hub.class.getName());
       
    
    public Hub() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("static-access")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        
        qreah q = new qreah();
        String start = q.hora() + ":" + q.minutos() + ":" + q.segundos();
        
        int rows = 7936;
    	int batch = Integer.parseInt(request.getParameter("batch"));
    	int numCom = batch;
        
        CompanyHub comHub = new CompanyHub();
        JSONArray companies = comHub.getCompaniesList();
        DBOne one = new DBOne();
		try {
			one.ConnectDBOne();
			int round = one.getRound();
			if ((round + batch) > rows) {
				round = 0;
			} 
			int j = 0;
			int i = round;
			int contador = 0;
			//for (int i = round; i < round + batch; i++) {
			while (contador < numCom) {
				i++;
				contador++;
				JSONObject json = new JSONObject(companies.get(i).toString());
				String symbol = json.getString("symbol");
				
				
				// Create a table with all the registers from the company
				// that already exists
				
				String exists = "SELECT symbol, concept, finantialDate FROM apiadbossDB.adargaConcepts "
						+ "where symbol = '" + symbol + "'";
				ResultSet rs = one.ExecuteSELECT(exists);
				List<TableSet> companyRegisters = new ArrayList<TableSet>();
				while (rs.next()) {
					String sym = rs.getString("symbol");
					String concept = rs.getString("concept");
					String finantialDate = rs.getString("finantialDate");
					TableSet companyRegister = new TableSet();
					companyRegister.setSymbol(sym);
					companyRegister.setConcept(concept);
					companyRegister.setFinantialDate(finantialDate);
					
					companyRegisters.add(companyRegister);
				}
				rs.close();
				
				Profile profile = new CompanyProfile().getProfile(symbol);
				String companyName = profile.getCompanyName().replaceAll("'", "");
				String sector = profile.getSector().replaceAll("'", "");
				String industry = profile.getIndustry().replaceAll("'", "");
				String description = profile.getDescription();
				description = description.replaceAll("'", "");
				String price = profile.getPrice();
				String mktCap = profile.getMktCap();
				
				if ((!sector.equals("")) 
						&& (!sector.equals("Financial Services"))
							) {
					
					// Gets data from the company through the API 
					// only if it is not a financial company
					
					HashMap<String, String> companyData = new HashMap<String, String>();
					companyData.put("symbol", symbol);
					companyData.put("companyName", companyName);
					companyData.put("sector", sector);
					companyData.put("industry", industry);
					companyData.put("description", description);
					companyData.put("price", price);
					companyData.put("mktCap", mktCap);
					
					HttpSession session = request.getSession();
					session.setAttribute("companyData", companyData);
					session.setAttribute("companyRegisters", companyRegisters);
					String serviceIS = "/serviceIS" + j;
					String serviceBS = "/serviceBS" + j;
					String serviceCS = "/serviceCS" + j;
					j++;
					log.info(symbol + " | " + i);
					request.getRequestDispatcher(serviceIS).include(request, response);
					request.getRequestDispatcher(serviceBS).include(request, response);
					request.getRequestDispatcher(serviceCS).include(request, response);
				    
					
				} else {
					contador = contador -1;
				}
				one.setRound(i + 1);
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}	
		
		
		
		String end = q.hora() + ":" + q.minutos() + ":" + q.segundos();
		out.write("<br>");
		out.write("Start: " + start);
		out.write("<br>");
		out.write("End: " + end);
		
	}
	
	public void dispatch(HttpServletRequest request, HttpServletResponse response, String serviceIS) throws ServletException, IOException {
		
		response.sendRedirect(serviceIS);
		//RequestDispatcher dispatcher = getServletContext()
		//	      .getRequestDispatcher(serviceIS);
		//dispatcher.forward(request, response);  
		
		return;
	}

}
