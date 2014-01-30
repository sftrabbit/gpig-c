package com.gpig.client;

import java.util.ArrayList;

public class QueryResult {
	private final String systemID;
	private final ArrayList<SensorData> entities;
	
	public QueryResult(String systemID, ArrayList<SensorData> entities){
		this.systemID = systemID;
		this.entities = entities;
	}

	public String getSystemID() {
		return systemID;
	}

	public ArrayList<SensorData> getEntities() {
		return entities;
	}

	public String toJSON() {
		//TODO TOM
		return null;
	}
	
	public static QueryResult parseJSON() {
		//TODO TOM
		return null;
	}
}
