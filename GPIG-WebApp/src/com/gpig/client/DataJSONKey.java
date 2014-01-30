package com.gpig.client;

/**
 * The keys used when reading or writing JSON
 * 
 * @author Tom Davies
 */
public enum DataJSONKey {
	
	JSON_SYSTEM_ID("SystemID"),
	JSON_SENSOR_ID("SensorID"),
	JSON_CREATION_TIMESTAMP("CreationTimestamp"),
	JSON_DB_TIMESTAMP("DatabaseTimestamp"),
	JSON_VALUE("Value"),
	JSON_PAYLOAD("Sensors"),
	JSON_RECORD_KEY("Records");
	
	private final String key;

	/**
	 * @param key The String used as the key for this field name when encoded
	 * as JSON
	 */
	private DataJSONKey(String key){
		this.key = key;
	}

	/**
	 * @return The String used as the key for this field name when encoded
	 * as JSON
	 */
	public String getKey() {
		return key;
	}
	
	@Override
	public String toString(){
		return key;
	}
}
