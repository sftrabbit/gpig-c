package com.gpig.server;

import java.util.Date;

public class DBRecord {
	
	private final String sensorID;
	private final Date creationTimestamp;
	private final Date databaseTimestamp;
	private final String value;

	public DBRecord(String sensorID, Date creationTimeStamp, Date databaseStamp, String value){
		this.sensorID = sensorID;
		this.creationTimestamp = creationTimeStamp;
		this.databaseTimestamp = databaseStamp;
		this.value = value;
	}

	public String getSensorID() {
		return sensorID;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public Date getDatabaseTimestamp() {
		return databaseTimestamp;
	}

	public String getValue() {
		return value;
	}
}
