package org.worldbank.seg_2g.worldbankapp;


import android.content.Context;

public class Country {

	//private Context context;
	
	private String name;
	private String threeLetterCode;
	private String twoLetterCode;
	
	// boolean for existence of country flag icon
	private final boolean hasIcon;
	private int flagId;
	
	public Country(String countryName, String threeLetterCode, String twoLetterCode, Context context) {
		//this.context = context;
		
		this.name = countryName;
		this.threeLetterCode = threeLetterCode;
		this.twoLetterCode = twoLetterCode;
		
		// check if flag icon exists and set boolean
		 checkFlag(context);
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
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
	public void checkFlag(Context context){
	if(twoLetterCode.equalsIgnoreCase("do")){
	flagId = context.getResources().getIdentifier(threeLetterCode.toLowerCase(), "drawable", context.getPackageName());
	}else{
	flagId = context.getResources().getIdentifier(twoLetterCode.toLowerCase(), "drawable", context.getPackageName());}
	}
	

	
}
