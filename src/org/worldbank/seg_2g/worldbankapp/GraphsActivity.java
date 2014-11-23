package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphsActivity extends Activity {

	private int indicatorSelection;
	private String queryJSON;
	private boolean offlineMode;
	
	private ArrayList<Country> countryList;      // will always contain all countries
	private ArrayList<Country> autoCompleteList; // will be reset every time text changes in text field
	private QueryGenerator queryGen;
	private EditText countryTextView;
	
	private GraphViewSeries populationEntries;
	private LinearLayout graphLayout;
	private ListView countryListView;
	
	//
	private CountryListAdapter listAdapter;
	private CountryListAdapter autoCompleteAdapter;
	
	private int startYear;
	private int endYear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		
		/* This code is for when this activity will be started by
		 * the main activity and send the user's indicator selection
		 * 
		 * Intent intent = getIntent();
		 * indicatorSelection = intent.getStringExtra(MainActivity.SELECTED_INDICATOR);
		 */
		
		// for now the code sent to the query generator is for the population indicator
		indicatorSelection = Settings.POPULATION;
		
		graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
		
		// load all countries into list from local json file
		countryList = new ArrayList<Country>();
		queryGen = new QueryGenerator(this);
		queryGen.setCountryList(countryList);
		
		// year selection currently hard coded
		startYear = 1999;
		endYear = 2009;
		
		// create country editText and listView
		createCountryViews();
		
		// check if network is available and set connectivity boolean (offline mode not yet available)
		if (deviceHasNetwork()) {
			offlineMode = false;
		} else {
			offlineMode = true;
		}
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
	
	// check if the device has network access
	private boolean deviceHasNetwork() {
		
        ConnectivityManager networkManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isDataConnected = networkManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiConnected = networkManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        
        if (isDataConnected || isWifiConnected) {
            return true;
        } else {
            return false;
        }
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
				// TODO: change hard coded year range to two bar seekbar
				queryJSON = queryGen.getJSON((Country) parent.getItemAtPosition(pos), indicatorSelection, startYear, endYear);
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
		
		countryTextView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				//
				String currentInput = view.getText().toString().toLowerCase();
				if (actionId == EditorInfo.IME_NULL) {
					if(!currentInput.equals("")) {
						//
						if (autoCompleteList.size() == 1) {
							queryJSON = queryGen.getJSON(autoCompleteList.get(0), indicatorSelection, startYear, endYear);
							createGraph(queryJSON, autoCompleteList.get(0).toString());
							return true;
						}
					}		
				}			
				return true;
			}
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
