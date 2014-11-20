package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private ArrayList<Country> countryList;
	private QueryGenerator queryGen;
	private Spinner countrySpinner;
	private TextView testTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// load all countries from local json file into list
		countryList = new ArrayList<Country>();
		queryGen = new QueryGenerator(this, testTextView);
		queryGen.setCountryList(countryList);
		
		testTextView = (TextView) findViewById(R.id.textView1);
		
		// create country spinner
		createSpinner();
		
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
	
	private void createSpinner() {
		countrySpinner = (Spinner) findViewById(R.id.country_spinner);
		ArrayAdapter<Country> spinnerArrayAdapter = new ArrayAdapter<Country>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
		countrySpinner.setAdapter(spinnerArrayAdapter);
		
		// add selection listeners
		countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if(!parent.getItemAtPosition(pos).toString().equalsIgnoreCase("aruba")) {
					
					// when a country is selected, create a query and start a graph activity
					queryGen.createGraph((Country) parent.getItemAtPosition(pos), Settings.POPULATION, 1999, 2009, testTextView);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

}
