package adarga.endpoints;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Info
 */
@WebServlet("/info")
public class Info extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Info() {
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
        out.write("api.adarga.org Services");
        out.write("<br>");
        out.write("<br>");
        out.write("List of companies with FCF Yield");
        out.write("<br>");
        out.write("**************************************");
        out.write("<br>");
        out.write("<br>");
        out.write("api.adarga.org/fcfyield?ini=0.2&fin=0.3&sym=MMM");
        out.write("<br>");
        out.write("<br>");
        out.write("If you use sym is to get the FCF Yield for just a company and ini and fin parameters are disabled");
        out.write("<br>");
	}

}
