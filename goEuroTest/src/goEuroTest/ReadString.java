package goEuroTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFileChooser;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.*;


/**
 * @author juancortes
 *
 */
public class ReadString {
	private final static String USER_AGENT = "Mozilla/5.0";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		ReadString http = new ReadString();
	      //  prompt the user to enter their name
		 String cityName = null;
	      System.out.print("Enter a city: ");
	      if(args.length > 0){
	    	   cityName = new String(args[0]);
	      }else{
	    	  
	    	  //  open up standard input
		      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		 
		      //  read the city from the command-line; need to use try/catch with the
		      //  readLine() method
		      try {
		    	  cityName = br.readLine();
		      } catch (IOException ioe) {
		         System.out.println("IO error trying to read a city!");
		         System.exit(1);
		      }
		 
		      System.out.println("Thanks for the city, " + cityName);
	      }
	      
	 
	     
	      if(null != cityName){
	   	  
	    	  http.sendGet(cityName);
	    	  
	      }
	 
	   }

	

	//HTTP GET request
	private void sendGet(String city) throws Exception {

		String url = "http://api.goeuro.com/api/v2/position/suggest/en/" + city;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		if(responseCode == 200){
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		 
		 while ((inputLine= in.readLine()) != null) {

			 JSONArray docs= new JSONArray(inputLine);

			   for(int i=0; i<docs.length();i++){
			       JSONObject geo_pos =  (JSONObject)(docs.getJSONObject(i).getJSONObject("geo_position"));
			       docs.getJSONObject(i).put("latitude", geo_pos.get("latitude"));
			       docs.getJSONObject(i).put("longitude", geo_pos.get("longitude"));			       
			       docs.getJSONObject(i).remove("geo_position");
			       docs.getJSONObject(i).remove("inEurope");
			       docs.getJSONObject(i).remove("locationId");
			       docs.getJSONObject(i).remove("countryCode");
			       docs.getJSONObject(i).remove("iata_airport_code");
			       docs.getJSONObject(i).remove("country");
			       docs.getJSONObject(i).remove("coreCountry");
			       docs.getJSONObject(i).remove("distance");
			       docs.getJSONObject(i).remove("fullName");
			       docs.getJSONObject(i).remove("key");
			   }    
			   //Choose directory
			   JFileChooser chooser = new JFileChooser();
			    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			    int option = chooser.showSaveDialog(null);
			    if (option == JFileChooser.APPROVE_OPTION)
			    {
			    	String csv = CDL.toString(docs);
					   System.out.println(csv);			   			
					   FileUtils.writeStringToFile(chooser.getSelectedFile(), csv);
			    }
			 }
		 
		  in.close();		
		}else{
			System.out.println("No data");
		}
	}
}
