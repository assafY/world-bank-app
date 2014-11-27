package org.worldbank.seg_2g.worldbankapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	public void countryMenu(View view){
		if(deviceHasNetwork()){
		Intent countryIntent = new Intent(this,CountryActivity.class);
		startActivity(countryIntent);
		}
	}
	
	public void helpMenu(View view){
	Intent helpIntent = new Intent(this, HelpActivity.class);
	startActivity(helpIntent);
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
