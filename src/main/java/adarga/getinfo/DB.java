package adarga.getinfo;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.google.apphosting.api.ApiProxy;


public class DB {
	private Connection conn;
	private String DB = "apiadbossDB.Users";
	private String locationTable = "apiadbossDB.locations";
	private static final Logger log = Logger.getLogger(DB.class.getName());
	String dataBase = "apiadbossDB";
	String instanceConnectionName = "devadboss-181207:europe-west1:apiadboss";
	String roorPass = "rootadboss2018";
	
	public void DB() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		conn = ConnectDB();
		
	}
	
	public void close() throws SQLException {
		conn.close();
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
		      url = "jdbc:google:mysql://" + instanceConnectionName + "/" + dataBase + "?user=root&password=" + roorPass;
		      
			  
		
		// If you are on App Engine environment
		} else {   
	      
		  
		  Properties properties = new Properties();
			String PropiedadesConexion = "/resources/config.properties";
			//properties.load(getClass().getClassLoader().getResourceAsStream(PropiedadesConexion));
	        //url = properties.getProperty("sqlUrl");
			url = "jdbc:mysql://google/" + dataBase + "?cloudSqlInstance=" + instanceConnectionName + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=" + roorPass + "&useSSL=false";
			//url = "jdbc:mysql://google/gmb_db?cloudSqlInstance=mov-prod3:europe-west1:mov-gmb&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=FAYO0173&useSSL=false"; 
			
	    	
		}     
	    
		
		try {  
	    	
			//if (url == null)
			//	url = "jdbc:mysql://google/MB?cloudSqlInstance=marketboss-201812:europe-west1:marketboss&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=QuiqueRafa&useSSL=false";
			
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	
	    	
	    	conn = DriverManager.getConnection(url); 
	    	
	        return conn;
	      } catch (SQLException e) {
	    	  
	        throw new ServletException("Unable to connect to Cloud SQL", e);
	      }
		
		
		
		}
	
	public Connection ConnectDB(String PropiedadesConexion, ServletContext contexto) {
		//Método cuyo objetivo es conectarse a una base de datos desde un servlet
		
		String url = "";
	      
	    Properties properties = new Properties();
	    try {
	        properties.load(contexto.getResourceAsStream(PropiedadesConexion));
	        url = properties.getProperty("sqlUrl");
	        System.out.println("url: " + url);
	    } catch (IOException e) {
	    	System.out.println("No hay configuración del JDBC en el fichero de propiedades");
	        System.out.println(e);
	        
	    }

	    System.out.println("connecting to: " + url);
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        System.out.println("url: " + url);
	        conn = DriverManager.getConnection(url); 
	        
			  
	    } catch (ClassNotFoundException e) {
	    	System.out.println("Error loading JDBC Driver");
	        System.out.println(e);
	        
	    } catch (SQLException e) {
	    	System.out.println("Unable to connect to PostGre");
	        System.out.println(e);  
	        
	    }
	    return conn;
	}
	
	public ResultSet ExecuteSELECT(String SQL) throws SQLException, ClassNotFoundException, ServletException, IOException {
		conn = ConnectDB();
		ResultSet rsfinal = null;
		ResultSet rs = null;
		if (conn == null) {
			
		} else {
			Statement secuencia = conn.createStatement(); 
			rs = (ResultSet) secuencia.executeQuery(SQL);
		}
		rsfinal = rs;
		
		return rsfinal;
	}
	
	public boolean Execute(String SQL) throws ClassNotFoundException, ServletException, IOException, SQLException  {
		conn = ConnectDB();
		
		boolean executed = false;
		Statement secuencia;
		try {
			
			secuencia = conn.createStatement();
			secuencia.executeUpdate(SQL);
			executed = true;
		} catch (SQLException e) {
			executed = false;
			log.info("Error in 'Execute' function inside BD.java class");
			e.printStackTrace();
		} 
		conn.close();
		return executed;
	}

	public int getRound() throws SQLException, ClassNotFoundException, ServletException, IOException {
		int round = 0;
		String SQL = "SELECT value FROM apiadbossDB.cv_variables WHERE variable = 'round'";
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			round = Integer.parseInt(rs.getString("value"));
	    }
		close();
		return round;
	}
	
	public void setRound(int round) throws ClassNotFoundException, ServletException, IOException, SQLException {
		String roundString = String.valueOf(round);
		String SQL = "UPDATE apiadbossDB.cv_variables SET value = '" + roundString + "' WHERE variable = 'round'";
		Execute(SQL);
		
	}
	
	public boolean companyIncluded(String Symbol) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		String SQL = "SELECT Company FROM apiadbossDB.CompanyValuation WHERE Symbol = '" + Symbol + "'";
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			result = true;
		}
		close();
		return result;
	}

}
