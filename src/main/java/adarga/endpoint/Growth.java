package adarga.endpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import adarga.analysis.OpportunityIdentification;

/**
 * Servlet implementation class Growth
 */
@WebServlet("/growth")
public class Growth extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Growth() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
    	OpportunityIdentification oi = new OpportunityIdentification();
    	try {
			JSONArray array = oi.growth();
			int len = array.length();
			for (int i=0; i<len; i++) {
				JSONObject json = (JSONObject) array.get(i);
				out.write(json.getString("Company") + "<BR>");
				out.write(json.getString("Sector") + "<BR>");
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
