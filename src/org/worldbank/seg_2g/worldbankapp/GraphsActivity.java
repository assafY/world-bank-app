package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphsActivity extends Activity {

	private String queryJSON;
	
	private ArrayList<Country> countryList;
	private ArrayList<Country> autoCompleteList;
	private QueryGenerator queryGen;
	private EditText countryTextView;
	
	private GraphViewSeries populationEntries;
	private LinearLayout graphLayout;
	private ListView countryListView;
	
	private CountryListAdapter listAdapter;
	private CountryListAdapter autoCompleteAdapter;
	
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
		countryTextView = (EditText) findViewById(R.id.countries_text_view);
		countryListView = (ListView) findViewById(R.id.countries_list_view);
		
		listAdapter = new CountryListAdapter(this, countryList);
		countryListView.setAdapter(listAdapter);
		
		// add list selection listener
		countryListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// when a country is selected from the list, get JSON data and create graph
				queryJSON = queryGen.getJSON((Country) parent.getItemAtPosition(pos), Settings.POPULATION, 1999, 2009);
				createGraph(queryJSON, parent.getItemAtPosition(pos).toString());	
			}
		});
		
		// add EditText key listener, update list on key typed
		countryTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable e) {
				// store current EditText input in lower case
				String currentInput = countryTextView.getText().toString().toLowerCase();
				
				// if the text field is not empty
				if (!currentInput.equals("")) {
					
					autoCompleteList = new ArrayList<Country>();
					for (Country c: countryList) {
						// for every country in the full country list, if it starts with the same text in the text field
						// add it to a new country list
						if(c.toString().toLowerCase().startsWith(currentInput)) {
							autoCompleteList.add(c);
						}
					}
					// create a new adapter using the new country list and set it to the ListView
					autoCompleteAdapter = new CountryListAdapter(GraphsActivity.this, autoCompleteList);
					countryListView.setAdapter(autoCompleteAdapter); 
				}
				// if the text field is empty, set the original adapter with the full country list to the ListView
				else {
					countryListView.setAdapter(listAdapter);
				}
			}

			// not needed for this listener but needs to be implemented
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}
			
			// not needed for this listener but needs to be implemented
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
		});
		
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
