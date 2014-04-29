package com.gpigc.core;

import java.util.List;

public class ClientSystem {

	private final String id;
	private final List<ClientSensor> sensors;

	public ClientSystem(String systemID, List<ClientSensor> sensors){
		this.id = systemID;
		this.sensors = sensors;
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

}
