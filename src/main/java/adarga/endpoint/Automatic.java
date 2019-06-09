package adarga.endpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adarga.getinfo.AutomaticValuation;
import adarga.getinfo.BalanceSheet;
import adarga.getinfo.DB;

/**
 * Servlet implementation class Automatic
 */
@WebServlet("/automatic")
public class Automatic extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Automatic.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Automatic() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/**
		DB db = new DB();
    	String SQL2 = "SELECT analysis FROM apiadbossDB.CompanyValuation WHERE Symbol = 'BLL'";
		ResultSet rs;
		try {
			rs = db.ExecuteSELECT(SQL2);
		
		while (rs.next()) {
			String blob = rs.getString("analysis");
			log.info("blob: " + blob.toString());
		}
		} catch (ClassNotFoundException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		AutomaticValuation aV = new AutomaticValuation();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			aV.getCompanies();
			out.write("Ok: Done!");
		} catch (SQLException | ClassNotFoundException e) {
			out.write("KO: Problem!");
			e.printStackTrace();
		}
	}

}
