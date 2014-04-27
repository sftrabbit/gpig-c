package com.gpigc.core.analysis;

import java.util.List;
import java.util.Map;

public class ClientSystem {

	private final String systemID;
	private final List<String> sensorIDs;
	private final Map<String, Map<String, Object>> parameters;

	public ClientSystem(String systemID, List<String> sensorIDs, Map<String, Map<String, Object>> parameters){
		this.systemID = systemID;
		this.sensorIDs = sensorIDs;
		this.parameters = parameters;
	}

	public String getSystemID() {
		return systemID;
	}

	public List<String> getSensorIDs() {
		return sensorIDs;
	}

	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj))
			return true;
		
		if(obj instanceof ClientSystem){
			if(systemID.equals(((ClientSystem)obj).systemID))
				return true;
		}
		return false;
	}

	public Map<String, Map<String, Object>> getParameters() {
		return parameters;
	}
}
