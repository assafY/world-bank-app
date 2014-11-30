package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GraphFragment extends Fragment {

	// TEMP class, once the action bar works properly it will be removed.
	// private GraphViewSeries populationEntries;
	private static RelativeLayout graphLayout;

	private String countryName;
	private String data;
	private String comparisonData;
	private GraphActivity context;

	private JSONArray dataFeed;
	private JSONObject titleValues;
	private JSONArray feedArray;

	private int totalEntries;
	private LineChartView graph;
	private List<PointValue> values;
	private List<PointValue> invisiblePercentGraphValues;

	private int jsonCounter;
	private List<AxisValue> axisValues;
	private String measureLabel;

	private int value;
	private float percentValue;
	private int year;
	private int roundedValue;
	
	private Line invisiblePercentGraphLine;
	private Line mainGraphLine;
	private Line comparisonGraphLine;
	private List<Line> graphLines;
	
	
	// Inflate the layout
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = (RelativeLayout) inflater.inflate(
				R.layout.fragment_graphs, container, false);
		graphLayout = (RelativeLayout) fragmentView.findViewById(R.id.Graphs);
		return fragmentView;
	}

	protected void createGraph(GraphActivity context, String JSONdata,
			String countryName) {
		this.countryName = countryName;
		this.data = JSONdata;
		this.context = context;

		createLinearGraph();
	}

	protected void createGraph(GraphActivity context, String JSONdata,
			String comparisonData, String countryName) {
		this.countryName = countryName;
		this.data = JSONdata;
		this.comparisonData = comparisonData;
		this.context = context;

		// createCompsrisonLinearGraph();
	}

	private void createLinearGraph() {

		// if data is null, there is no network (second check for tablets)
		if (data == null) {
			Toast.makeText(context, GraphActivity.NO_NETWORK_TEXT,
					Toast.LENGTH_LONG).show();
		} else {
			try {
				dataFeed = new JSONArray(data);
				titleValues = dataFeed.getJSONObject(0);

				// get total number of entries
				totalEntries = titleValues.getInt("total");
				graph = new LineChartView(context);
				values = new ArrayList<PointValue>();

				feedArray = dataFeed.getJSONArray(1);

				jsonCounter = totalEntries - 1;
				axisValues = new ArrayList<AxisValue>();
				mainGraphLine = new Line().setColor(Color.BLUE).setCubic(false).setStrokeWidth(1);
				graphLines = new ArrayList<Line>();

				measureLabel = null;

				// put every entry from JSON in graph and create a label
				for (int i = 0; i < totalEntries; ++i) {
					
					JSONObject json = feedArray.getJSONObject(jsonCounter--);
					
					// add measure type label to string
					if (measureLabel == null) {
						JSONObject indicator = json.getJSONObject("indicator");
						measureLabel = indicator.getString("value");
					}
					
					// extract current entry (int for value graph, float for percent graph)
					if (measureLabel.contains("%")) {
						percentValue = (float) json.getDouble("value");
						year = json.getInt("date");
						// create percent value labels for representation in the graph Y axis (single run)
						// and add invisible dummy 0 and 100 values to show full graph scale
						if (axisValues.size() == 0) {
							createPercentLabel();
							invisiblePercentGraphValues = new ArrayList<PointValue>();
							invisiblePercentGraphValues.add(new PointValue(year, 0));
							invisiblePercentGraphValues.add(new PointValue(year, 100));
							invisiblePercentGraphLine = new Line(invisiblePercentGraphValues).setHasPoints(false).setHasLines(false);
							graphLines.add(invisiblePercentGraphLine);
						}
						// add year and value to the list later used to draw graph
						values.add(new PointValue(year, percentValue));
					}
					else {
						value = json.getInt("value");
						year = json.getInt("date");
						// round the entry value for representation in the graph Y axis
						createNumberLabel();
						// add year and value to the list later used to draw graph
						values.add(new PointValue(year, value));
					}
					
					
				}

				mainGraphLine.setValues(values);
				graphLines.add(mainGraphLine);

				LineChartData chartData = new LineChartData();

				Axis axisX = new Axis().setMaxLabelChars(4);
				Axis axisY = new Axis().setName(measureLabel).setHasLines(true)
						.setMaxLabelChars(11);

				axisY.setValues(axisValues);

				chartData.setAxisXBottom(axisX);
				chartData.setAxisYLeft(axisY);

				chartData.setLines(graphLines);

				graph.setLineChartData(chartData);

				graphLayout.removeAllViews();
				graphLayout.addView(graph);

			} catch (JSONException e) {
				// TODO: Handle exception
				e.printStackTrace();
			}
		}
	}

	// called if the graph contains percent values
	private void createPercentLabel() {
		for (int i = 100; i >= 0; i -= 20) {
			char[] label = String.valueOf(i).toCharArray();
			axisValues.add(new AxisValue(i, label));
		}
	}
	
	// called if the graph contains number values
	private void createNumberLabel() {

		String valueString = String.valueOf(value);
		int digitCount = valueString.length() - 1;
		int charCounter = 0;
		// put first digit char in new label
		String newLabel = "" + valueString.charAt(charCounter++);
		// store the first three digits as int for rounded display value
		int firstThreeDigits = Integer.parseInt(valueString.substring(0, 3));
		// calculate rounded value for display
		roundedValue = 1;
		switch (digitCount) {
		case 9:
			roundedValue = firstThreeDigits * 10000000;
			break;
		case 8:
			roundedValue = firstThreeDigits * 1000000;
			break;
		case 7:
			roundedValue = firstThreeDigits * 100000;
			break;
		case 6:
			roundedValue = firstThreeDigits * 10000;
			break;
		case 5:
			roundedValue = firstThreeDigits * 1000;
			break;
		case 4:
			roundedValue = firstThreeDigits * 100;
			break;
		case 3:
			roundedValue = firstThreeDigits * 10;
			break;
		case 2:
			roundedValue = firstThreeDigits;
		}

		// change string to represent rounded population value
		valueString = String.valueOf(roundedValue);

		// add commas to the value string
		while (digitCount > 3) {
			while (digitCount-- % 3 != 0) {
				newLabel += valueString.charAt(charCounter++);
			}
			newLabel += "," + valueString.charAt(charCounter++);
		}
		// finalise new label with last three chars from string
		newLabel += valueString.substring(charCounter);

		// convert string to char array
		char[] label = newLabel.toCharArray();

		// add label to new list
		axisValues.add(new AxisValue(roundedValue, label));
	}

	public void removeFragment() {
		graphLayout.removeAllViews();

	}

	public RelativeLayout getlayout() { // TEMP for testing purpose.
		return graphLayout;
	}

}
