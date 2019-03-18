package adarga;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Automatic
 */
@WebServlet("/automatic")
public class Automatic extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Automatic() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AutomaticValuation aV = new AutomaticValuation();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			aV.getCompanies();
			out.write("Ok: Done!");
		} catch (SQLException | ClassNotFoundException e) {
			out.write("KO: Problem!");
			e.printStackTrace();
		}
	}

}
