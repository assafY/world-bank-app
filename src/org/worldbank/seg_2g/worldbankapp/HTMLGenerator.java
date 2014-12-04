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

		String html = "";
		
		html = getHTMLBeginning(""+queryCode)
				+ getCountryValueBlock(queryCode, year)
				+ getHTMLEnding();
		
		return html;
	}

	private String getHTMLBeginning(String queryCode) {
		String s = "<html>\n"
				+ "  <head>\n"
				+ "    <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>\n"
				+ "    <script type=\"text/javascript\">\n"
				+ "      google.load(\"visualization\", \"1\", {packages:[\"geochart\"]});\n"
				+ "      google.setOnLoadCallback(drawRegionsMap);\n"
				+ "\n"
				+ "      function drawRegionsMap() {\n"
				+ "\n"
				+ "        var data = google.visualization.arrayToDataTable([\n"
				+ "          ['Country', '" + "CO2" + "'],\n";
		return s;
	}

	private String getHTMLEnding() {
		String s = "        ]);\n"
				+ "\n"
				+ "        var options = {};\n"
				+ "\n"
				+ "        var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));\n"
				+ "\n"
				+ "        chart.draw(data, options);\n"
				+ "      }\n"
				+ "    </script>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <div id=\"regions_div\"></div>\n"
				+ "  </body>\n"
				+ "</html>\n";

		return s;
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
				returnString += getCountryAttributeLine(selectedCountry, value)
						+ "\n";
			}
		}

		return returnString;
	}

	private String getCountryAttributeLine(Country country, double value) {
		return "          ['" + country.getName() + "', " + value + "],";
	}

	private double getAttributeValueForCountry(Country selectedCountry,
			int queryCode, int year) {

		String value = "null";

		try {
			String data = qG.getJSON(selectedCountry, queryCode, year, year);

			JSONArray dataFeed = new JSONArray(data);
			JSONObject json = dataFeed.getJSONArray(1).getJSONObject(0);

			value = json.getString("value");

		} catch (Exception e) {
		}

		if (value == "null")
			return -1;
		else {
			double v = (double) Double.parseDouble(value);
			return v;
		}
	}
}
