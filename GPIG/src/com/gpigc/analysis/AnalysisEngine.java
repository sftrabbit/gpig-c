package com.gpigc.analysis;

import java.util.List;

import com.gpigc.database.SystemDataGateway;

abstract class AnalysisEngine {
	
	protected List<String> associatedSystems;
	
	protected SystemDataGateway database;
	
	public List<String> getAssociatedSystems() {
		return associatedSystems;
	}
	
	public abstract Result analyse();
}