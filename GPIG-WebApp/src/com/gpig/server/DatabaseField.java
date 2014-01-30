package com.gpig.server;

/**
 * The keys used for the fields stored in the datastore
 * 
 * @author Tom Davies
 */
public enum DatabaseField {
	
	SYSTEM_ID("SystemID"),
	SENSOR_ID("SensorID"),
	CREATION_TIMESTAMP("CreationTimestamp"),
	DB_TIMESTAMP("DatabaseTimestamp"),
	VALUE("Value"),
	NUM_RECORDS("NumRecords"),
	ENTITY("DataEntity"),
	START_TIME("StartTimestamp"),
	END_TIME("EndTimestamp");
	
	private final String key;

	/**
	 * @param key The String used as a key when referring to this feild in the
	 * datastore
	 */
	private DatabaseField(String key){
		this.key = key;
	}

	/**
	 * @return The String used as a key when referring to this feild in the
	 * datastore
	 */
	public String getKey() {
		return key;
	}
	
	@Override
	public String toString(){
		return key;
	}
}
