package org.worldbank.seg_2g.worldbankapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

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
		WebView tv = (WebView) findViewById(R.id.webView1);
		tv.getSettings().setJavaScriptEnabled(true);
		HTMLGenerator hg = new HTMLGenerator(this);
		String s = hg.getHTMLCode(Settings.CO2_EMISSIONS, 1994);
		System.out.println(s);
		tv.loadData(s, "text/html", null);
	}
}
