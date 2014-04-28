package com.gpigc.core;

public enum SensorParameter {

	LOWER_BOUND("LowerBound"),
	UPPER_BOUND("UpperBound");
	
	
	private final String key;

	/**
	 * @param key The String used as the key for this field name when encoded
	 * as JSON
	 */
	private SensorParameter(String key){
		this.key = key;
	}

	/**
	 * @return The String used as the key for this field name when encoded
	 * as JSON
	 */
	public String getKey() {
		return key;
	}
	
	@Override
	public String toString(){
		return key;
	}
}
