package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class GraphActivity extends Activity implements ActionBar.TabListener {

	private static final CharSequence NO_NETWORK_TEXT = "Your device has no network";
	private static final CharSequence ACTIVITY_TITLE = "Graph Activity";
	private static final CharSequence DRAWER_TITLE = "Select Country";
	
	private CountryListAdapter autoCompleteAdapter;
	private CountryListAdapter listAdapter;
	private ArrayList<Country> countryList;      // will always contain all countries
	private ArrayList<Country> autoCompleteList; // will be reset every time text changes in text field
	private EditText countryTextView;
	private DrawerLayout countryDrawerLayout;
	private ActionBarDrawerToggle drawerToggle;
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
		else if (drawerToggle.onOptionsItemSelected(item)) {
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
				
		countryDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		countryListView = (ListView) findViewById(R.id.countries_list_view);
		countryTextView = (EditText) findViewById(R.id.country_text_view);
		
		drawerToggle = new ActionBarDrawerToggle(this, countryDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(ACTIVITY_TITLE);
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(DRAWER_TITLE);
				invalidateOptionsMenu();
			}
		};
		
		countryDrawerLayout.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
				
		listAdapter = new CountryListAdapter(this, countryList);
		countryListView.setAdapter(listAdapter);
				
		// add list selection listener
		countryListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// when a country is selected from the list, get JSON data and create graph
				// TODO: change hard coded year range to two bar seekbar
				
				// if a user is connected, create graph layout
				if (deviceHasNetwork()) {
					queryJSON = queryGen.getJSON((Country) parent.getItemAtPosition(pos), indicatorSelection, startYear, endYear);
					new GraphFragment().createLinearGraph(GraphActivity.this,queryJSON, parent.getItemAtPosition(pos).toString());
				}
				// if disconnected do nothing and notify with Toast
				else {
					Toast.makeText(getApplicationContext(), NO_NETWORK_TEXT, 
							   Toast.LENGTH_LONG).show();
				}
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
				
				/*countryTextView.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
						//
						String currentInput = view.getText().toString().toLowerCase();
						if (actionId == EditorInfo.IME_ACTION_GO) {
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
				});*/
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
