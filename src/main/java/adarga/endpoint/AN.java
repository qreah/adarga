package adarga.endpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import adarga.getinfo.BalanceSheet;
import adarga.getinfo.CashFlowStatement;
import adarga.getinfo.Company;
import adarga.getinfo.CompanyInformation;
import adarga.getinfo.IncomeStatement;


/**
 * Servlet implementation class AN
 */
@WebServlet("/analysis")
public class AN extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AN() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        
		String company = request.getParameter("com");
		//comment to be erased
		BalanceSheet bs = new BalanceSheet();
		IncomeStatement is = new IncomeStatement();
		CashFlowStatement cs = new CashFlowStatement();
		Company com = new Company();
		List<Object> reports;
		try {
			reports = com.getFinancialStatements(company);
		
		CompanyInformation ci = new CompanyInformation();
		bs = (BalanceSheet) reports.get(0);
		is = (IncomeStatement) reports.get(1);
		cs = (CashFlowStatement) reports.get(2);
		ci = (CompanyInformation) reports.get(3);
		
		
		JSONObject result;
		
		
			result = com.analysis(bs, is, cs, ci);
			out.println(result.toString());	
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}

}
