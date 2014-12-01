package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class HTMLGenerator {

	QueryGenerator qG;

	public HTMLGenerator(Context context) {
		qG = new QueryGenerator(context);
	}

	public String getHTMLCode(int queryCode, int year) {

		// TODO check for internet connection

		return getCountryValueBlock(queryCode, year);
	}

	private String getCountryValueBlock(int queryCode, int year) {

		ArrayList<Country> arrlCountries = new ArrayList<Country>();
		qG.setCountryList(arrlCountries);
		String returnString = "";

		for (int i = 0; i < arrlCountries.size(); i++) {
			Country selectedCountry = arrlCountries.get(i);
			double value = getAttributeValueForCountry(selectedCountry,
					queryCode, year);
			if (value > -1) {
				returnString += getCountryAttributeLine(selectedCountry, value) + "\n";
			}
		}

		return returnString;
	}

	private String getCountryAttributeLine(Country country, double value) {
		return "          ['" + country.getName() + "', " + value + "],";
	}

	private double getAttributeValueForCountry(Country selectedCountry,
			int queryCode, int year) {

		String value = null;

		try {
			String data = qG.getJSON(selectedCountry, queryCode, year, year);

			JSONArray dataFeed = new JSONArray(data);
			JSONObject json = dataFeed.getJSONArray(1).getJSONObject(0);

			value = json.getString("value");

		} catch (Exception e) {
		}

		return (value != null) ? (double) Double.parseDouble(value) : null;
	}
}
