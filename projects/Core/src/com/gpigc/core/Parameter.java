package com.gpigc.core;

public enum Parameter {

	LOWER_BOUND, UPPER_BOUND, NUM_RECORDS, EXPRESSION, PHONE_IP;

	public static boolean isValid(String name) {
		for(Parameter param: Parameter.values()){
			if(param.name().equals(name))
				return true;
		}
		return false;
	}
}
