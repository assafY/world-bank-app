package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GraphActivity extends Activity implements ActionBar.TabListener {

	private static final CharSequence NO_NETWORK_TEXT = "Your device has no network";
	
	private CountryListAdapter listAdapter;
	private CountryListAdapter autoCompleteAdapter;
	private ArrayList<Country> countryList;      // will always contain all countries
	private ArrayList<Country> autoCompleteList; // will be reset every time text changes in text field

	private EditText countryTextView;
	private ListView countryListView;
	
	private QueryGenerator queryGen;
	private String queryJSON;
	
    private	GraphAdapter graphAdapter;
    private	ViewPager graphView;
	
	private final int startYear = 1999;
	private final int endYear = 2009;
	private int indicatorSelection = Settings.POPULATION;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Returns a fragment for each of the categories.
		graphAdapter = new GraphAdapter(getFragmentManager());		

		graphView = (ViewPager) findViewById(R.id.graphPager);
		graphView.setAdapter(graphAdapter);

		//Swap tab
		graphView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						new GraphFragment().removeFragment();
					}
				});

		// add the tabs to the action bar.
		for (int i = 0; i < graphAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(graphAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		//load the countries
		loadCountries();
		//puts the  country in the list
		createCountryViews();
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item events.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadCountries() {
		// load all countries into list from local json file
		countryList = new ArrayList<Country>();
		queryGen = new QueryGenerator(this);
		queryGen.setCountryList(countryList);
	}	
	
			
	private void createCountryViews() {
				
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
				if (deviceHasNetwork()) {
					queryJSON = queryGen.getJSON((Country) parent.getItemAtPosition(pos), indicatorSelection, startYear, endYear);
					new GraphFragment().createLinearGraph(GraphActivity.this,queryJSON, parent.getItemAtPosition(pos).toString());
				} else {
					Toast.makeText(getApplicationContext(), NO_NETWORK_TEXT, 
							   Toast.LENGTH_LONG).show();
				}
			}
		});

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
					autoCompleteAdapter = new CountryListAdapter(GraphActivity.this, autoCompleteList);
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
			
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// switch to the corresponding graph. not implemented yet
		graphView.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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

}
