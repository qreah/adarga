package adarga.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import adarga.external.FinantialRatios.Ratios;
import adarga.external.FinantialRatios.Series;


/**
 * Servlet implementation class External
 */
@WebServlet("/External")
public class External extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(External.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public External() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
		String symbol = request.getParameter("sym");
		
		
					FinantialRatios fr = new FinantialRatios();
			KeyMetrics km = new KeyMetrics();
			Growth g = new Growth();
			BalanceSheet bs = new BalanceSheet();
			IncomeStatement is = new IncomeStatement();
			CashFlowStatement cs = new CashFlowStatement();
			//fr.storeReport(symbol);
			//km.storeReport(symbol);
			//g.storeReport(symbol);
			//is.storeReport(symbol);
			//bs.storeReport(symbol);
			//cs.storeReport(symbol);
		
	}

}
