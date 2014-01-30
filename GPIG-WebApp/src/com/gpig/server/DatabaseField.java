package com.gpig.server;

public enum DatabaseField {
	SYSTEM_ID("SystemID"),
	SENSOR_ID("SensorID"),
	CREATION_TIMESTAMP("CreationTimestamp"),
	DB_TIMESTAMP("DatabaseTimestamp"),
	VALUE("Value"),
	NUM_RECORDS("NumRecords"),
	ENTITY("DataEntity"),
	START_TIME("StartTimeStamp"),
	END_TIME("EndTimeStamp");
	
	private final String key;

	private DatabaseField(String key){
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	@Override
	public String toString(){
		return key;
	}
}
