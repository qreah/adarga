package adarga.endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try {
			DB db = new DB();
			String SQL = "SELECT companyName, symbol, value, reportDate, industry, description FROM apiadbossDB.adargaConcepts\n" + 
					"	where concept = 'FCFY'\n" + 
					"		and value > 0.2\n" + 
					"        and value is not NULL\n" + 
					"        and value < 0.3\n" + 
					"        and industry != ''\n" + 
					"	order by 3 desc";
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
