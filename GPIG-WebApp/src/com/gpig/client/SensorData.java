package com.gpig.client;

import java.util.Date;

public class SensorData {
	
	private final String sensorID;
	private final Date creationTimeStamp;
	private final Date databaseStamp;
	private final String value;

	public SensorData(String sensorID, Date creationTimeStamp, Date databaseStamp, String value){
		this.sensorID = sensorID;
		this.creationTimeStamp = creationTimeStamp;
		this.databaseStamp = databaseStamp;
		this.value = value;
	}

	public String getSensorID() {
		return sensorID;
	}

	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}

	public Date getDatabaseStamp() {
		return databaseStamp;
	}

	public String getValue() {
		return value;
	}
}
