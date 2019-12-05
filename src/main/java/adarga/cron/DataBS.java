package adarga.cron;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import adarga.external.BalanceSheet;
import adarga.external.CompanyProfile;
import adarga.external.KeyMetrics;
import adarga.external.Storage;
import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.DB;
import adarga.utis.qreah;

/**
 * Servlet implementation class DataBS
 */
@WebServlet("/DataBS")
public class DataBS extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(KeyMetrics.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataBS() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
		DB db;
		try {
			qreah q = new qreah();
			String start = q.hora() + ":" + q.minutos() + ":" + q.segundos();
			Storage st = new Storage();
			JSONArray array = st.getCompaniesList();
			db = new DB();
			int round = db.getRound();
			JSONObject json = new JSONObject(array.get(round).toString());	
			String symbol = json.getString("symbol");
			BalanceSheet bs = new BalanceSheet();
			
			Profile profile = new CompanyProfile().getProfile(symbol);
			String companyName = profile.getCompanyName();
			String sector = profile.getSector();
			String industry = profile.getIndustry();
			String description = profile.getDescription().replace("'", "");
			String price = profile.getPrice();
			String mktCap = profile.getMktCap();
			
			HashMap<String, String> companyData = new HashMap<String, String>();
			companyData.put("symbol", symbol);
			companyData.put("companyName", companyName);
			companyData.put("sector", sector);
			companyData.put("industry", industry);
			companyData.put("description", description);
			companyData.put("price", price);
			companyData.put("mktCap", mktCap);
			
			
			bs.storeReport(companyData);	
			String end = q.hora() + ":" + q.minutos() + ":" + q.segundos();
			out.write("Symbol: " + symbol);
			out.write("<br>");
			out.write("Start: " + start);
			out.write("<br>");
			out.write("End: " + end);
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		
	}

}
