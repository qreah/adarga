package adarga.getinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;


public class Item {
	private static final Logger log = Logger.getLogger(Item.class.getName());
	JSONObject dataJSON = new JSONObject();
	List<String> years = new ArrayList<String>();
	String name;
	String TTM;
	
	public Item setZero(int numYear, int lastYear) {
		name = "Zero";
		TTM = null;
		for (int i = 0; i < numYear; i++) {
			int yeari = lastYear - i;
			dataJSON.put(Integer.toString(yeari), 0);
			years.add(Integer.toString(yeari));
			
		}
		sortYears();
		return this;
	}
			
	
	public void setValues(JSONObject json) {
		
		Iterator<String> iter = json.keys();
		years = new ArrayList<String>();
		String keyShort;
		while (iter.hasNext()) {
			String key = iter.next();
			if (key.length() < 4) {
				TTM = key;
			} else {
				keyShort = key.substring(0, 4);
				
				if (json.get(key).equals("")) {
					dataJSON.put(keyShort, 0);
				} else {
					
					//dataJSON.put(keyShort, Double.parseDouble((String) json.get(key)));
					dataJSON.put(keyShort, json.get(key));
				}
				
				years.add(keyShort);
			}
		}
		
		sortYears();
		
	}
	
	public void sortYears() {
		Collections.sort(years, new Comparator<String>() {
	        public int compare(String o1, String o2) {
	            return extractInt(o1) - extractInt(o2);
	        }

	        int extractInt(String s) {
	            String num = s.replaceAll("\\D", "");
	            return num.isEmpty() ? 0 : Integer.parseInt(num);
	        }
	    });
	}
	
	public int lastYear() {
		int last = years.size()-1;
		
		return Integer.parseInt(years.get(last));
	}
	
	public int size() {
		return dataJSON.length();
	}
	
		
	public Double getValue(int year) {
		return dataJSON.getDouble(String.valueOf(year));
	}
	
	public void setValue(int year, Double value) {
		dataJSON.put(String.valueOf(year), value);
		years.add(String.valueOf(year));
	}
	
	// It supposes that the JSON objects are sorted when values are setted
	
	public Item sum(Item item) {
		Item newItem = new Item();
		int itemSize = item.size();
		int size = this.size();
		log.info("SUM itemSize: " + item.lastYear());
		log.info("SUM size: " + lastYear());
		if (itemSize == size) {
			Iterator<String> iter = years.iterator();
			
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				Double operation = item.getValue(year) + this.getValue(year);
				newItem.setValue(year, operation);
				
			}
			return newItem;
		} else {
			log.info("Item | Items size are different in sum operation");
			return null;
		}
	}
	
	public Item sumNumber(Double number) {
		Item newItem = new Item();
		int size = this.size();
			Iterator<String> iter = years.iterator();
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				Double operation = number + this.getValue(year);
				newItem.setValue(year, operation);
			}
			return newItem;	
	}
	
	
	public Item substractNumberAnte(Double number) {
		Item newItem = new Item();
		int size = this.size();
		
		Iterator<String> iter = years.iterator();
		while (iter.hasNext()) {
			int year = Integer.parseInt(iter.next());
			Double operation = number - this.getValue(year);
			newItem.setValue(year, operation);
		}
		return newItem;	
	}
	
	public Item substractNumberPost(Double number) {
		Item newItem = new Item();
		int size = this.size();
			Iterator<String> iter = years.iterator();
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				Double operation = this.getValue(year) - number;
				newItem.setValue(year, operation);
			}
			return newItem;	
	}
	
	public Item substract(Item item) {
		Item newItem = new Item();
		int itemSize = item.size();
		int size = this.size();
		if (itemSize == size) {
			Iterator<String> iter = years.iterator();
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				Double operation = this.getValue(year) - item.getValue(year);
				newItem.setValue(year, operation);
				
			}
			return newItem;
		} else {
			log.info("Item | Items size are different in sum operation");
			return null;
		}
	}
	
	public Item multiplyNumber(Double number) {
		Item newItem = new Item();
		int size = this.size();
			Iterator<String> iter = years.iterator();
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				Double operation = number * this.getValue(year);
				newItem.setValue(year, operation);
			}
			return newItem;	
	}
	
	public Item multiply(Item item) {
		Item newItem = new Item();
		int itemSize = item.size();
		
		int size = this.size();
		int delta = delta(lastYear(), item.lastYear());
		
		if (itemSize == size) {
			
			Iterator<String> iter = years.iterator();
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				
				Double operation = item.getValue(year - delta) * this.getValue(year);
				newItem.setValue(year, operation);
				
			}
			return newItem;
		} else {
			log.info("Item | Items size are different in sum operation");
			return null;
		}	
	}
	
	public Item divideNumber(Double number) {
		Item newItem = new Item();
		int size = this.size();
			Iterator<String> iter = years.iterator();
			Double operation;
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				if (number != 0.0) {
					operation = this.getValue(year) / number;
				} else {
					operation = 9999999999999999999999999999.9;
				}
				
				newItem.setValue(year, operation);
			}
			return newItem;	
	}
	
	public Item divide(Item item) {
		Item newItem = new Item();
		int itemSize = item.size();
		int size = this.size();
		int delta= item.lastYear() - lastYear();
		JSONObject json = new JSONObject();
		
		if (itemSize == size) {
			
			Iterator<String> iter = years.iterator();
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				Double operation = this.getValue(year) / item.getValue(year + delta);
				newItem.setValue(year, operation);
				
			}
			return newItem;
		} else {
			log.info("Item | Items size are different in divide operation");
			return null;
		}	
	}
	
	
	public Item change(Item item) {
		Item newItem = new Item();
		int itemSize = item.size();
		int size = this.size();
		if (itemSize == size) {
			
			Iterator<String> iter = years.iterator();
			while (iter.hasNext()) {
				int year = Integer.parseInt(iter.next());
				Double operation = (this.getValue(year) - item.getValue(year)) / item.getValue(year);
				newItem.setValue(year, operation);
				
			}
			return newItem;
		} else {
			log.info("Item | Items size are different in divide operation");
			return null;
		}	
	}
	
	public Item changeInItem() {
		Item newItem = new Item();
		
		int size = this.size();
		int firstYear = this.lastYear() - size + 1;
		Double temp = 0.0;
		Double operation;
		
		Iterator<String> iter = years.iterator();
		while (iter.hasNext()) {
			int year = Integer.parseInt(iter.next());
			if (year == firstYear) {
				
				newItem.setValue(year, 0.0);
				temp = this.getValue(year);
			} else {
				if (temp == 0.0) {
					operation = 99999999999999999999999999999.9;
					newItem.setValue(year, operation);
					temp = this.getValue(year);
				} else {
					operation = (this.getValue(year) - temp) / temp;
					newItem.setValue(year, operation);
					temp = this.getValue(year);
				}
				
			}
				
		}
		return newItem;
			
	}
	
	 public int delta(int year, int yearItem) {
		return year  - yearItem;
		 
	 }
	
	 public JSONObject toJSON() {
			return dataJSON;
		}
	
	public String toString() {
		return dataJSON.toString();
	}
	
}