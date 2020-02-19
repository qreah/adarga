package adarga.endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adarga.getinfo.DB;

/**
 * Servlet implementation class GetFCFY
 */
@WebServlet("/fcfyield")
public class GetFCFY extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(GetFCFY.class.getName());
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFCFY() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        String ini = request.getParameter("ini");
        String fin = request.getParameter("fin");
        String sym = request.getParameter("sym");
        
        out.write("api.adarga.org/fcfyield?ini=0.2&fin=0.3");
        out.write("<br>");
        
        if (sym != null) {
        	
            String Industry = request.getParameter("industry");
          
            try {
    			DB db = new DB();
    			String SQL = "SELECT companyName, symbol, value, reportDate, industry, description FROM apiadbossDB.FCFYield\n" + 
    					"	where concept = 'FCFY'\n" + 
    					"		and symbol='" + sym + "'";
    			
    			log.info(SQL);
    			ResultSet rs = db.ExecuteSELECT(SQL);
    			while (rs.next()) {
    				String companyName = rs.getString("companyName");
    				String symbol = rs.getString("symbol");
    				String fcfYield = rs.getString("value");
    				String reportDate = rs.getString("reportDate");
    				String industry = rs.getString("industry");
    				String description = rs.getString("description");
    				String output = "<br>";
    				output = output + companyName + " | " + symbol;
    				output = output + "<br>";
    				output = output + "FCF Yield: " + fcfYield;
    				output = output + "<br>";
    				output = output + "Report Date: " + reportDate;
    				output = output + "<br>";
    				output = output + "Industry: " + industry;
    				output = output + "<br>";
    				output = output + "Description: " + description;
    				output = output + "<br>";
    				output = output + "<br>";
    				output = output + "<br>";
    				out.write(output);
    				
    			}
    			
    		} catch (ClassNotFoundException | SQLException e) {
    			e.printStackTrace();
    		}
        } else {
        	if (ini == null) {
            	ini = "0.2";
            }
            if (fin == null) {
            	fin = "0.3";
            }
            String Industry = request.getParameter("industry");
            
            log.info(Industry);
            try {
    			DB db = new DB();
    			String SQL = "SELECT companyName, symbol, value, reportDate, industry, description FROM apiadbossDB.FCFYield\n" + 
    					"	where concept = 'FCFY'\n" + 
    					"		and value > " + ini + "\n" + 
    					"        and value is not NULL\n" + 
    					"        and value < " + fin + "\n" + 
    					"        and industry != ''\n";
    			
    			if (Industry != null) {
    				SQL = SQL + " and industry=" + Industry + "\n";
    	        }
    			SQL	= SQL +	"	order by 3 desc";
    			log.info(SQL);
    			ResultSet rs = db.ExecuteSELECT(SQL);
    			while (rs.next()) {
    				String companyName = rs.getString("companyName");
    				String symbol = rs.getString("symbol");
    				String fcfYield = rs.getString("value");
    				String reportDate = rs.getString("reportDate");
    				String industry = rs.getString("industry");
    				String description = rs.getString("description");
    				String output = "<br>";
    				output = output + companyName + " | " + symbol;
    				output = output + "<br>";
    				output = output + "FCF Yield: " + fcfYield;
    				output = output + "<br>";
    				output = output + "Report Date: " + reportDate;
    				output = output + "<br>";
    				output = output + "Industry: " + industry;
    				output = output + "<br>";
    				output = output + "Description: " + description;
    				output = output + "<br>";
    				output = output + "<br>";
    				output = output + "<br>";
    				out.write(output);
    				
    			}
    			
    		} catch (ClassNotFoundException | SQLException e) {
    			e.printStackTrace();
    		}
        }
        
        
        
	}

}
