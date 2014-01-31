package com.gpigc.core.eventnotify;

import com.gpigc.core.analysis.Result;

public class EventNotify {
	private Result result;
	private String analysisEngineName;
	private String componentID;
	
	public EventNotify(Result result, String analysisEngineName, String componentID) throws InvalidEventNotifyException {
		if(result.isNotify() == false) {
			throw new InvalidEventNotifyException("Event Notification being built when isNotify() is false!");
		}
		this.result = result;
		this.analysisEngineName = analysisEngineName;
		this.componentID = componentID;
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public String getAnalysisEngineName() {
		return this.analysisEngineName;
	}
	
	public String getComponentID() {
		return this.componentID;
	}
}