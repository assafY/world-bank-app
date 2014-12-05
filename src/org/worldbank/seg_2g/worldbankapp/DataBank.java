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

	public static void fetchData(int queryCode) {
		int i = queryCode - 1;
		if (!fetched[i]) {
			for (int j = 0; j < countriesNo; j++) {
				dataSheet[i][j] = fetchValuesFor(arrlCountries.get(j),
						i + 1);
			}
			fetched[i] = true;
		}
	}

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
					// System.out.println(date + ": " + value);
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
				// System.out.println(i + ": " + v + " (" + (i + startYear) +
				// ")");
			} catch (Exception e) {
				values[i] = -1;
			}
		}

		return values;
	}

	public static double getValuesFor(Country country, int queryCode, int year) {
		return dataSheet[queryCode - 1][arrlCountries.indexOf(country)][year
				- startYear];
	}

	public static ArrayList<Country> getCountryArrayList() {
		return arrlCountries;
	}
}
