package com.gpigc.database;

import java.util.Map;

public class SystemData {
	
<<<<<<< HEAD
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
=======
	protected String systemId;
	protected long timestamp;
	protected Map<String, String> payload;
	
	public SystemData(String systemId, long timestamp, Map<String, String> payload)
	{
		this.systemId = systemId;
		this.timestamp = timestamp;
		this.payload = payload;
	}
	
	public String getSystemID() {
		return systemId;
	}
	
	public long getTimeStamp() {
		return timestamp;
	}
	
	public Map<String, String> getPayload() {
>>>>>>> 570c24de4512b0357252d47a726226c73d45cf49
		return payload;
	}
}