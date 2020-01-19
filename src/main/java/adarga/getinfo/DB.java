package adarga.getinfo;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import com.google.appengine.api.ThreadManager;
import com.google.apphosting.api.ApiProxy;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class DB {
	private Connection conn;
	private String DB = "apiadbossDB.Users";
	private String locationTable = "apiadbossDB.locations";
	private static final Logger log = Logger.getLogger(DB.class.getName());
	String dataBase = "apiadbossDB";
	String instanceConnectionName = "devadboss-181207:europe-west1:apiadboss";
	String roorPass = "rootadboss2018";
	Statement statement;
	DataSource pool;
	
	public DB() throws ClassNotFoundException, ServletException, IOException, SQLException {
		
		conn = ConnectDB();
		statement = conn.createStatement();
		//ConnectDBHikari();
		
	}
	
	
	
	public void close() throws SQLException {
		conn.close();
		conn.abort(Runnable::run);
		statement.close();
	}
	
	public void abort() throws SQLException {
		conn.abort(Runnable::run);
	}
	
	public void reviveDB() throws ClassNotFoundException, ServletException, IOException, SQLException {
		conn = ConnectDB();
		statement = conn.createStatement();
		
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
	    	Connection conn = DriverManager.getConnection(url); 
	    	
	        return conn;
	      } catch (SQLException e) {
	    	  
	        throw new ServletException("Unable to connect to Cloud SQL", e);
	      }
		
		
		
		}
	
	public Connection ConnectDBOne() throws ClassNotFoundException, SQLException {
		
		String url = "jdbc:mysql://google/" + dataBase + "?cloudSqlInstance=" + instanceConnectionName + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=" + roorPass + "&useSSL=false";
		Class.forName("com.mysql.cj.jdbc.Driver");
    	Connection conn = DriverManager.getConnection(url); 
    	return conn;
	}
	
	public void ConnectDBHikari() {
		//String url = "jdbc:mysql://google/" + dataBase + "?cloudSqlInstance=" + instanceConnectionName + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=" + roorPass + "&useSSL=false";
	
		String url = "jdbc:mysql://google/" + dataBase; // + "?cloudSqlInstance=" + instanceConnectionName + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=" + roorPass + "&useSSL=false";
		
		HikariConfig config = new HikariConfig();
		//config.setDriverClassName("com.mysql.cj.jdbc.Driver");
		config.setJdbcUrl(String.format("jdbc:mysql:///%s", dataBase));
		//config.setJdbcUrl(url);
		//config.setThreadFactory(ThreadManager.backgroundThreadFactory());
		
		config.setUsername("root"); 
		config.setPassword(roorPass); 
		config.setRegisterMbeans(false);
		
		config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
		config.addDataSourceProperty("cloudSqlInstance", instanceConnectionName);
		config.addDataSourceProperty("useSSL", "false");
		
		/*
		config.setMaximumPoolSize(12);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("useServerPrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		*/
		config.setMaximumPoolSize(5);       
        config.setMinimumIdle(5);
		config.setConnectionTimeout(10000); // 10 seconds
		config.setIdleTimeout(600000); // 10 minutes
		config.setMaxLifetime(1800000); // 30 minutes
		
		
		pool = new HikariDataSource(config);
		
	}
	
	public void executeHikari(String SQL) {
		try (Connection conn = pool.getConnection()) {

			PreparedStatement voteStmt = conn.prepareStatement(SQL);
			voteStmt.execute();

		} catch (SQLException ex) {
			  
		}
	}
	
	public ResultSet executeSelectHikari(String SQL) {
		ResultSet rs = null;
		try (Connection conn = pool.getConnection()) {

			PreparedStatement voteStmt = conn.prepareStatement(SQL);
			rs = voteStmt.executeQuery();

		} catch (SQLException ex) {
			  
		}
		return rs;
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
	
	public ResultSet ExecuteSELECT(String SQL) throws SQLException  {
				
		ResultSet rs = null;
		
			rs = (ResultSet) statement.executeQuery(SQL);
		
		return rs;
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
		if (conn.isClosed()) {
			ConnectDB();	
			statement = conn.createStatement();
			log.info("conn and statement are closed");
		}
		
		statement.addBatch(SQL);
	}
	
	public int[] executeBatch() throws SQLException  {
		
		int[] result = null;
		
			result = statement.executeBatch();
		
		return result;
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
	
	public int getRoundFCFY() throws SQLException, ClassNotFoundException, ServletException, IOException {
		int round = 0;
		String SQL = "SELECT value FROM apiadbossDB.cv_variables WHERE variable = 'roundFCFY'";
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			round = Integer.parseInt(rs.getString("value"));
			log.info("round: " + round);
	    }
		
		return round;
	}
	
	public void setRound(int round) throws ClassNotFoundException, ServletException, IOException, SQLException {
		String roundString = String.valueOf(round);
		String SQL = "UPDATE apiadbossDB.cv_variables SET value = '" + roundString + "' WHERE variable = 'round'";
		Execute(SQL);
		
	}
	
	public void setRoundFCFY(int roundFCFY) throws ClassNotFoundException, ServletException, IOException, SQLException {
		String roundString = String.valueOf(roundFCFY);
		String SQL = "UPDATE apiadbossDB.cv_variables SET value = '" + roundString + "' WHERE variable = 'roundFCFY'";
		Execute(SQL);
		
	}
	
	public boolean companyIncluded(String Symbol) throws ClassNotFoundException, SQLException, ServletException, IOException {
		boolean result = false;
		String SQL = "SELECT Company FROM apiadbossDB.CompanyValuation WHERE Symbol = '" + Symbol + "'";
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			result = true;
		}
	
		return result;
	}
	
	public String getRatio(String symbol, String concept, String finDate) throws ClassNotFoundException, SQLException, ServletException, IOException {
		String out = "";
		String SQL = "select value from apiadbossDB.adargaConcepts " + 
				"where symbol = '" + symbol + "'" + 
				" and concept = '" + concept + "'" + 
				" and finantialDate = '" + finDate + "'";	
		log.info(SQL);
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			out = rs.getString("value");
		}
		return out;
	}
	
	public String getLastFinDate(String symbol) throws ClassNotFoundException, SQLException, ServletException, IOException {
		String out = "";
		String SQL = "select finantialDate from apiadbossDB.adargaConcepts " + 
				"where symbol = '" + symbol + "'" + 
				" order by finantialDate asc";	
		boolean anyResult = false;
		ResultSet rs = ExecuteSELECT(SQL);
		while (rs.next()) {
			if (!rs.getString("finantialDate").equals("TTM")) {
				out = rs.getString("finantialDate");
			}
			anyResult = true;
		}
		if (!anyResult) {
			out = "ko";
		}
		return out;
	}
	
	public boolean StatementClosed() throws SQLException {
		
		return statement.isClosed();
		
	}

}
