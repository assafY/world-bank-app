package org.worldbank.seg_2g.worldbankapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class QueryGenerator {

	//public final static String QUERY_RESULT = "org.worldbank.seg_2g.RESULT";
	
	// list of contstants containing different indicator query codes to use when querying server
	private static final String WB_URL = "http://api.worldbank.org/countries/";
	private static final String POPULATION = "SP.POP.TOTL?";
	private static final String URBAN_RURAL_POPULATION = "SP.URB.TOTL.IN.ZS?";
	private static final String ENERGY_PRODUCTION = "EG.EGY.PROD.KT.OE?";
	private static final String ENERGY_USE = "EG.USE.COMM.KT.OE?";
	private static final String FOSSIL_FUEL = "EG.USE.COMM.FO.ZS?";
	private static final String FOREST_AREA = "AG.LND.FRST.K2?";
	private static final String CO2_EMISSIONS = "EG.USE.COMM.FO.ZS?";
	private static final String CH4_EMISSIONS = "EN.ATM.METH.KT.CE?";
	private static final String GREENHOUSE_GASSES = "EN.ATM.GHGO.KT.CE?";
	private static final String FUEL_PRICE = "SP.RUR.TOTL.ZS?";
	// TODO: Create constants for all queries
	
	private Context context;
	
	private String URL;
	private String indicatorCode;
	
	public QueryGenerator(Context context) {
		this.context = context;
	}
	
	public void setCountryList(ArrayList<Country> countryList) {
		try {
			// put country json in JSONArray
			JSONArray dataFeed = new JSONArray(loadCountriesFromAssets());
			
			// get total number of countries 
			JSONObject titleValues = dataFeed.getJSONObject(0);
			int totalCountries = titleValues.getInt("total");
			
			// create Country object for all countries and add to list and names array
			JSONArray feedArray = dataFeed.getJSONArray(1);
			for (int i = 0; i < totalCountries; ++i) {
				JSONObject json = feedArray.getJSONObject(i);
				Country currentCountry = new Country(json.getString("name"), json.getString("id"), json.getString("iso2Code"), context);
				countryList.add(currentCountry);
			}	
			
		} catch (JSONException e) {
			//TODO: Handle exception
		}
	}
	
	private String loadCountriesFromAssets() {
        String data = null;
        
        try {
        	// load local country json file into InputStream
            InputStream is = context.getAssets().open("countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            
            // convert to string
            data = new String(buffer, "UTF-8");

        } catch (IOException e) {
        	//TODO: Handle exception
            e.printStackTrace();
            return null;
        }
        
        return data;
	}
	
	private void setIndicatorCode(int indicator) {
		
		// set indicator string to the correct code based on constant int passed from main activity (activity does not yet exist)
		switch(indicator) {
			case Settings.POPULATION: 			indicatorCode = POPULATION;
									  			break;
			case Settings.URBAN_RURAL:			indicatorCode = URBAN_RURAL_POPULATION;
												break;
			case Settings.ENERGY_PRODUCTION:	indicatorCode = ENERGY_PRODUCTION;
												break;
			case Settings.ENERGY_USE: 			indicatorCode = ENERGY_USE;
			  									break;
			case Settings.FOSSIL_FUEL:			indicatorCode = FOSSIL_FUEL;
												break;
			case Settings.FOREST_AREA:			indicatorCode = FOREST_AREA;
												break;
			case Settings.CO2_EMISSIONS:		indicatorCode = CO2_EMISSIONS;
												break;
			case Settings.CH4_EMISSIONS: 		indicatorCode = CH4_EMISSIONS;
												break;
			case Settings.GREENHOUSE_GASSES:	indicatorCode = GREENHOUSE_GASSES;
												break;
			case Settings.FUEL_PRICE:			indicatorCode = FUEL_PRICE;
												break;
		}
	}
	
	public String getJSON(Country selectedCountry, int queryCode, int startYear, int endYear) {
		
		// Set the correct indicator
		setIndicatorCode(queryCode);
		// Build URL string
		URL = WB_URL + selectedCountry.getTwoLetterCode() + "/indicators/" + indicatorCode + "date=" + startYear + ":" + endYear + "&format=json";
		try {
			return new getDataFeed().execute().get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO: Handle exception
			e.printStackTrace();
			}
		return null;
	}
	
	// Fetch the data and put it into the app.
	private class getDataFeed extends AsyncTask<String, Void, String> {
		
        @Override
        protected String doInBackground(String... params) {
        	
            // Create download objects
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(URL);
            StringBuilder  content = new StringBuilder();
            try {
                // Execute response and create input stream
                HttpResponse response = client.execute(get);
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(in));
                    // Build string from input stream
                    String readLine = reader.readLine();
                    while (readLine != null) {
                        content.append(readLine);
                        readLine = reader.readLine();
                    }
                } else {
                    content = null;
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            // return data
            return content.toString();
        }

    }
	
}
 