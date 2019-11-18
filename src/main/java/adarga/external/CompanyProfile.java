package adarga.external;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.gson.Gson;

import adarga.external.KeyMetrics.Metrics;

public class CompanyProfile {
	private static final Logger log = Logger.getLogger(KeyMetrics.class.getName());
	private static String symbol;
	private static String urlEndpoint = "https://financialmodelingprep.com/api/v3/company/profile/";
	static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();
    
    private static Profile profile = new Profile();
    
    
    public Profile getProfile(String symbol) throws IOException {
    	String urlEndpointComposer = urlEndpoint + symbol + "?datatype=json";
    	HttpRequestFactory requestFactory 
	    = HTTP_TRANSPORT.createRequestFactory(
	      (HttpRequest requestX) -> {
	        requestX.setParser(new JsonObjectParser(JSON_FACTORY));
	    });
		GenericUrl url = new GenericUrl(urlEndpointComposer);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse res = request.execute();
		
		String json = res.parseAsString();
		JSONObject jsonO = new JSONObject(json);
		JSONObject prof = jsonO.getJSONObject("profile");
		Gson gson = new Gson();
		Profile profile = gson.fromJson(prof.toString(), Profile.class);
		return profile;
		
    }
    
    static public class Profile {
		
		@Key("companyName") private String companyName;
		@Key("exchange") private String exchange;
		@Key("industry") private String industry;
		@Key("sector") private String sector;
		@Key("ceo") private String ceo;
		@Key("description") private String description;
		@Key("price") private String price;
		@Key("mktCap") private String mktCap;
		
		
		
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getMktCap() {
			return mktCap;
		}
		public void setMktCap(String mktCap) {
			this.mktCap = mktCap;
		}
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public String getExchange() {
			return exchange;
		}
		public void setExchange(String exchange) {
			this.exchange = exchange;
		}
		public String getIndustry() {
			return industry;
		}
		public void setIndustry(String industry) {
			this.industry = industry;
		}
		public String getSector() {
			return sector;
		}
		public void setSector(String sector) {
			this.sector = sector;
		}
		public String getCeo() {
			return ceo;
		}
		public void setCeo(String ceo) {
			this.ceo = ceo;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		
		
		
    }
    
	public static void main(String[] args) {
		

	}

}
