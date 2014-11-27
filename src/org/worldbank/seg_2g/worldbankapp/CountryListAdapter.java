package org.worldbank.seg_2g.worldbankapp;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CountryListAdapter extends ArrayAdapter<Country> {
	
	private ArrayList<Country> countryList;	
	private Activity activity;
	
	public CountryListAdapter(Activity activity, ArrayList<Country> cl) {
		
		super(activity, R.layout.list_item_layout, cl);
		
		countryList = cl;
		this.activity = activity;
	}
	
	// override method to add flag icon and country name into every list item
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		LayoutInflater inflater = activity.getLayoutInflater();
		
		View tableRow = inflater.inflate(R.layout.list_item_layout, null, true);
		TextView countryTextView = (TextView) tableRow.findViewById(R.id.country_text_view);
		ImageView countryImageView = (ImageView) tableRow.findViewById(R.id.flag_image_view);
		
		Country currentCountry = countryList.get(position);
		countryTextView.setText(" " + currentCountry.toString());
		countryTextView.setText(currentCountry.toString());
		
		countryTextView.setText(currentCountry.toString());
		// if country has a flag icon add it to the list
		if (currentCountry.hasFlagIcon()) {
			countryImageView.setImageResource(currentCountry.getFlagId());
		}
		
		return tableRow;
		
	}
}
