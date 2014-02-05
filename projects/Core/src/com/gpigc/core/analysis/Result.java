package com.gpigc.core.analysis;

import java.util.Map;

/**
 * Provides a class for storing analysis results and data to be saved
 * 
 * @author GPIGC
 */
public class Result {
	private Map<String, String> dataToWrite;
	private boolean notify;
	
	/**
	 * Initialising result object
	 * 
	 * @param dataToSave	The data to be persisted back to the database
	 * @param notify		True if notification is to be raised, false otherwise
	 */
	public Result(Map<String,String> dataToSave, boolean notify) {
		this.dataToWrite = dataToSave;
		this.notify = notify;
	}
	
	/**
	 * @return	The data to be persisted to the database
	 */
	public Map<String,String> getDataToSave() {
		return dataToWrite;
	}

	/**
	 * @return	True if notification required
	 */
	public boolean isNotify() {
		return notify;
	}
}
