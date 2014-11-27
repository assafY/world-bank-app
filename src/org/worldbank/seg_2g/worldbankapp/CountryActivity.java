package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class CountryActivity extends Activity {
	private EditText countryTextView;
	private ListView countryListView;
	private CountryListAdapter listAdapter;
	private CountryListAdapter autoCompleteAdapter;
	private ArrayList<Country> countryList; 
	private ArrayList<Country> autoCompleteList;
	private QueryGenerator queryGen;
	private Intent graphActivity;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country);
		loadCountries();
		createCountryViews();
		graphActivity = new Intent(this,GraphActivity.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.country, menu);
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
	
private void loadCountries(){
// load all countries into list from local json file
countryList = new ArrayList<Country>();
queryGen = new QueryGenerator(this);
queryGen.setCountryList(countryList);
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
			startActivity(graphActivity);
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
					autoCompleteAdapter = new CountryListAdapter(CountryActivity.this, autoCompleteList);
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
}
