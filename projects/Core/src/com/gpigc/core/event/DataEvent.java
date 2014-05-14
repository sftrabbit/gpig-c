package com.gpigc.core.event;

import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;

public class DataEvent {
	
	private final Map<Parameter, String> data;
	private final ClientSystem system;

	public DataEvent(Map<Parameter, String> data, ClientSystem system){
		this.data = data;
		this.system = system;
		
	}

	public Map<Parameter, String> getData() {
		return data;
	}

	public ClientSystem getSystem() {
		return system;
	}
}
