package com.gpigc.core;

import java.util.Map;

public class ClientSensor {

	private final Map<SensorParameter,String> parameters;
	private final String id;
	
	public ClientSensor(String id, Map<SensorParameter, String>parameters){
		this.id = id;
		this.parameters = parameters;
	}

	public String getID() {
		return id;
	}

	public Map<SensorParameter,String> getParameters() {
		return parameters;
	}
}
