package com.gpigc.core.analysis;

import java.util.Map;

public class Result {
	private Map<String, String> dataToSave;
	private boolean notify;
	
	public Result(Map<String,String> dataToSave, boolean notify) {
		this.dataToSave = dataToSave;
		this.notify = notify;
	}
	
	public Map<String,String> getDataToSave() {
		return dataToSave;
	}

	public boolean isNotify() {
		return notify;
	}
}
