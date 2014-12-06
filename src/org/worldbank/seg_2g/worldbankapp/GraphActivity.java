package org.worldbank.seg_2g.worldbankapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.worldbank.seg_2g.worldbankapp.RangeSeekBar.OnRangeSeekBarChangeListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
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

	public static final CharSequence NO_NETWORK_TEXT = "Your device has no network";
	private static final CharSequence ACTIVITY_TITLE = "Graph Activity";
	private static final CharSequence DRAWER_TITLE = "Select Country";
	private static final String[] CATEGORY = {"Population","Energy","Environment"};
	
	private static Date MIN_YEAR;
	private static Date MAX_YEAR;
	
	private static final int NUMBER_OF_CATEGORIES = CATEGORY.length;
	private static final int NUMBER_OF_PAGES = 4;
	
	public static int CATEGORY_COUNTER = 0;
	
	private CountryListAdapter listAdapter;
	private CountryListAdapter autoCompleteAdapter;
	
	private ArrayList<Country> countryList;      // will always contain all countries
	private ArrayList<Country> autoCompleteList; // will be reset every time text changes in text field
	GraphFragment[][] graphLayoutArray = new GraphFragment[NUMBER_OF_CATEGORIES][NUMBER_OF_PAGES]; // will save graphs of a certain country
	
	private EditText countryTextView;
	private ListView countryListView;
	private DrawerLayout countryDrawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ActionBar actionBar;

	
	public static int currentPagePosition = 1;
	private Country currentCountry;
	private String currentTab = CATEGORY[0];

	private QueryGenerator queryGen;
	private String queryJSON;
	private String comparisonQuery;
	
    private	GraphAdapter graphAdapter;
    public static	ViewPager graphView;
    
    private RangeSeekBar<Long> yearSeekBar;
    private TextView startYearView;
    private TextView endYearView;
    
    private SharedPreferences graphPreferences;
	
	private int startYear = 1989;
	private int endYear = 2009;
	//private int indicatorSelection = Settings.POPULATION;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		
		// Set up the action bar.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		
		
		graphPreferences = getPreferences(0);
		
		// Returns a fragment for each of the categories.
		graphAdapter = new GraphAdapter(getFragmentManager());		

		graphView = (ViewPager) findViewById(R.id.graph_pager);
		graphView.setAdapter(graphAdapter);
		
		
		
		// swap tab
		graphView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Log.d("Debug", "Value: " + Integer.toString(position)); // Test Info **DONT REMOVE**
						currentPagePosition = position;
						graphPage(position);
					}
				});

		// add the tabs to the action bar.
		for (int i = 0; i < CATEGORY.length; i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(CATEGORY[i])
					.setTabListener(this));
		}
		
		try {
			MIN_YEAR = new SimpleDateFormat("yyyy").parse("1960");
			MAX_YEAR = new SimpleDateFormat("yyyy").parse("2013");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		startYearView = (TextView) findViewById(R.id.start_year_textview);
		endYearView = (TextView) findViewById(R.id.end_year_textview);
		
		yearSeekBar = new RangeSeekBar<Long>(MIN_YEAR.getTime(), MAX_YEAR.getTime(), getApplicationContext());
		yearSeekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Long>() {
	        @Override
	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
	        	
	        }
	});
		
		LinearLayout seekBarLayout = (LinearLayout) findViewById(R.id.year_seek_bar_layout);
		seekBarLayout.addView(yearSeekBar);
		
		// load countries into list
		loadCountries();
		// create country list, text field, navigation drawer and adapters
		createCountryViews();
		// set listeners for country views
		setCountryViewListeners();
		
		if (!graphPreferences.getBoolean("activityPreviouslyOpened", false)) {
			// find way to open drawer first time app is used
			SharedPreferences.Editor graphPrefsEditor = graphPreferences.edit();
			graphPrefsEditor.putBoolean("activityPreviouslyOpened", true);
		}
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));   
		getActionBar().setDisplayShowTitleEnabled(false);
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
		currentCountry = countryList.get(0);
	}	
	
			
	private void createCountryViews() {
				
		countryTextView = (EditText) findViewById(R.id.countries_text_view);
		countryDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		countryListView = (ListView) findViewById(R.id.countries_list_view);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		drawerToggle = new ActionBarDrawerToggle(this, countryDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				getActionBar().setTitle(ACTIVITY_TITLE);
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				getActionBar().setTitle(DRAWER_TITLE);
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
				// TODO: change hard coded year range to two bar seekbar
				
				// if a user is connected, create graph layout
				if (deviceHasNetwork()) {
					currentCountry = (Country) parent.getItemAtPosition(pos);
					graphPage(currentPagePosition);
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
		
		countryTextView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				//
				String currentInput = view.getText().toString().toLowerCase();
				if (actionId == EditorInfo.IME_ACTION_GO) {
					if(!currentInput.equals("")) {
						//
						if (autoCompleteList.size() == 1) {
							queryJSON = queryGen.getJSON(autoCompleteList.get(0), Settings.POPULATION, startYear, endYear);
							new GraphFragment().createGraph(GraphActivity.this, queryJSON, autoCompleteList.get(0).toString());
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
			case 0: if (CATEGORY_COUNTER > 0) {
						currentPagePosition = 4;
						actionBar.setSelectedNavigationItem(--CATEGORY_COUNTER);	
						break;
					}	
			case 1: 
					if (currentTab.equals(CATEGORY[0])) {
						if (graphLayoutArray[0][0] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
							graphLayoutArray[0][0] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[0][0].reloadGraph();
							break;
						}
					}
					else if (currentTab.equals(CATEGORY[1])) {
						if (graphLayoutArray[1][0] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.ENERGY_PRODUCTION, startYear, endYear);
							graphLayoutArray[1][0] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[1][0].reloadGraph();
							break;
						}
						
					}
					else if (currentTab.equals(CATEGORY[2])) {
						if (graphLayoutArray[2][0] == null) {	
							queryJSON = queryGen.getJSON(currentCountry, Settings.CO2_EMISSIONS, startYear, endYear);
							graphLayoutArray[2][0] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[2][0].reloadGraph();
							break;
						}
					}
			case 2: 	
					if (currentTab.equals(CATEGORY[0])) {
						if (graphLayoutArray[0][1] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.URBAN_RURAL, startYear, endYear);
							graphLayoutArray[0][1] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[0][1].reloadGraph();
							break;
						}
					}
					else if (currentTab.equals(CATEGORY[1])) {
						if (graphLayoutArray[1][1] == null) {	
							queryJSON = queryGen.getJSON(currentCountry, Settings.ENERGY_USE, startYear, endYear);
							graphLayoutArray[1][1] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[1][1].reloadGraph();
							break;
						}
					}
					else if (currentTab.equals(CATEGORY[2])) {
						if (graphLayoutArray[2][1] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.FOREST_AREA, startYear, endYear);
							graphLayoutArray[2][1] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[2][1].reloadGraph();
							break;
						}
					}			
			case 3: 	
					if (currentTab.equals(CATEGORY[0])) {
						if (graphLayoutArray[0][2] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
							comparisonQuery = queryGen.getJSON(currentCountry, Settings.CO2_EMISSIONS, startYear, endYear);
							graphLayoutArray[0][2] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[0][2].reloadGraph();
							break;
						}
					}
					else if (currentTab.equals(CATEGORY[1])) {
						if (graphLayoutArray[1][2] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.FOSSIL_FUEL, startYear, endYear);
							graphLayoutArray[1][2] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[1][2].reloadGraph();
							break;
						}
					}
					else if (currentTab.equals(CATEGORY[2])) {
						if (graphLayoutArray[2][2] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
							comparisonQuery = queryGen.getJSON(currentCountry, Settings.FOREST_AREA, startYear, endYear);
							graphLayoutArray[2][2] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[2][2].reloadGraph();
							break;
						}
					}			
					break;
			case 4: 	
					if (currentTab.equals(CATEGORY[0])) {
						if (graphLayoutArray[0][3] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
							comparisonQuery = queryGen.getJSON(currentCountry, Settings.ENERGY_USE, startYear, endYear);
							graphLayoutArray[0][3] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[0][3].reloadGraph();
							break;
						}
					}
					else if (currentTab.equals(CATEGORY[1])) {
						if (graphLayoutArray[1][3] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
							comparisonQuery = queryGen.getJSON(currentCountry, Settings.ENERGY_PRODUCTION, startYear, endYear);
							graphLayoutArray[1][3] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[1][3].reloadGraph();
							break;
						}
					}
					else if (currentTab.equals(CATEGORY[2])) {
						if (graphLayoutArray[2][3] == null) {
							queryJSON = queryGen.getJSON(currentCountry, Settings.POPULATION, startYear, endYear);
							comparisonQuery = queryGen.getJSON(currentCountry, Settings.CO2_EMISSIONS, startYear, endYear);
							graphLayoutArray[2][3] = new GraphFragment().createGraph(GraphActivity.this, queryJSON, comparisonQuery, currentCountry.toString());
							break;
						}
						else {
							graphLayoutArray[2][3].reloadGraph();
							break;
						}
					}			
			case 5: if (CATEGORY_COUNTER < 2) {
						currentPagePosition = 1;
						actionBar.setSelectedNavigationItem(++CATEGORY_COUNTER);
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
