package com.gpigc.core.event;

import com.gpigc.core.analysis.Result;

public class Event {
	private Result result;
	private String analysisEngineName;
	private String systemId;
	
	public Event(Result result, String analysisEngineName, String systemId) {
		this.result = result;
		this.analysisEngineName = analysisEngineName;
		this.systemId = systemId;
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public String getAnalysisEngineName() {
		return this.analysisEngineName;
	}
	
	public String getSystemId() {
		return this.systemId;
	}
}