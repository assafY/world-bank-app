package org.worldbank.seg_2g.worldbankapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MapActivity extends Activity {

	public static final CharSequence NO_NETWORK_TEXT = "Your device has no network";
	HTMLGenerator htmlGen;
	WebView webMap;
	int currentAttribute = 1, currentYear = 2010;

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
			new AttributePickerDialogFragment().show(getFragmentManager(),
					"options_attribute");
		} else if (id == R.id.action_modify_year) {
			new YearPickerDialogFragment().show(getFragmentManager(),
					"options_year");
		}
		return super.onOptionsItemSelected(item);
	}

	// check if the device has network access
	private boolean deviceHasNetwork() {

		ConnectivityManager networkManager = null;
		networkManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		try {
			boolean isDataConnected = networkManager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
			boolean isWifiConnected = networkManager.getNetworkInfo(
					ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

			if (isDataConnected || isWifiConnected) {
				return true;
			}
		} catch (NullPointerException e) {
			// null is returned on tablets, therefore return true
			return true;
		}

		return false;

	}

	protected void onCreateExtra() {

		if (deviceHasNetwork()) {
			webMap = (WebView) findViewById(R.id.webView1);
			WebSettings webMapSettings = webMap.getSettings();
			webMapSettings.setJavaScriptEnabled(true);
			webMapSettings.setBuiltInZoomControls(true);
			webMapSettings.setDisplayZoomControls(false);
			webMapSettings.setLoadWithOverviewMode(true);
			webMapSettings.setUseWideViewPort(true);
			htmlGen = new HTMLGenerator(this);

			webMap.loadData(htmlGen.getNoDataCode(), "text/html", null);
		} else {
			Toast.makeText(getApplicationContext(), NO_NETWORK_TEXT,
					Toast.LENGTH_LONG).show();
		}

	}

	private void drawMap() {

		this.setTitle(Settings.Attributes[currentAttribute] + " - "
				+ currentYear);
		webMap.loadData(htmlGen.getLoadingCode(), "text/html", null);

		new Thread(new Runnable() {

			@Override
			public void run() {

				final String s = htmlGen.getHTMLCode(currentAttribute,
						currentYear);
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
			String[] options = new String[nr];
			for (int i = 0; i < nr; i++) {
				options[i] = Settings.Attributes[i + 1];
			}
			return options;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setItems(getOptions(),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (deviceHasNetwork()) {
								currentAttribute = which + 1;
								drawMap();
							} else {
								Toast.makeText(getApplicationContext(),
										NO_NETWORK_TEXT, Toast.LENGTH_LONG)
										.show();
							}
						}
					});
			return builder.create();
		}
	}

	private class YearPickerDialogFragment extends DialogFragment {

		private String[] getOptions() {
			String[] options = new String[54];
			for (int i = 0; i < 54; i++) {
				options[i] = "" + (i + 1961);
			}
			return options;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final NumberPicker picker = new NumberPicker(
					getApplicationContext());
			picker.setMinValue(1961);
			picker.setMaxValue(2014);
			picker.setValue(currentYear);
			picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
			builder.setView(picker);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							if (deviceHasNetwork()) {
								currentYear = picker.getValue();
								drawMap();
							} else {
								Toast.makeText(getApplicationContext(),
										NO_NETWORK_TEXT, Toast.LENGTH_LONG)
										.show();
							}
						}
					});
			return builder.create();
		}
	}
}
