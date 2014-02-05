package com.gpigc.core.analysis;

import java.util.List;

import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

/**
 * Provides the basic functionality and abstract methods that all analysis engines require
 * 
 * @author GPIGC
 */
public abstract class AnalysisEngine {
	
	protected String engineName;
	
	protected List<String> associatedSystems;
	
	protected SystemDataGateway database;
	
	/**
	 * @return A list of systems ID associated with the instance of the analysis engine
	 */
	public List<String> getAssociatedSystems() {
		return associatedSystems;
	}
	
	/**
	 * @return The name of the analysis engine
	 */
	public String getEngineName() {
		return engineName;
	}
	
	/**
	 * Performs analysis upon the given data
	 * 
	 * @return The result of analysis in a result object
	 */
	public abstract Result analyse();
}