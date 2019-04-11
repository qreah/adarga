package adarga.endpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import adarga.analysis.OpportunityIdentification;

/**
 * Servlet implementation class Level1
 */
@WebServlet("/level2")
public class Level2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Level2() {
    	
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
    	OpportunityIdentification oi = new OpportunityIdentification();
    	try {
			JSONObject json = oi.Level2();
			out.write(json.toString());
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}

}