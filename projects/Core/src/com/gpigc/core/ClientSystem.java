package com.gpigc.core;

import java.util.List;
import java.util.Map;

public class ClientSystem {

	private final String id;
	private final List<ClientSensor> sensors;
	private final List<String> registeredEngineNames;
	private final Map<Parameter,String> parameters;

	public ClientSystem(String systemID, List<ClientSensor> sensors, 
			List<String> registeredEngineNames, Map<Parameter,String> parameters){
		this.id = systemID;
		this.sensors = sensors;
		this.registeredEngineNames = registeredEngineNames;
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj))
			return true;
		
		if(obj instanceof ClientSystem){
			if(id.equals(((ClientSystem)obj).id))
				return true;
		}
		return false;
	}

	public String getID() {
		return id;
	}

	public List<ClientSensor> getSensors() {
		return sensors;
	}

	public boolean hasSensorWithID(String id) {
		for(ClientSensor sensor: sensors){
			if(sensor.getID().equals(id))
				return true;
		}
		return false;
	}
	
	public ClientSensor getSensorWithID(String id) {
		for(ClientSensor sensor: sensors){
			if(sensor.getID().equals(id))
				return sensor;
		}
		return null;
	}

	public List<String> getRegisteredEngineNames() {
		return registeredEngineNames;
	}

	public Map<Parameter,String> getParameters() {
		return parameters;
	}

}
