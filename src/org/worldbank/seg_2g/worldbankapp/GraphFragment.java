package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;
import java.util.List;

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
				
				try {
		            JSONArray dataFeed = new JSONArray(data);
		            JSONObject titleValues = dataFeed.getJSONObject(0);
		            
		            // get total number of entries
		            int totalEntries = titleValues.getInt("total");
		            LineChartView graph = new LineChartView(context);
		            List<PointValue> values = new ArrayList<PointValue>();
		            
		            JSONArray feedArray = dataFeed.getJSONArray(1);
		            int jsonCounter = totalEntries - 1;
		            
		            for (int i = 0; i <totalEntries; ++i) {
		                JSONObject json = feedArray.getJSONObject(jsonCounter--);
		                values.add(new PointValue(i, json.getInt("value")));
		            }
		            
		            Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
		            List<Line> lines = new ArrayList<Line>();
		            lines.add(line);
		            
		            LineChartData chartData = new LineChartData();
		            chartData.setLines(lines);
		            graph.setLineChartData(chartData);
		            
		            graphLayout.removeAllViews();
		            graphLayout.addView(graph);

		        } catch (JSONException e) {
		            // TODO: Handle exception
		            e.printStackTrace();
		        }	
			}	
		
	
			public void removeFragment(){
	            graphLayout.removeAllViews();
	
			}
}
