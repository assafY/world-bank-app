package org.worldbank.seg_2g.worldbankapp;

import android.content.Context;

public class Country {

	private Context context;
	
	private String name;
	private String threeLetterCode;
	private String twoLetterCode;
	
	private boolean hasIcon;
	
	public Country(String countryName, String threeLetterCode, String twoLetterCode, Context context) {
		this.context = context;
		
		this.name = countryName;
		this.threeLetterCode = threeLetterCode;
		this.twoLetterCode = twoLetterCode;
		
		// check if flag icon exists and set boolean
		int checkFlag = context.getResources().getIdentifier(twoLetterCode, "drawable", context.getPackageName());
		if (checkFlag != 0) {
			hasIcon = true;
		}
		else {
			hasIcon = false;
		}
	}
	
	public String getTwoLetterCode() {
		return twoLetterCode;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
}
