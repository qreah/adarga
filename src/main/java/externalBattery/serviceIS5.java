package externalBattery;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import adarga.cron.Hub;
import adarga.external.IncomeStatement;
import adarga.getinfo.DBOne;
import adarga.utils.TableSet;

/**
 * Servlet implementation class serviceIS0
 */
@WebServlet("/serviceIS5")
public class serviceIS5 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(serviceIS5.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public serviceIS5() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		
		IncomeStatement is = new IncomeStatement();
		HttpSession session = request.getSession();
		
		HashMap<String, String> companyData = new HashMap<String, String>();
		companyData = (HashMap<String, String>) session.getAttribute("companyData");
		List<TableSet> companyRegisters = (List<TableSet>) session.getAttribute("companyRegisters");
		DBOne one = new DBOne();
		try {
			one.ConnectDBOne();
			
			is.storeReport(companyData, one, companyRegisters);
			
			one.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

}
