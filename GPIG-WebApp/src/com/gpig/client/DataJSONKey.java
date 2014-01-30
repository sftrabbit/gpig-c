package com.gpig.client;

public enum DataJSONKey {
	JSON_SYSTEM_ID("SystemID"),
	JSON_SENSOR_ID("SensorID"),
	JSON_CREATION_TIMESTAMP("CreationTimestamp"),
	JSON_DB_TIMESTAMP("DatabaseTimestamp"),
	JSON_VALUE("Value"),
	JSON_PAYLOAD("Sensors"),
	JSON_RECORD_KEY("Records");
	private final String key;

	private DataJSONKey(String key){
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
