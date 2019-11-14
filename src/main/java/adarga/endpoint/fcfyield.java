package adarga.endpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import adarga.analysis.OpportunityIdentification;
import adarga.getinfo.BalanceSheet;

/**
 * Servlet implementation class Level1
 */
@WebServlet("/fcfyield")
public class fcfyield extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(fcfyield.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public fcfyield() {
    	
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
    	OpportunityIdentification oi = new OpportunityIdentification();
    	try {
			JSONArray array = oi.fcfYield();
			int len = array.length();
			for (int i=0; i<len; i++) {
				JSONObject json = (JSONObject) array.get(i);
				log.info(json.toString());
				out.write(json.getString("Company") + "<BR>");
				out.write(json.getString("Sector") + "<BR>");
				out.write(json.getString("Industry") + "<BR>");
				out.write(json.getString("Description") + "<BR>");
				out.write(json.getString("Symbol") + "<BR>");
				out.write(json.getDouble("FCFYield") + "<BR>");
				out.write("<BR>");
				out.write("<BR>");
				
				
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}

}
