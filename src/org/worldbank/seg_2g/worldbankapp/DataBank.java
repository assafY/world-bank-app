package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class DataBank {

	private static final int startYear = 1961, endYear = 2014;
	private static QueryGenerator qG;
	private static ArrayList<Country> arrlCountries = new ArrayList<Country>();
	private static int attributesNo;
	private static int countriesNo;
	private static int yearsNo;

	private static boolean reinit = false, fetched[];

	private static double[][][] dataSheet;

	/**
	 * Initialisation method for the DataBank. Must be called before any other
	 * operation. Contains already called check.
	 * 
	 * @param context
	 *            the activity context
	 */
	public static void initialise(Context context) {
		if (!reinit) {
			qG = new QueryGenerator(context);
			qG.setCountryList(arrlCountries);
			attributesNo = Settings.NUMBER_OF_ATTRIBUTES;
			countriesNo = arrlCountries.size();
			yearsNo = 54;
			dataSheet = new double[attributesNo][countriesNo][yearsNo];
			fetched = new boolean[attributesNo];
			for (int i = 0; i < attributesNo; i++) {
				fetched[i] = false;
			}
			reinit = true;
		}
	}

	/**
	 * Retrieves data for every attribute for year span.
	 */
	public static void fetchAllData() {
		for (int i = 0; i < attributesNo; i++) {
			if (!fetched[i]) {
				for (int j = 0; j < countriesNo; j++) {
					dataSheet[i][j] = fetchValuesFor(arrlCountries.get(j),
							i + 1);
				}
				fetched[i] = true;
			}
		}
	}

	/**
	 * Retrieves data for specified attribute
	 * 
	 * @param queryCode
	 *            attribute id
	 */
	public static void fetchData(int queryCode) {
		int i = queryCode - 1;
		if (!fetched[i]) {
			for (int j = 0; j < countriesNo; j++) {
				dataSheet[i][j] = fetchValuesFor(arrlCountries.get(j), i + 1);
			}
			fetched[i] = true;
		}
	}

	// fills in the array with one country line
	private static double[] fetchValuesFor(Country selectedCountry,
			int queryCode) {

		String value = "nop";
		int date = 0;
		String[] valuess = new String[yearsNo];

		try {
			String data = qG.getJSON(selectedCountry, queryCode, startYear,
					endYear);

			JSONArray dataFeed = new JSONArray(data);

			for (int i = 0; i > -1; i++) {
				try {
					JSONObject json = dataFeed.getJSONArray(1).getJSONObject(i);
					value = json.getString("value");
					date = json.getInt("date");
					valuess[date - startYear] = value;
				} catch (Exception e) {
					i = -100;
				}
			}

		} catch (Exception e) {
		}

		double[] values = new double[yearsNo];

		for (int i = 0; i < yearsNo; i++) {
			try {
				double v = (double) Double.parseDouble(valuess[i]);
				values[i] = v;
			} catch (Exception e) {
				values[i] = -1;
			}
		}

		return values;
	}

	/**
	 * Returns the value for the specific country attribute and year from the
	 * saved data.
	 * 
	 * @param country
	 *            country
	 * @param queryCode
	 *            attribute id
	 * @param year
	 *            year
	 * @return value of attribute for country in year
	 */
	public static double getValueFor(Country country, int queryCode, int year) {
		return dataSheet[queryCode - 1][arrlCountries.indexOf(country)][year
				- startYear];
	}

	/**
	 * Returns an {@link ArrayList} of all {@link Country} objects.
	 * 
	 * @return ArrayList of countries
	 */
	public static ArrayList<Country> getCountryArrayList() {
		return arrlCountries;
	}
}
