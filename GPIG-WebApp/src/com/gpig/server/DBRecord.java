package com.gpig.server;

import java.util.Date;

public class DBRecord {
	
	private final String sensorID;
	private final Date creationTimeStamp;
	private final Date databaseStamp;
	private final String value;

	public DBRecord(String sensorID, Date creationTimeStamp, Date databaseStamp, String value){
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
