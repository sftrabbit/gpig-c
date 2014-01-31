package com.gpigc.core.analysis;

import java.util.Map;

public class Result {
	private Map<String, String> dataToWrite;
	private boolean notify;
	
	public Result(Map<String,String> dataToSave, boolean notify) {
		this.dataToWrite = dataToSave;
		this.notify = notify;
	}
	
	public Map<String,String> getDataToSave() {
		return dataToWrite;
	}

	public boolean isNotify() {
		return notify;
	}
}
