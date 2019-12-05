package adarga.tests;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import adarga.external.KeyMetrics.Metrics;

/**
 * Servlet implementation class TestGSON
 */
@WebServlet("/TestGSON")
public class TestGSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestGSON() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
		String symbol = request.getParameter("sym");
		//String json = "{\"date\":\"2010-01-31\",\"Payables Turnover\":\"3.4878\",\"EV to Sales\":\"2.2519\",\"Graham Net-Net\":\"0.1697\",\"Average Receivables\":\"346699000.0\",\"Intangibles to Total Assets\":\"0.1367\",\"Payout Ratio\":\"0.0\",\"POCF ratio\":\"18.849\",\"Capex to Depreciation\":\"-0.3946\",\"PB ratio\":\"3.4698\",\"PFCF ratio\":\"22.1997\",\"Shareholders Equity per Share\":\"4.8495\",\"PTB ratio\":\"4.252\",\"Net Income per Share\":\"-0.1237\",\"Free Cash Flow Yield\":\"0.0446\",\"Net Current Asset Value\":\"1560052000.0\",\"Invested Capital\":\"1888467000.0\",\"EV to Free cash flow\":\"18.2614\",\"ROE\":\"-0.0255\",\"EV to Operating cash flow\":\"15.3563\",\"Current ratio\":\"3.163\",\"Cash per Share\":\"0.8138\",\"ROIC\":\"-0.0576\",\"Book Value per Share\":\"4.849\",\"Price to Sales Ratio\":\"2.7641\",\"Capex to Revenue\":\"0.0233\",\"Working Capital\":\"1696452000.0\",\"Return on Tangible Assets\":\"-0.03\",\"Market Cap\":\"9194693000.0\",\"Stock-based compensation to Revenue\":\"0.0724\",\"Days Sales Outstanding\":\"41.1435\",\"SG&A to Revenue\":\"0.1103\",\"Enterprise Value\":\"7490916000.0\",\"Inventory Turnover\":\"7.6601\",\"Tangible Asset Value\":\"3095616000.0\",\"Free Cash Flow per Share\":\"0.7464\",\"Income Quality\":\"-7.175\",\"Tangible Book Value per Share\":\"5.633\",\"Earnings Yield\":\"-0.0072\",\"PE ratio\":\"0.0\",\"Capex to Operating Cash Flow\":\"0.1591\",\"R&D to Revenue\":\"0.2732\",\"Average Inventory\":\"434254000.0\",\"Graham Number\":\"0.0\",\"Days Payables Outstanding\":\"37.8038\",\"Enterprise Value over EBITDA\":\"63.6496\",\"Capex per Share\":\"-0.1412\",\"Average Payables\":\"281695500.0\",\"Revenue per Share\":\"6.0528\",\"Operating Cash Flow per Share\":\"0.8876\",\"Interest Coverage\":\"-23.7873\",\"Dividend Yield\":\"0.0\",\"Debt to Assets\":\"0.0068\",\"Net Debt to EBITDA\":\"-14.4768\",\"Debt to Equity\":\"0.0092\",\"Interest Debt per Share\":\"0.0445\",\"Days of Inventory on Hand\":\"-56.1502\",\"Receivables Turnover\":\"9.5946\"}";
		String json = "{'date': '23/08/19', 'Revenue per Share': '0.76'}";
		Gson gson = new Gson();
		Metrics2 ratios = gson.fromJson(json, Metrics2.class);
		out.write(ratios.revenuePerShare);
	}
	
	
	static public class Metrics2 {
		
		@SerializedName("date")  public String date;
		@SerializedName("Revenue per Share") public String revenuePerShare;
	}

}
