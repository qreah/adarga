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
import adarga.ratios.FinancialManagement;
import adarga.ratios.GlobalManagement;

/**
 * Servlet implementation class Print2Check
 */
@WebServlet("/Print2Check")
public class Print2Check extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Print2Check() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		GlobalManagement gm = new GlobalManagement();
		FinancialManagement fm = new FinancialManagement();
		BalanceSheet bs = new BalanceSheet();
		IncomeStatement is = new IncomeStatement();
		CashFlowStatement cs = new CashFlowStatement();
		String company = request.getParameter("com");
		Company com = new Company();
		List<Object> reports;
		try {
			reports = com.getFinancialStatements(company);
		
		CompanyInformation ci = new CompanyInformation();
		bs = (BalanceSheet) reports.get(0);
		is = (IncomeStatement) reports.get(1);
		cs = (CashFlowStatement) reports.get(2);
		ci = (CompanyInformation) reports.get(3);
		gm.loadGlobalManagement(bs, is, cs, ci);
		fm.loadFinancialManagement(bs, is, cs);
		
		
		String print = gm.print2Check() + "<br>" + fm.print2Check();
		
		//JSONObject result;
		//result = com.analysis(bs, is, cs, ci);
		out.println(print);	
		
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		
	}

}
