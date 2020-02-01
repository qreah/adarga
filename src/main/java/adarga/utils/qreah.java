package adarga.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;


/*
 *  The goal of the Class is to provide some usefull methods of general purpose
 */ 

public class qreah {

	private static final Logger log = Logger.getLogger(qreah.class.getName());
	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = new JacksonFactory();
    
	public qreah() {
		
	}
	
	public String HTTP(String url) throws IOException {
		HttpRequestFactory requestFactory 
        = HTTP_TRANSPORT.createRequestFactory(
          (HttpRequest requestX) -> {
            requestX.setParser(new JsonObjectParser(JSON_FACTORY));
        });
		System.out.println(url);
		GenericUrl urlT = new GenericUrl(url);
		HttpRequest request = requestFactory.buildGetRequest(urlT);
		
		HttpResponse response = request.execute();
		String res = response.parseAsString();
		return res;
	}
	
	public String HTTP(String url, GoogleCredential credential) throws IOException {
		
		HttpRequestFactory requestFactory 
        = HTTP_TRANSPORT.createRequestFactory(credential);
		
		System.out.println(url);
		GenericUrl urlT = new GenericUrl(url);
		
		HttpRequest request = requestFactory.buildGetRequest(urlT);
		request.setRequestMethod("PUT");
		
		long len = 0;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength((long) 0);
		//headers.set("Content-Length", len);
		
		request.setHeaders(headers);
		HttpResponse response = request.execute();
		String res = response.parseAsString();
		return res;
	}
	
	/*
	 *   Goal: to send email from development project account (qhrear@gmail.com)
	 *   Input:
	 *   - String destinatario: email address you want to send email to
	 *   - String asunto: subject of the email
	 *   - String cuerpo: body of the email to be sent 
	 *   Output:
	 *   - None (an email is sent and can be seen in the development project account mailbox
	 */
	
	public void enviarMail(String destinatario, String asunto, String cuerpo) {
	    
	    //String remitente = "qhrear@gmail.com"; 
	    String remitente = "operations@adboss.io";
	    String clave = "FAYO0173"; //Para la dirección nomcuenta@gmail.com

	    Properties props = System.getProperties();
	    	    
	    props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
	    props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
	    props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
	    props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google
	    props.put("mail.debug.auth", "true");
	    
	    Session session = Session.getInstance(props, new javax.mail.Authenticator() {

	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(remitente,clave);
	        }
	    });
	    
	    MimeMessage message = new MimeMessage(session);

	    try {
	        message.setFrom(new InternetAddress(remitente)); 
	        message.addRecipients(Message.RecipientType.TO, destinatario);//Se podrían añadir varios de la misma manera
	        message.setSubject(asunto);
	        message.setContent(cuerpo, "text/html");
	        Transport transport = session.getTransport("smtp");
	        transport.connect("smtp.gmail.com", remitente, clave);	        
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
	    }
	    catch (MessagingException me) {
	        me.printStackTrace();   
	    }
	}
	
	
	/*
	 *   Goal: to provide the actual hour from the Calendar
	 *   Input:
	 *   	- None
	 *   Output:
	 *   	- Hour at execution time
	 */
	
	public int hora() {
		
		Calendar calendario = new GregorianCalendar();
		int hora;
		hora =calendario.get(Calendar.HOUR_OF_DAY);
		calendario.get(Calendar.MINUTE);
		calendario.get(Calendar.SECOND);
		return hora;
	}
	
	/*
	 *   Goal: to provide the actual minute from the Calendar
	 *   Input:
	 *   	- None
	 *   Output:
	 *   	- Minute at execution time
	 */
	
	public int minutos() {
		
		Calendar calendario = new GregorianCalendar();
		int minutos;
		calendario.get(Calendar.HOUR_OF_DAY);
		minutos = calendario.get(Calendar.MINUTE);
		calendario.get(Calendar.SECOND);
		return minutos;
	}

	
	/*
	 *   Goal: to provide the actual second from the Calendar
	 *   Input:
	 *   	- None
	 *   Output:
	 *   	- Second at execution time
	 */
	
	public int segundos() {
	
	Calendar calendario = new GregorianCalendar();
	int segundos;
	calendario.get(Calendar.HOUR_OF_DAY);
	calendario.get(Calendar.MINUTE);
	segundos = calendario.get(Calendar.SECOND);
	return segundos;
	}
	
	public long getTimestamp() {
		Calendar cal = new GregorianCalendar();
		long date = cal.getTimeInMillis();
		return date;
	}
	
	public List<String> getDatesNowAndLessXDays(int days){
		List<String> dates = new ArrayList<String>();
		Date date = new java.util.Date();
		TimeZone tz = TimeZone.getTimeZone("Europe/Madrid");
		Calendar calendar = Calendar.getInstance(tz);
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		Date date2 = calendar.getTime();
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'").format(date);
		String timeStamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'").format(date2);
		dates.add(timeStamp);
		dates.add(timeStamp2);
		
		return dates;
	}
	
	public List<String> getDateRange(String start, String end) throws ParseException {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = df.parse(start); 
		Date endDate = df.parse(end);
		
        List<String> ret = new ArrayList<String>();
        Date tmp = startDate;
        while(tmp.before(endDate) || tmp.equals(endDate)) {
        	String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(tmp);
            ret.add(timeStamp);
            long ltime=tmp.getTime()+1*24*60*60*1000;
            tmp = new Date(ltime);
        }
        log.info(ret.toString());
        return ret;
    }
	
	
	
	/**
	 * Get the day of the month in String type
	 * 
	 * @param date
	 * @return String with the date of the month
	 */
	
	public String day(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return String.valueOf(day);
	}
	
	public String today() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		date = date.substring(0, 10);
		date = date.replace("/", "-");
		return date;
	}
	
	public String todayLess(int days) {
		String dateS = null;
		Date date = new java.util.Date();
		TimeZone tz = TimeZone.getTimeZone("Europe/Madrid");
		Calendar calendar = Calendar.getInstance(tz);
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date date2 = calendar.getTime();
		String timeStamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'").format(date2);
		dateS = timeStamp2.substring(0, 10);
		return dateS;
	}
	
	/**
	 * Get the month of the date in String type
	 * 
	 * @param date
	 * @return String with the month of the year
	 */
	
	public String month(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		return String.valueOf(month);
	}
	
	/**
	 * Get the year of the date in String type
	 * 
	 * @param date
	 * @return String with the year
	 */
	
	public String year(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return String.valueOf(year);
	}
	
	
	
	
	/*  Goal: to provide the actual time(Hour, Minutes, Seconds) from the Calendar
	 *   Input:
	 *   	- None
	 *   Output:   	
	 *   - hh:mm:ss at execution time
	 */ 	
	
	public String horaTotal() {
		
		Calendar calendario = new GregorianCalendar();
		int hora, minutos, segundos;
		hora = calendario.get(Calendar.HOUR_OF_DAY);
		minutos = calendario.get(Calendar.MINUTE);
		segundos = calendario.get(Calendar.SECOND);
		return hora + ":" + segundos + ":" + minutos;
	}
	
	
	
	public List<String> get30LastDays() {
		List<String> result = new ArrayList<String>();
		List<String> dates = getDatesNowAndLessXDays(-2);
		List<String> dates2 = getDatesNowAndLessXDays(-31);
		String endTime = dates.get(1).substring(0, 10);
		String startTime = dates2.get(1).substring(0, 10);
		result.add(startTime);
		result.add(endTime);
		return result;
	
	}
	
	
	
	public JsonArray toJsonArray(ResultSet rs) throws SQLException {
		JsonObject jObj = new JsonObject();
		JsonArray dao = new JsonArray();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
		        String columnName = rsmd.getColumnName(i);
		        String columnValue = rs.getString(i);
		        jObj.addProperty(columnName, columnValue);
		    }
			dao.add(jObj); 
			
		}
		return dao;
	}
	
	
	
	
	
	/**
	 * Translate an ArrayList to JsonArray. Used when you want to send
	 * the object to the Front End
	 * @param array: object from Array<String>
	 * @return JsonArray: [{'id': 'xxxx'}, {'id': 'yyyy'}, {'id': 'zzzz'}}
	 */
	
	public JsonArray toJsonArray(ArrayList<String> array) {

		JsonArray dao = new JsonArray();
		Iterator<String> iter = array.iterator();
		int i = 0;
		while (iter.hasNext()) {
			i++;
			String idPage = iter.next();
			JsonObject jObj = new JsonObject();
			jObj.addProperty("Id", idPage);
			dao.add(jObj); 
			
		}
		return dao;
	}
	
	public void  addProperty(JsonObject jObj, Object Obj, String name) {
		if (Obj!=null) {
			jObj.addProperty(name, Obj.toString());
		} else {
			jObj.addProperty(name, "");
		}
	}
	
		
	
	
	public void  addProperty(JsonObject jObj, Object Obj, String name, int value) {
		if (Obj!=null) {
			jObj.addProperty(name, value);
		} else {
			jObj.addProperty(name, "");
		}
	}
	
	/**
	 * Transform the body content from a POST request in JSON format to an JSONOBject
	 * @param POST request
	 * @return the JSONObject that defines the JSON object sent
	 */
	
	public JSONObject getContentFromPost(HttpServletRequest request) {
		
		StringBuilder jsonBuff = new StringBuilder();
		String line = null;
		try {
		    BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null)
		        jsonBuff.append(line);
		} catch (Exception e) { /*error*/ }
		
		JSONObject json = new JSONObject(jsonBuff.toString());
		return json;
		
	}
	
	/**
	 * To get a post message with utf8 format
	 * @throws IOException 
	 */
	
	/*
	 *  Important: you have to use the method before any request.getParameter(whatever);
	 */
	
	
	public String postUTF8Message(HttpServletRequest request) throws IOException {
		
		StringBuilder buffer = new StringBuilder();
		InputStream inputStream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream , StandardCharsets.UTF_8));
	    String line;
	    
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    log.info(buffer.toString());
	    return buffer.toString();
	}
	
	
	
	public void writeFile(String content) {
		FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter("pruebaZ.txt");
            pw = new PrintWriter(fichero);
            pw.println(content);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
	}
	
	
	
public static void main (String [ ] args) throws MessagingException, ParseException {
	/*
	qreah q = new qreah();
	List<String> days = q.get30LastDays();
	System.out.println(days.get(0));
	System.out.println(days.get(1));
	*/
	qreah q = new qreah();
	String start = "2019-10-01";
	String end = "2019-10-10";
	List<String> days = q.getDateRange(start, end);
	int len = days.size();
	for (int i=0; i < len; i++) {
		System.out.println(days.get(i));
	}
	
	
}



}
