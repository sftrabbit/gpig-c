package com.gpigc.core;

import java.util.Map;

public class ClientSensor {

	private final Map<Parameter,String> parameters;
	private final String id;
	
	public ClientSensor(String id, Map<Parameter, String>parameters){
		this.id = id;
		this.parameters = parameters;
	}

	public String getID() {
		return id;
	}

	public Map<Parameter,String> getParameters() {
		return parameters;
	}
}
