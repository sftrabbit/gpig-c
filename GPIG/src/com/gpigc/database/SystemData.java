package com.gpigc.database;

import java.util.Map;

public class SystemData {
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
		return payload;
	}
}
