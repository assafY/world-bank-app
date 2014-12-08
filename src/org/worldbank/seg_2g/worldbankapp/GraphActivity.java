package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import org.worldbank.seg_2g.worldbankapp.RangeSeekBar.OnRangeSeekBarChangeListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

public class GraphActivity extends Activity implements ActionBar.TabListener {

	// constant to be shown as Toast across app when a country is selected while device has no network
	public static final CharSequence NO_NETWORK_TEXT = "Your device has no network";
	public static final CharSequence NO_COUNTRY_SELECTED = "Please select a country from the list";
	// constant string array containing tab values
	private static final String[] CATEGORY = {"Population","Energy","Environment"};
	// constant integers containing number of tabs and pages under each tab
	private static final int NUMBER_OF_CATEGORIES = CATEGORY.length;
	private static final int NUMBER_OF_PAGES = 4;
	
	// integer to keep track of which tab the user is in
	private int tabCounter;
	
	// main list, autocomplete list and view pager adapters
	private CountryListAdapter listAdapter;
	private CountryListAdapter autoCompleteAdapter;
	private	GraphAdapter graphAdapter;
	
	// viewpager responsible for page swiping
    public static ViewPager graphView;
	
    // full country list which will always contain all countries
	private ArrayList<Country> countryList;
	// autocomplete country list which will be reset every time text changes in text field
	private ArrayList<Country> autoCompleteList; 
	// graph fragment array caches fragments in the background as the user views different graphs
	GraphFragment[][] graphLayoutArray = new GraphFragment[NUMBER_OF_CATEGORIES][NUMBER_OF_PAGES];
	
	// country list drawer views
	private EditText countryTextView;
	private ListView countryListView;
	private DrawerLayout countryDrawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	
	private ActionBar actionBar;

	// fields to keep track of current page, tab and selected country
	public static int currentPagePosition = 1;
	private Country currentCountry;
	private String currentTab = CATEGORY[0];

	// json query generator and strings
	private QueryGenerator queryGen;
	private String queryJSON;
	private String comparisonQuery;
	
    // two-handle seekbar and year display
    private RangeSeekBar<Integer> yearSeekBar;
    private TextView startYearView;
    private TextView endYearView;
	
    // integers of the currently selected years, used for generating new json queries
	private int startYear;
	private int endYear;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		
		// Set up the action bar.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Returns a fragment for each of the categories.
		graphAdapter = new GraphAdapter(getFragmentManager());		

		graphView = (ViewPager) findViewById(R.id.graph_pager);
		graphView.setAdapter(graphAdapter);
		
		
		
		// swap tab
		graphView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						//Log.d("Debug", "Value: " + Integer.toString(position)); // Test Info **DONT REMOVE**
					
						// if next button was clicked before country is selected, notify user
						if (currentCountry == null) {
							Toast.makeText(getApplicationContext(), NO_COUNTRY_SELECTED, Toast.LENGTH_SHORT).show();
						}
						else {						
							currentPagePosition = position;
							//graphAdapter.setNewPosition(position);
							graphPage(position);
						}
					}
				});

		// add the tabs to the action bar.
		for (int i = 0; i < CATEGORY.length; i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(CATEGORY[i])
					.setTabListener(this));
		}
		
		// default year range values
		startYear = 1989;
		endYear = 2009;
		
		// find year number labels
		startYearView = (TextView) findViewById(R.id.start_year_textview);
		endYearView = (TextView) findViewById(R.id.end_year_textview);
		
		startYearView.setText(String.valueOf(startYear));
		endYearView.setText(String.valueOf(endYear));
		
		// construct seekbar and enable live updating
		yearSeekBar = new RangeSeekBar<Integer>(Settings.MIN_YEAR, Settings.MAX_YEAR, getApplicationContext());
		yearSeekBar.setSelectedMinValue(startYear);
		yearSeekBar.setSelectedMaxValue(endYear);
		yearSeekBar.setNotifyWhileDragging(true);
		
		// set listener to change year labels while user is dragging and redraw graph after a selection is made
		yearSeekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
	        @Override
	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
		        	startYearView.setText(String.valueOf(minValue));
		        	endYearView.setText(String.valueOf(maxValue));	
	        	
	        	if (!bar.isPressed() && (startYear != minValue || endYear != maxValue)) {
	        		int newStartYear = minValue;
		        	int newEndYear = maxValue;
		        	
		        	if (newStartYear != newEndYear) {
		        		startYear = newStartYear;
		        		endYear = newEndYear;
		        		graphLayoutArray = new GraphFragment[NUMBER_OF_CATEGORIES][NUMBER_OF_PAGES];
		        		graphPage(currentPagePosition);
		        	}
		        	// disable selection of same start and end years
		        	else {
		        		if (startYear != newStartYear) {
		        			yearSeekBar.setSelectedMinValue(--newStartYear);
		        			startYear = newStartYear;
		        			startYearView.setText(String.valueOf(newStartYear));
		        			graphLayoutArray = new GraphFragment[NUMBER_OF_CATEGORIES][NUMBER_OF_PAGES];
			        		graphPage(currentPagePosition);
		        		}
		        		else {
		        			yearSeekBar.setSelectedMaxValue(++newEndYear);
		        			endYear = newEndYear;
		        			endYearView.setText(String.valueOf(newEndYear));
		        			graphLayoutArray = new GraphFragment[NUMBER_OF_CATEGORIES][NUMBER_OF_PAGES];
			        		graphPage(currentPagePosition);
		        		}
		        	}
	        	}
	        }
		});
		
		LinearLayout seekBarLayout = (LinearLayout) findViewById(R.id.year_seek_bar_layout);
		seekBarLayout.addView(yearSeekBar);
		
		// initialize tab counter with default value
		tabCounter = 0;
		
		// load countries into list
		loadCountries();
		// create country list, text field, navigation drawer and adapters
		createCountryViews();
		// set listeners for country views
		setCountryViewListeners();
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));   
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item events.
		int id = item.getItemId();
		if (id == R.id.back) {
			// if back button was clicked before country is selected, notify user
			if (currentCountry == null) {
				Toast.makeText(getApplicationContext(), NO_COUNTRY_SELECTED, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			if (graphAdapter.getPosition() > 0) {
				graphPage(graphAdapter.getPosition());	
			    graphAdapter.setBackPosition();
			}
			else {
				if (graphAdapter.getPosition() == 0) {
					if (tabCounter > 0) {
						actionBar.setSelectedNavigationItem(--tabCounter);	
						graphAdapter.restartPosition();
						graphPage(graphAdapter.getPosition());	
					    graphAdapter.restartGraph();
					}
				}
			}
		}
		else if (id == R.id.next) {
			// if next button was clicked before country is selected, notify user
			if (currentCountry == null) {
				Toast.makeText(getApplicationContext(), NO_COUNTRY_SELECTED, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			if (graphAdapter.getPosition() < 5) {
				graphPage(graphAdapter.getPosition());	
				graphAdapter.setPosition();
			}
			else {
				if (tabCounter < 2) {
					actionBar.setSelectedNavigationItem(++tabCounter);	
					graphAdapter.restartPosition();
					graphPage(graphAdapter.getPosition());	
					graphAdapter.setPosition();
				}
			}
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
		//currentCountry = countryList.get(0);
	}	
	
			
	private void createCountryViews() {
				
		countryTextView = (EditText) findViewById(R.id.countries_text_view);
		countryDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		countryListView = (ListView) findViewById(R.id.countries_list_view);
		
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle("Countries");
		
		drawerToggle = new ActionBarDrawerToggle(this, countryDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				actionBar.show();
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				actionBar.hide();
				invalidateOptionsMenu();
			}
		};
		
		countryDrawerLayout.setDrawerListener(drawerToggle);
				
		listAdapter = new CountryListAdapter(this, countryList);
		countryListView.setAdapter(listAdapter);
	}
	
	private void setCountryViewListeners() {
		
		// add list selection listener
		countryListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// when a country is selected from the list, get JSON data and create graph
				
				// if a user is connected, create graph layout
				if (deviceHasNetwork()) {
					graphLayoutArray = new GraphFragment[NUMBER_OF_CATEGORIES][NUMBER_OF_PAGES];
					currentCountry = (Country) parent.getItemAtPosition(pos);
					graphPage(currentPagePosition);
					actionBar.setTitle(currentCountry.toString());
					actionBar.setSelectedNavigationItem(0);
					graphAdapter.restartGraph();
					tabCounter = 0;
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
					
					// if there is one country in new list, notify user they can press Go to select
					if (autoCompleteList.size() == 1) {
						Toast.makeText(getApplicationContext(), "Press 'Go' to select " + autoCompleteList.get(0).toString(), Toast.LENGTH_SHORT).show();
					}
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
				if (actionId == EditorInfo.IME_ACTION_GO) {
					if(!currentInput.equals("")) {
						//
						if (autoCompleteList.size() == 1) {
							graphLayoutArray = new GraphFragment[NUMBER_OF_CATEGORIES][NUMBER_OF_PAGES];
							currentCountry = autoCompleteList.get(0);
							graphPage(currentPagePosition);
							graphAdapter.restartGraph();
							tabCounter =0;
							return true;
						}
					}		
				}			
				return true;
			}
		});
	}
	
	/**TEMP this will be improved.
	 * This will provide the swap motion and will allow to fetch data from a 2D array of fragment [X][Y] where X is the category number and Y is the Graph
	 * If Position is 0 then it will return to the last category available, starting from page 1 of that category.
	 * @param position
	 * Contains certain bugs which will be fixed
	 */
	public void graphPage(int position) {
		

		switch (position) {
		
		case 1: 
				if (currentTab.equals(CATEGORY[0])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
				         new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
						break;
					}
				
				else if (currentTab.equals(CATEGORY[1])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.ENERGY_PRODUCTION, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
						break;
					}
				else if (currentTab.equals(CATEGORY[2])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.CO2_EMISSIONS, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
						break;
				}
		case 2: 	
				if (currentTab.equals(CATEGORY[0])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.URBAN_RURAL, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
						break;
					}
				
				else if (currentTab.equals(CATEGORY[1])) {	
						queryJSON = queryGen.getJSON(currentCountry, Settings.ENERGY_USE, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
						break;
					
				}
				else if (currentTab.equals(CATEGORY[2])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.FOREST_AREA, startYear, endYear);
				     	new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
						break;
					}		
		case 3: 	
				if (currentTab.equals(CATEGORY[0])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
						comparisonQuery = queryGen.getJSON(currentCountry, Settings.CO2_EMISSIONS, startYear, endYear);
				        new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
						break;
					}
				else if (currentTab.equals(CATEGORY[1])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.FOSSIL_FUEL, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
						break;
				
				}
				else if (currentTab.equals(CATEGORY[2])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
						comparisonQuery = queryGen.getJSON(currentCountry, Settings.FOREST_AREA, startYear, endYear);
					    new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
						break;
					}
					
		case 4: 	
				if (currentTab.equals(CATEGORY[0])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
						comparisonQuery = queryGen.getJSON(currentCountry, Settings.ENERGY_USE, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
						break;
				}
				else if (currentTab.equals(CATEGORY[1])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
						comparisonQuery = queryGen.getJSON(currentCountry, Settings.ENERGY_PRODUCTION, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
						break;
					
				}
				else if (currentTab.equals(CATEGORY[2])) {
						queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
						comparisonQuery = queryGen.getJSON(currentCountry, Settings.CO2_EMISSIONS, startYear, endYear);
						new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
						break;
				}
		}

}
			
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		currentTab = (String) tab.getText();
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
		
		ConnectivityManager networkManager = null;
        networkManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
        try {
        	boolean isDataConnected = networkManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
	        boolean isWifiConnected = networkManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
	        
	        if (isDataConnected || isWifiConnected) {
	            return true;
	        }
        } catch (NullPointerException e) {
        	// null is returned on tablets, therefore return true
        	return true;
        }
        
        
        return false;
        
    }

}
