package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SimpleValueFormatter;
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

	//TEMP class, once the action bar works properly it will be removed.
		//private GraphViewSeries populationEntries;
		private static RelativeLayout graphLayout;
		
		   // Inflate the layout
		  public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	            View fragmentView =  (RelativeLayout) inflater.inflate(R.layout.fragment_graphs, container, false);
	            graphLayout = (RelativeLayout) fragmentView.findViewById(R.id.Graphs);
			    return fragmentView;
			    }  
		  
			protected void createLinearGraph(GraphActivity context, String data, String countryName) {
				
				// if data is null, there is no network (second check for tablets)
				if (data == null) {
					Toast.makeText(context, GraphActivity.NO_NETWORK_TEXT, 
							   Toast.LENGTH_LONG).show();
				}
				else {
					try {
			            JSONArray dataFeed = new JSONArray(data);
			            JSONObject titleValues = dataFeed.getJSONObject(0);
			            
			            // get total number of entries
			            int totalEntries = titleValues.getInt("total");
			            LineChartView graph = new LineChartView(context);
			            List<PointValue> values = new ArrayList<PointValue>();
			            
			            JSONArray feedArray = dataFeed.getJSONArray(1);
			            int jsonCounter = totalEntries - 1;
			            List<AxisValue> popAxisValues = new ArrayList<AxisValue>();
			            
			            for (int i = 0; i < totalEntries; ++i) {
			            	// extract current population entry
			                JSONObject json = feedArray.getJSONObject(jsonCounter--);
			                int population = json.getInt("value");
			                int year = json.getInt("date");
			                
			                // add year and population to the list later used to draw graph
			                values.add(new PointValue(year, population));
			                
			                // add commas to Y axis labels
			                String popString = String.valueOf(population);
			                int digitCount = popString.length() - 1;
							int charCounter = 0;
							// put first digit char in new label
							String newLabel = "" + popString.charAt(charCounter++);
							// store the first digit as int for rounded display value
							int firstThreeDigits = Integer.parseInt(popString.substring(0, 3));
							// calculate rounded population for display
							int roundedPop = 1;
							switch(digitCount) {
								case 8: roundedPop = firstThreeDigits * 1000000;
										break;
								case 7: roundedPop = firstThreeDigits * 100000;
										break;
								case 6: roundedPop = firstThreeDigits * 10000;
										break;
								case 5: roundedPop = firstThreeDigits * 1000;
										break;
								case 4: roundedPop = firstThreeDigits * 100;
										break;
								case 3: roundedPop = firstThreeDigits * 10;
										break;
							}
							
							// change string to represent rounded population value
							popString = String.valueOf(roundedPop);
							
							while (digitCount > 3) {
								while (digitCount-- % 3 != 0) {
									newLabel += popString.charAt(charCounter++);
								}
								newLabel += "," + popString.charAt(charCounter++);
							}
							// finalise new label with last three chars from string
							newLabel += popString.substring(charCounter);
							// convert string to char array
							char[] label = newLabel.toCharArray();
							
							// add label to new list
							popAxisValues.add(new AxisValue(roundedPop, label));
			            }
			            
			            Line line = new Line(values).setColor(Color.BLUE).setCubic(false).setStrokeWidth(1);
			            List<Line> lines = new ArrayList<Line>();
			            lines.add(line);
			            
			            LineChartData chartData = new LineChartData();
			            
			            Axis axisX = new Axis().setMaxLabelChars(4);
						Axis axisY = new Axis().setHasLines(true).setMaxLabelChars(11);
						
						axisY.setValues(popAxisValues);
						
						chartData.setAxisXBottom(axisX);
						chartData.setAxisYLeft(axisY);
						
			            chartData.setLines(lines);
			            
			            graph.setLineChartData(chartData);
			            
			            graphLayout.removeAllViews();
			            graphLayout.addView(graph);
	
			        } catch (JSONException e) {
			            // TODO: Handle exception
			            e.printStackTrace();
			        }
				}
			}	
		
	
			public void removeFragment(){
	            graphLayout.removeAllViews();
	
			}
}
