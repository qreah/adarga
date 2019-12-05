package adarga.cron;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adarga.external.Storage;
import adarga.utis.qreah;

/**
 * Servlet implementation class Data
 */
@WebServlet("/data")
public class Data extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Data() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		qreah q = new qreah();
		String init = Long.toString(q.getTimestamp());
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		Storage st = new Storage();
		out.write("Store Data");
		try {
			
			String start = q.hora() + ":" + q.minutos() + ":" + q.segundos();
			st.getCompanies();
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
