package adarga.endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adarga.external.CompanyProfile.Profile;
import adarga.external.Ratios;
import adarga.external.Ratios.FinRatios;
import adarga.external.Ratios.GrowthMetrics;
import adarga.external.Ratios.KeyMetrics;
import adarga.utils.qreah;

/**
 * Servlet implementation class FinantialRatios
 */
@WebServlet("/ratios")
public class FinantialRatios extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(GetFCFY.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FinantialRatios() {
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
        
        String symbol = request.getParameter("com");
        if (symbol.isEmpty()) {
        	symbol = "MMM";
        }
        Ratios ratios = new Ratios();
        qreah q = new qreah();
        String start = q.hora() + ":" + q.minutos() + ":" + q.segundos();
        try {
			ratios.store(symbol);
		} catch (ClassNotFoundException | SQLException e) {e.printStackTrace();}
        String end = q.hora() + ":" + q.minutos() + ":" + q.segundos();
		out.write("<br>");
		out.write("Start: " + start);
		out.write("<br>");
		out.write("End: " + end);       
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
