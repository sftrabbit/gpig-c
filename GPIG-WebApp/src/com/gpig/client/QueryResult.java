package com.gpig.client;

import java.util.ArrayList;

public class QueryResult {
	private final String systemID;
	private final ArrayList<DBRecord> entities;
	
	public QueryResult(String systemID, ArrayList<DBRecord> entities){
		this.systemID = systemID;
		this.entities = entities;
	}

	public String getSystemID() {
		return systemID;
	}

	public ArrayList<DBRecord> getEntities() {
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
