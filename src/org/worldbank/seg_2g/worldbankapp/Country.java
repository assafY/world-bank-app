package org.worldbank.seg_2g.worldbankapp;

import android.content.Context;

public class Country {

	//private Context context;
	
	private String name;
	private String threeLetterCode;
	private String twoLetterCode;
	
	// boolean for existence of country flag icon
	private final boolean hasIcon;
	private final int flagId;
	
	public Country(String countryName, String threeLetterCode, String twoLetterCode, Context context) {
		//this.context = context;
		
		this.name = countryName;
		this.threeLetterCode = threeLetterCode;
		this.twoLetterCode = twoLetterCode;
		
		// check if flag icon exists and set boolean
		flagId = context.getResources().getIdentifier(twoLetterCode.toLowerCase(), "drawable", context.getPackageName());
		if (flagId != 0) {
			hasIcon = true;
		}
		else {
			hasIcon = false;
		}
	}
	
	public String getTwoLetterCode() {
		return twoLetterCode;
	}
	
	public String getThreeLetterCode() {
		return threeLetterCode;
	}
	
	public int getFlagId() {
		return flagId;
	}
	
	public boolean hasFlagIcon() {
		return hasIcon;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
}
