package com.gpigc.core.event;

import com.gpigc.core.analysis.Result;

/**
 * Provides a class for storing an event, consisting of data, the analysis engine that generated it, and the system that triggered it
 * 
 * @author GPIGC
 */
public class Event {
	private Result result;
	private String analysisEngineName;
	private String systemId;
	
	/**
	 * Initialises the event object
	 * 
	 * @param result             The result data from the analysis engine that generated the event
	 * @param analysisEngineName The name of the analysis engine that generated the event
	 * @param systemId           The ID of the system that triggered the analysis
	 */
	public Event(Result result, String analysisEngineName, String systemId) {
		this.result = result;
		this.analysisEngineName = analysisEngineName;
		this.systemId = systemId;
	}
	
	/**
	 * @return A result object from the analysis engine that generated the event
	 */
	public Result getResult() {
		return this.result;
	}
	
	/**
	 * @return The name of the analysis engine that generated the event
	 */
	public String getAnalysisEngineName() {
		return this.analysisEngineName;
	}
	
	/**
	 * @return The ID of the system that triggered the analysis
	 */
	public String getSystemId() {
		return this.systemId;
	}
}