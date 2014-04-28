package com.gpigc.core.event;

import java.util.Map;

import com.gpigc.core.ClientSystem;

public class DataEvent {
	
	private final Map<String, String> data;
	private final ClientSystem system;

	public DataEvent(Map<String, String> data, ClientSystem system){
		this.data = data;
		this.system = system;
		
	}

	public Map<String, String> getData() {
		return data;
	}

	public ClientSystem getSystem() {
		return system;
	}
}
