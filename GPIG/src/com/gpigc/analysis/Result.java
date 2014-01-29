package com.gpigc.analysis;

import java.util.Map;

public class Result {
	private Map<? , ?> dataToSave;
	private boolean notify;
	
	public Result(Map<? , ?> dataToSave, boolean notify) {
		this.dataToSave = dataToSave;
		this.notify = notify;
	}
	
	public Map<? , ?> getDataToSave() {
		return dataToSave;
	}

	public boolean isNotify() {
		return notify;
	}
	
	public void process() {
		
	}
}
