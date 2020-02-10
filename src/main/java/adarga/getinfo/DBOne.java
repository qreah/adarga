package adarga.getinfo;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.google.apphosting.api.ApiProxy;

public class DBOne implements Serializable {
	
	private Connection conn;
	private String DB = "apiadbossDB.Users";
	private String locationTable = "apiadbossDB.locations";
	private static final Logger log = Logger.getLogger(DBOne.class.getName());
	String dataBase = "apiadbossDB";
	String instanceConnectionName = "devadboss-181207:europe-west1:apiadboss";
	String user = "root";
	String rootPass = "rootadboss2018";
	Statement statement;
	
	public void ConnectDBOne() throws ClassNotFoundException, SQLException, ServletException, IOException  {
		
		conn = ConnectDB();
		statement = conn.createStatement();
    	
    	
	}
	
	public void close() throws SQLException {
		conn.close();
		conn.abort(Runnable::run);
		statement.close();
	}
	
public Connection ConnectDB() throws ServletException, ClassNotFoundException, IOException, SQLException {
		
		String url;
		ApiProxy.Environment env = ApiProxy.getCurrentEnvironment();
		
		// If your are not at App Engine environment, you work al localhost and
		// your driver url is different	
		InetAddress iAddress = InetAddress.getLocalHost();
        String hostName = iAddress.getHostName();
        
		if (hostName.equals("localhost")) {  
			Map<String,Object> attr = env.getAttributes();
		      String hostname = (String) attr.get("com.google.appengine.runtime.default_version_hostname");
		      url = hostname.contains("localhost:")
		          ? System.getProperty("cloudsql-local") : System.getProperty("cloudsql");
		      url = "jdbc:google:mysql://" + instanceConnectionName + "/" + dataBase + "?user=root&password=" + rootPass;
		      
			  
		
		// If you are on App Engine environment
		} else {   
	      
		  
		  Properties properties = new Properties();
			String PropiedadesConexion = "/resources/config.properties";
			//properties.load(getClass().getClassLoader().getResourceAsStream(PropiedadesConexion));
	        //url = properties.getProperty("sqlUrl");
			url = "jdbc:mysql://google/" + dataBase + "?cloudSqlInstance=" + instanceConnectionName + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=" + rootPass + "&useSSL=false";
			//url = "jdbc:mysql://google/gmb_db?cloudSqlInstance=mov-prod3:europe-west1:mov-gmb&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=FAYO0173&useSSL=false"; 
			
	    	
		}     
	    
		
		try {  
	    	
			//if (url == null)
			//	url = "jdbc:mysql://google/MB?cloudSqlInstance=marketboss-201812:europe-west1:marketboss&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=QuiqueRafa&useSSL=false";
			
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	Connection conn = DriverManager.getConnection(url); 
	    	
	        return conn;
	      } catch (SQLException e) {
	    	  
	        throw new ServletException("Unable to connect to Cloud SQL", e);
	      }
		
		
		
		}
	
	public int getRound() throws SQLException, ClassNotFoundException, ServletException, IOException {
		int round = 0;
		String SQL = "SELECT value FROM apiadbossDB.cv_variables WHERE variable = 'round'";
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			round = Integer.parseInt(rs.getString("value"));
	    }
		
		return round;
	}
	
	public void setRound(int round) throws ClassNotFoundException, ServletException, IOException, SQLException {
		String roundString = String.valueOf(round);
		String SQL = "UPDATE apiadbossDB.cv_variables SET value = '" + roundString + "' WHERE variable = 'round'";
		Execute(SQL);
		
	}
	
	public int getRoundFCFY() throws SQLException, ClassNotFoundException, ServletException, IOException {
		int round = 0;
		String SQL = "SELECT value FROM apiadbossDB.cv_variables WHERE variable = 'roundFCFY'";
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			round = Integer.parseInt(rs.getString("value"));
	    }
		
		return round;
	}
	
	
	public void setRoundFCFY(int round) throws ClassNotFoundException, ServletException, IOException, SQLException {
		String roundString = String.valueOf(round);
		String SQL = "UPDATE apiadbossDB.cv_variables SET value = '" + roundString + "' WHERE variable = 'roundFCFY'";
		Execute(SQL);
		
	}
	
	public ResultSet ExecuteSELECT(String SQL) throws SQLException   {
		
		return statement.executeQuery(SQL);
	}
	
	public boolean Execute(String SQL) throws ClassNotFoundException, ServletException, IOException, SQLException  {
		
		boolean equity = SQL.contains("tockholders");
		if (equity) {
			SQL = SQL.replace("tockholders'", "tockholders");
		}
		
		boolean executed = false;
		//Statement secuencia;
		try {
			statement.executeUpdate(SQL);
			executed = true;
		} catch (SQLException e) {
			executed = false;
			log.info("Error in 'Execute' function inside BD.java class");
			e.printStackTrace();
		} 
		
		return executed;
	}
	
	public void addBatch(String SQL) throws ClassNotFoundException, SQLException, ServletException, IOException {
		statement.addBatch(SQL);
	}
	
	public int[] executeBatch() throws SQLException  {
		
		int[] result = statement.executeBatch();
		statement.clearBatch();
		
		return result;
	}
	
	public static void main(String[] args) {
		

	}

}
