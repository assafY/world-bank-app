package org.worldbank.seg_2g.worldbankapp;

public class Settings {

	public static final int NUMBER_OF_ATTRIBUTES = 8;

	public static final int POPULATION = 1;
	public static final int URBAN_RURAL = 2;
	public static final int ENERGY_PRODUCTION = 3;
	public static final int ENERGY_USE = 4;
	public static final int FOSSIL_FUEL = 5;
	public static final int FOREST_AREA = 6;
	public static final int CO2_EMISSIONS = 7;
	public static final int CH4_EMISSIONS = 8;

	public static final String[] Attributes = { "8", "Population",
			"Urban population (%)", "Energy Production (kt oil equiv.)", "Energy Use (kt oil equiv.)",
			"Fossil fuel (%)", "Forest area (sq. km)", "CO2 Emissions (kt)", "CH4 Emissions (kt CO2 equiv.)" };
	
	public static final int MIN_YEAR = 1960;
	public static final int MAX_YEAR = 2013;
	
	// TODO: Create constant ints for every query

}
