package com.gpigc.database;

import java.util.Date;
import java.util.Map;

public class SystemData {
	
	private Map<String, Integer> payload;
	private Date timeStamp;
	private String systemId;
	
	public SystemData(Map<String, Integer> payload, Date timeStamp, String systemId) {
		this.payload = payload;
		this.timeStamp = timeStamp;
		this.systemId = systemId;
	}
	
	public String getSystemId() {
		return systemId;
	}
	
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	public Map<String, Integer> getPayload() {
		return payload;
	}
}