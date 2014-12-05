package org.worldbank.seg_2g.worldbankapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MapActivity extends Activity {

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

	protected void onCreateExtra() {
		final WebView tv = (WebView) findViewById(R.id.webView1);
		WebSettings tvSettings = tv.getSettings();
		tvSettings.setJavaScriptEnabled(true);
		tvSettings.setBuiltInZoomControls(true);
		tvSettings.setDisplayZoomControls(false);
		tvSettings.setLoadWithOverviewMode(true);
		tvSettings.setUseWideViewPort(true);
//		setContentView(tv);
		final Context contxt = this;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HTMLGenerator hg = new HTMLGenerator(contxt);
				final String s = hg.getHTMLCode(Settings.CO2_EMISSIONS, 1994);
				tv.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tv.loadData(s, "text/html", null);
					}
				});
			}
		}).start();
//		HTMLGenerator hg = new HTMLGenerator(this);
//		String s = hg.getHTMLCode(Settings.CO2_EMISSIONS, 1994);
//		System.out.println(s);
//		tv.loadData(s, "text/html", null);
	}
}
