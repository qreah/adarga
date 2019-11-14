package adarga.getinfo;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;

import org.json.JSONArray;
import org.json.JSONObject; 


public class Financials {
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    JSONObject json = new JSONObject();
    
	public static class Result {
		@Key public int totalrows;
		@Key public List<Row> rows;
	  }
	
	public static class Row {
		@Key public int rownum;
		@Key public List<Values> values;
	  }
	
	public static class Values {
		@Key public JSONObject json;
	  }

	
	public static class BalanceSheetSeries {
		JSONArray bsSeries = new JSONArray();
		
		public void putYear(BalanceSheet bs) {
			this.bsSeries.put(bs);
		}
		public String toString(int index) {
			String result = "";
			result = bsSeries.get(index).toString();
			return result;
		}
	}
	
	
	public static class BalanceSheet {
	    @Key public Long accountspayableandaccruedexpenses;
	    @Key public Long cashandcashequivalents;
	    @Key public Long cashcashequivalentsandshortterminvestments;	 
	    @Key public Long commonstock;	 
	    @Key public Long goodwill;
	    @Key public Long intangibleassets;
	    @Key public Long inventoriesnet;
	    @Key public Long longtermdeferredincometaxliabilities;
	    @Key public Long longtermdeferredliabilitycharges;
	    @Key public Long longterminvestments;
	    @Key public Long otheraccumulatedcomprehensiveincome;
	    @Key public Long otherassets;
	    @Key public Long othercurrentassets;
	    @Key public Long othercurrentliabilities;
	    @Key public Long otherliabilities;
	    @Key public Long propertyplantequipmentnet;
	    @Key public Long retainedearnings;
	    @Key public Long totalassets;
	    @Key public Long totalcurrentassets;
	    @Key public Long totalcurrentliabilities;
	    @Key public Long totalliabilities;
	    @Key public Long totallongtermdebt;
	    @Key public Long totalreceivablesnet;
	    @Key public Long totalshorttermdebt;
	    @Key public Long totalstockholdersequity;
	    
	    public String toString() {
			return "accountspayableandaccruedexpenses: " + accountspayableandaccruedexpenses;
	    	
	    }
	    
	  }

	  public static class EdgarOnlineUrl extends GenericUrl {

		  @Key
		  public String fields;
		  
		  public EdgarOnlineUrl(String encodedUrl) {
		    super(encodedUrl);
		   
		  }

	  }
	  
	  public static void runBalanceSheet() throws IOException {
			HttpRequestFactory requestFactory 
		    = HTTP_TRANSPORT.createRequestFactory(
		      (HttpRequest requestX) -> {
		        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
		    });
			EdgarOnlineUrl url = new EdgarOnlineUrl("http://datafied.api.edgar-online.com/v2/corefinancials/ann?primarysymbols=msft&fields=BalanceSheetConsolidated&appkey=b4s2w5jtbvfjsw8vmdbypg34");
		    
			HttpRequest request = requestFactory.buildGetRequest(url);
			Result BSSeries = request.execute().parseAs(Result.class);
						
			String responseString = request.execute().parseAsString();
			JSONObject response = new JSONObject(responseString);
			JSONObject result = response.getJSONObject("result");
			JSONArray rows = result.getJSONArray("rows");
			BalanceSheetSeries bsSeries = new BalanceSheetSeries();
			int numRows = rows.length();
			for (int i = 0; i < numRows; i++) {
				JSONObject rowsI = rows.getJSONObject(i);
				JSONArray values = rowsI.getJSONArray("values");
				
				JSONObject newValues = new JSONObject();
				int numValues = values.length();
				
				for (int j = 0; j < numValues; j++) {
					JSONObject item = new JSONObject(values.get(j).toString());
					newValues.put(item.getString("field"), item.getLong("value"));
					
				}
				BalanceSheet bs = new BalanceSheet();
				bs.accountspayableandaccruedexpenses = newValues.getLong("accountspayableandaccruedexpenses");
				bs.cashandcashequivalents = newValues.getLong("cashandcashequivalents");
			    bs.cashcashequivalentsandshortterminvestments = newValues.getLong("cashcashequivalentsandshortterminvestments");	 
			    bs.commonstock = newValues.getLong("commonstock");	 
			    bs.goodwill = newValues.getLong("goodwill");	
			    bs.intangibleassets = newValues.getLong("intangibleassets");	
			    bs.inventoriesnet = newValues.getLong("inventoriesnet");	
			    bs.longtermdeferredincometaxliabilities = newValues.getLong("longtermdeferredincometaxliabilities");
			    bs.longtermdeferredliabilitycharges = newValues.getLong("longtermdeferredliabilitycharges");
			    bs.longterminvestments = newValues.getLong("longterminvestments");
			    bs.otheraccumulatedcomprehensiveincome = newValues.getLong("otheraccumulatedcomprehensiveincome");	
			    bs.otherassets = newValues.getLong("otherassets");
			    bs.othercurrentassets = newValues.getLong("othercurrentassets");
			    bs.othercurrentliabilities = newValues.getLong("othercurrentliabilities");
			    bs.otherliabilities = newValues.getLong("otherliabilities");
			    bs.propertyplantequipmentnet = newValues.getLong("propertyplantequipmentnet");
			    bs.retainedearnings = newValues.getLong("retainedearnings");
			    bs.totalassets = newValues.getLong("totalassets");
			    bs.totalcurrentassets = newValues.getLong("totalcurrentassets");
			    bs.totalcurrentliabilities = newValues.getLong("totalcurrentliabilities");
			    bs.totalliabilities = newValues.getLong("totalliabilities");
			    bs.totallongtermdebt = newValues.getLong("totallongtermdebt");
			    bs.totalreceivablesnet = newValues.getLong("totalreceivablesnet");
			    bs.totalshorttermdebt = newValues.getLong("totalshorttermdebt");
			    bs.totalstockholdersequity  = newValues.getLong("totalstockholdersequity");
			    
			    bsSeries.putYear(bs);
			    
			}
			
		}
	  
	  public static void main(String[] args) throws IOException {
		    try {
		        try {
		        	runBalanceSheet();
		          return;
		        } catch (HttpResponseException e) {
		          System.err.println(e.getMessage());
		        }
		      } catch (Throwable t) {
		        t.printStackTrace();
		      }
		      System.exit(1);
		     
	  }
}



  