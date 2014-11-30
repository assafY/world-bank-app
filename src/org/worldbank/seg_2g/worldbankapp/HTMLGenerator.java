package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class HTMLGenerator {
	
	Context context;

	public HTMLGenerator(Context context) {
		this.context = context;
	}
	public void lala() {

		//TODO check for internet connection
				
		QueryGenerator qG = new QueryGenerator(context);
		ArrayList<Country> arrlCountries = new ArrayList<Country>();
		qG.setCountryList(arrlCountries);
		Country selectedCountry = arrlCountries.get(152); //146RO-152null
		int queryCode = Settings.CO2_EMISSIONS;
		int year = 1994;
		try {
			String data = qG.getJSON(selectedCountry, queryCode, year, year);

			JSONArray dataFeed = new JSONArray(data);
			JSONObject json = dataFeed.getJSONArray(1).getJSONObject(0);
			
			String value = json.getString("value");
			System.out.println(value);

		} catch (Exception e) {
		}

	}
}
