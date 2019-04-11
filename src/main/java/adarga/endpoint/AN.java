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
		String company = request.getParameter("com");
		//comment to be erased
		BalanceSheet bs = new BalanceSheet();
		IncomeStatement is = new IncomeStatement();
		CashFlowStatement cs = new CashFlowStatement();
		Company com = new Company();
		com.getFinancialStatements(company);
		CompanyInformation ci = new CompanyInformation();
		JSONObject result = com.analysis(bs, is, cs, ci);
		//Málaga Madrid
		out.println(company + "<BR>");
		out.println("<BR>");
		out.println("salesGrowth: " + result.getString("salesGrowth") + "<BR>");
		out.println("<BR>");
		out.println("NOPATGrowth: " + result.getString("NOPATGrowth") + "<BR>");
		out.println("<BR>");
		out.println("NOPATMargin: " + result.getString("NOPATMargin") + "<BR>");
		out.println("<BR>");
		out.println("netWorkingCapitalOverRevenues: " + result.getString("netWorkingCapitalOverRevenues") + "<BR>");
		out.println("<BR>");
		out.println("netLongTermAssetsOverRevenue: " + result.getString("netLongTermAssetsOverRevenue") + "<BR>");
		out.println("<BR>");
		out.println("netDebtToCapitalRatio: " + result.getString("netDebtToCapitalRatio") + "<BR>");
		out.println("<BR>");
		out.println("afterTaxCostOfDebt: " + result.getString("afterTaxCostOfDebt") + "<BR>");
		out.println("<BR>");
		out.println("OperatingROA: " + result.getString("OperatingROA") + "<BR>");
		out.println("<BR>");
		out.println("salesOverNetAssets: " + result.getString("salesOverNetAssets") + "<BR>");
		out.println("<BR>");
		out.println("ROE: " + result.getString("ROE") + "<BR>");
		out.println("<BR>");
		out.println("returnOnTangibleEquity: " + result.getString("returnOnTangibleEquity") + "<BR>");
		out.println("<BR>");
		out.println("growthIncome: " + result.getString("growthIncome") + "<BR>");
		out.println("<BR>");
		out.println("growthOperatingIncome: " + result.getString("growthOperatingIncome") + "<BR>");
		out.println("<BR>");
		out.println("g10Years: " + result.getString("g10Years") + "<BR>");
		
		
		
	}

}
