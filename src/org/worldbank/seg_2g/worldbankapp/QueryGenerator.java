package org.worldbank.seg_2g.worldbankapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
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

	// list of contstants containing different indicator query codes to use when
	// querying server
	private static final String WB_URL = "http://api.worldbank.org/countries/";
	
	private static final String POPULATION = "SP.POP.TOTL?";
	private static final String URBAN_RURAL_POPULATION = "SP.URB.TOTL.IN.ZS?";
	private static final String ENERGY_PRODUCTION = "EG.EGY.PROD.KT.OE?";
	private static final String ENERGY_USE = "EG.USE.COMM.KT.OE?";
	private static final String FOSSIL_FUEL = "EG.USE.COMM.FO.ZS?";
	private static final String FOREST_AREA = "AG.LND.FRST.K2?";
	private static final String CO2_EMISSIONS = "EN.ATM.CO2E.KT?";
	private static final String CH4_EMISSIONS = "EN.ATM.METH.KT.CE?";
	private Context context;

	private String URL;
	private String indicatorCode;

	public QueryGenerator(Context context) {
		this.context = context;
	}

	public void setCountryList(final ArrayList<Country> countryList) {

		try {
			// put full country json in JSONArray
			JSONArray dataFeed = new JSONArray(loadCountriesFromAssets());

			// get total number of countries
			JSONObject titleValues = dataFeed.getJSONObject(0);
			int totalCountries = titleValues.getInt("total");

			// extract main country value JSON array from file
			JSONArray feedArray = dataFeed.getJSONArray(1);
			// create Country object for all countries and add to list
			for (int i = 0; i < totalCountries; ++i) {
				JSONObject json = feedArray.getJSONObject(i);
				String country = json.getString("name");
				
				if (json.optDouble("longitude", -1.0) != -1.0) {
					Country currentCountry = new Country(country,
							json.getString("id"), json.getString("iso2Code"),
							context);
					countryList.add(currentCountry);
				}
			}
			// sort the list by country name
			Collections.sort(countryList);

		} catch (JSONException e) {
			// TODO: Handle exception
		}

	}

	private String loadCountriesFromAssets() {
		String data = null;

		try {
			// load local country json file into InputStream
			InputStream dataStream = context.getAssets().open("countries.json");
			int size = dataStream.available();
			byte[] dataBuffer = new byte[size];

			dataStream.read(dataBuffer);
			dataStream.close();

			// convert to string
			data = new String(dataBuffer);

		} catch (IOException e) {
			// TODO: Handle exception
			e.printStackTrace();
			return null;
		}

		return data;
	}

	private void setIndicatorCode(int indicator) {

		// set indicator string to the correct code based on constant sent to getJSON
		switch (indicator) {
		case Settings.POPULATION:
			indicatorCode = POPULATION;
			break;
		case Settings.URBAN_RURAL:
			indicatorCode = URBAN_RURAL_POPULATION;
			break;
		case Settings.ENERGY_PRODUCTION:
			indicatorCode = ENERGY_PRODUCTION;
			break;
		case Settings.ENERGY_USE:
			indicatorCode = ENERGY_USE;
			break;
		case Settings.FOSSIL_FUEL:
			indicatorCode = FOSSIL_FUEL;
			break;
		case Settings.FOREST_AREA:
			indicatorCode = FOREST_AREA;
			break;
		case Settings.CO2_EMISSIONS:
			indicatorCode = CO2_EMISSIONS;
			break;
		case Settings.CH4_EMISSIONS:
			indicatorCode = CH4_EMISSIONS;
		}
	}

	public String getJSON(Country selectedCountry, int queryCode,
			int startYear, int endYear) {

		// Set the correct indicator
		setIndicatorCode(queryCode);
		// Build URL string
		URL = WB_URL + selectedCountry.getTwoLetterCode() + "/indicators/"
				+ indicatorCode + "date=" + startYear + ":" + endYear
				+ "&format=json&per_page=100";
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
			StringBuilder content = new StringBuilder();
			try {
				// Execute response and create input stream
				HttpResponse response = client.execute(get);
				int responseCode = response.getStatusLine().getStatusCode();
				if (responseCode == 200) {
					InputStream in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
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
