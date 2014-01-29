package com.gpigc.analysis;

import java.util.List;

public class Result {
	private List<Object> dataToSave;
	private boolean notify;
	
	public Result(List<Object> dataToSave, boolean notify) {
		this.dataToSave = dataToSave;
		this.notify = notify;
	}
	
	public List<Object> getDataToSave() {
		return dataToSave;
	}

	public boolean isNotify() {
		return notify;
	}
	
	public void pushNotification() {
		
	}
}
