package com.gpigc.core.analysis;

import java.util.List;

import com.gpig.client.SystemDataGateway;

public abstract class AnalysisEngine {
	
	protected List<String> associatedSystems;
	
	protected SystemDataGateway database;
	
	public List<String> getAssociatedSystems() {
		return associatedSystems;
	}
	
	public abstract Result analyse();
}