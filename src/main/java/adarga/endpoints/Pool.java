package adarga.endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adarga.getinfo.DB;

/**
 * Servlet implementation class Pool
 */
@WebServlet("/pool")
public class Pool extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Pool() {
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
        
		String SQL = "SELECT * FROM apiadbossDB.adargaConcepts where symbol = 'IBP'";
		DB db;
		try {
			db = new DB();
			db.abort();
			/*
			String Texto = "";
			ResultSet rs = db.executeSelectHikari(SQL);
			while (rs.next()) {
				Texto = rs.getString("symbol") 
						+ rs.getString("concept") 
						+ rs.getString("value") 
						+ rs.getString("finantialDate") 
						+ rs.getString("reportDate") ;
			}
			out.write(Texto);
			*/
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}

}
