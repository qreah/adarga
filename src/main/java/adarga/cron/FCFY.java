package adarga.cron;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import adarga.external.BalanceSheet;
import adarga.external.CompanyProfile;
import adarga.external.KeyMetrics;
import adarga.external.Storage;
import adarga.external.CompanyProfile.Profile;
import adarga.getinfo.DB;
import adarga.utis.qreah;

/**
 * Servlet implementation class FCFY
 */
@WebServlet("/FCFY")
public class FCFY extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(KeyMetrics.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FCFY() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
		Storage st = new Storage();
		qreah q = new qreah();
		try {
			String start = q.hora() + ":" + q.minutos() + ":" + q.segundos();
			st.storeFCFY();
			String end = q.hora() + ":" + q.minutos() + ":" + q.segundos();
			out.write("<br>");
			out.write("Start: " + start);
			out.write("<br>");
			out.write("End: " + end);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

}
