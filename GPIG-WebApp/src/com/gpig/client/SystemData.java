package com.gpig.client;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public class SystemData {

	private final String systemID;
	private final Date timeStamp;
	private final Map<String, ? extends Object> payload;
	
	public SystemData(String systemID, Date timeStamp, Map<String, ? extends Object> payload) {
		this.systemID = systemID;
		this.timeStamp = timeStamp;
		this.payload = payload;
	}

	public String getSystemID() {
		return systemID;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public Map<String, ? extends Object> getPayload() {
		return payload;
	}
	
	public static SystemData parseJSON(BufferedReader bufferedReader){
		//TODO TOM
		return null;
	}
	
	public String toJSON(){
		//TODO TOM
		return null;
	}
	
}
