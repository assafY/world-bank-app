package org.worldbank.seg_2g.worldbankapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphFragment extends Fragment {

	//TEMP class, once the action bar works properly it will be removed.
		private GraphViewSeries populationEntries;
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
		            GraphViewData[] gvd = new GraphViewData[totalEntries];
		            
		            JSONArray feedArray = dataFeed.getJSONArray(1);
		            int jsonCounter = totalEntries - 1;
		            
		            for (int i = 0; i <totalEntries; ++i) {
		                JSONObject json = feedArray.getJSONObject(jsonCounter--);
		                gvd[i]= new GraphViewData(i, json.getInt("value"));
		            }
		            populationEntries = new GraphViewSeries(gvd);
		            GraphView graphView = new LineGraphView(context, countryName);
		            graphView.addSeries(populationEntries);
		            graphLayout.removeAllViews();
		            graphLayout.addView(graphView);

		        } catch (JSONException e) {
		            // TODO: Handle exception
		            e.printStackTrace();
		        }	
			}	
		
	
			public void removeFragment(){
	            graphLayout.removeAllViews();
	
			}
}
