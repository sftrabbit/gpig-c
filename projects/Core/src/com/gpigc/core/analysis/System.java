package com.gpigc.core.analysis;

import java.util.Observable;

public class System extends Observable {
	
	private String systemId;
	
	public System(String systemId) {
		this.systemId = systemId;
	}
	
	public void systemStateUpdate() {
		setChanged();
		notifyObservers();
	}
	
	public String getSystemId() {
		return systemId;
	}
}