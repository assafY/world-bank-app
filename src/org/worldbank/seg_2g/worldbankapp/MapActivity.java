package org.worldbank.seg_2g.worldbankapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MapActivity extends Activity {
	
	HTMLGenerator htmlGen;
	WebView webMap;
	int currentAttribute = 1, currentYear = 2014;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		onCreateExtra();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_modify_attribute) {
			new AttributePickerDialogFragment().show(getFragmentManager(), "options_attribute");
		}
		else if (id == R.id.action_modify_year) {
			new YearPickerDialogFragment().show(getFragmentManager(), "options_year");
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	protected void onCreateExtra() {
		
		webMap = (WebView) findViewById(R.id.webView1);
		WebSettings webMapSettings = webMap.getSettings();
		webMapSettings.setJavaScriptEnabled(true);
		webMapSettings.setBuiltInZoomControls(true);
		webMapSettings.setDisplayZoomControls(false);
		webMapSettings.setLoadWithOverviewMode(true);
		webMapSettings.setUseWideViewPort(true);
		htmlGen = new HTMLGenerator(this);
		
		webMap.loadData(htmlGen.getNoDataCode(), "text/html", null);

	}

	private void drawMap() {
		
		this.setTitle(Settings.Attributes[currentAttribute] + " - " + currentYear);
		webMap.loadData(htmlGen.getLoadingCode(), "text/html", null);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				final String s = htmlGen.getHTMLCode(currentAttribute, currentYear);
				webMap.post(new Runnable() {

					@Override
					public void run() {
						webMap.loadData(s, "text/html", null);
					}
				});
			}
		}).start();
	}
	
	private class AttributePickerDialogFragment extends DialogFragment {
				
		private String[] getOptions() {
			int nr = Settings.NUMBER_OF_ATTRIBUTES;
			String [] options = new String[nr];
			for (int i = 0; i < nr; i++) {
				options[i] = Settings.Attributes[i+1];
			}
			return options;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setItems(getOptions(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					currentAttribute = which+1;
					drawMap();
				}
			});
			return builder.create();
		}
	}
	
	private class YearPickerDialogFragment extends DialogFragment {
		
		private String[] getOptions() {
			String [] options = new String[54];
			for (int i = 0; i < 54; i++) {
				options[i] = ""+(i+1961);
			}
			return options;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setItems(getOptions(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					currentYear = which+1961;
					drawMap();
				}
			});
			return builder.create();
		}
	}
}
