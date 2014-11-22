package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphsActivity extends Activity {

	private String queryJSON;
	
	private ArrayList<Country> countryList;
	private QueryGenerator queryGen;
	private TextView countryTextView;
	
	private GraphViewSeries populationEntries;
	private LinearLayout graphLayout;
	private ListView countryListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		
		graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
		
		// load all countries from local json file into list
		countryList = new ArrayList<Country>();
		queryGen = new QueryGenerator(this);
		queryGen.setCountryList(countryList);
		
		// create country autocomplete textview and listview
		createCountryViews();
		
		//TODO: Check if network is available
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	private void createCountryViews() {
		
		// at the moment creates a text view and list view which overlap, need to fix the text view
		countryTextView = (TextView) findViewById(R.id.countries_text_view);
		countryListView = (ListView) findViewById(R.id.countries_list_view);
		
		CountryListAdapter listAdapter = new CountryListAdapter(this, countryList);
		countryListView.setAdapter(listAdapter);
		
		// add selection listeners
		OnItemClickListener clickListener = new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				queryJSON = queryGen.getJSON((Country) parent.getItemAtPosition(pos), Settings.POPULATION, 1999, 2009);
				createGraph(queryJSON, parent.getItemAtPosition(pos).toString());	
			}
		};
		
		countryListView.setOnItemClickListener(clickListener);
	}

	protected void createGraph(String data, String countryName) {
		
		try {
            JSONArray dataFeed = new JSONArray(data);
            JSONObject titleValues = dataFeed.getJSONObject(0);
            
            // get total number of entries
            int totalEntries = titleValues.getInt("total");
            GraphViewData[] gvd = new GraphViewData[totalEntries];
            
            JSONArray feedArray = dataFeed.getJSONArray(1);
            int jsonCounter = totalEntries - 1;
            
            for (int i = 0; i < totalEntries; ++i) {
                JSONObject json = feedArray.getJSONObject(jsonCounter--);
                gvd[i]= new GraphViewData(i, json.getInt("value"));
            }

            populationEntries = new GraphViewSeries(gvd);

            GraphView graphView = new LineGraphView(this, countryName);
            graphView.addSeries(populationEntries);
            graphLayout.removeAllViews();
            graphLayout.addView(graphView);

        } catch (JSONException e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
	
		
	}

}
