package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import android.content.Context;

public class HTMLGenerator {

	DataBank db;
	
	public HTMLGenerator(Context context) {
		db = new DataBank(context);
	}

	public String getHTMLCode(int queryCode, int year) {

		// TODO check for internet connection

		String html = "";

		html = getHTMLBeginning("" + queryCode)
				+ getCountryValueBlock(queryCode, year) + getHTMLEnding();

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
				+ "        var options = {\n"
				+ "          colorAxis: {colors: ['green', 'red']}\n"
				+ "};\n"
				+ "\n"
				+ "        var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));\n"
				+ "\n" + "        chart.draw(data, options);\n" + "      }\n"
				+ "    </script>\n" + "  </head>\n" + "  <body>\n"
				+ "    <div id=\"regions_div\"></div>\n" + "  </body>\n"
				+ "</html>\n";

		return s;
	}

	private String getCountryValueBlock(int queryCode, int year) {

		String returnString = "";
		ArrayList<Country> arrl = db.getCountryArrayList();
		for (int i = 0; i < arrl.size(); i++) {
			Country country = arrl.get(i);
			double value = db.getValuesFor(country, queryCode, year);
			returnString += getCountryAttributeLine(country, value);
		}
		
		return returnString;
	}

	private String getCountryAttributeLine(Country country, double value) {
		return "          ['" + country.getName() + "', " + value + "],\n";
	}

}
