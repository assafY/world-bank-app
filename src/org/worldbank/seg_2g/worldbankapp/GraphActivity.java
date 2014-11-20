package org.worldbank.seg_2g.worldbankapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphActivity extends Activity {

	private GraphViewSeries populationEntries;
	private LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		// get JSON from Query Generator
		Intent intent = getIntent();
		String graphData = intent.getStringExtra(QueryGenerator.QUERY_RESULT);
		
		layout = (LinearLayout) findViewById(R.id.graph_layout);

		createGraph(graphData);
	}

	private void createGraph(String data) {
		
        try {
            JSONArray dataFeed = new JSONArray(data);
            JSONObject titleValues = dataFeed.getJSONObject(0);
            
            // get total number of entries
            int totalEntries = titleValues.getInt("total");
            GraphViewData[] gvd = new GraphViewData[totalEntries];
            
            JSONArray feedArray = dataFeed.getJSONArray(1);
            
            for (int i = 0; i < totalEntries; ++i) {
                JSONObject json = feedArray.getJSONObject(i);
                gvd[i]= new GraphViewData(i, json.getInt("value"));
            }

            populationEntries = new GraphViewSeries(gvd);

            GraphView graphView = new LineGraphView(this, "Some Country");
            graphView.addSeries(populationEntries);
            layout.addView(graphView);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
