package com.gpig.client;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class QueryResult {
	private final static String RECORD_KEY = "Records";
	public  static final String DB_TIMESTAMP_KEY 			= "DatabaseTimestamp";

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
		StringWriter writer = new StringWriter();
		JsonFactory factory = new JsonFactory();
		JsonGenerator gen;
		try {
			gen = factory.createGenerator(writer);
			gen.writeStartObject();
			gen.writeStringField(SystemData.SYSTEM_ID_KEY, this.systemID);
			gen.writeArrayFieldStart(RECORD_KEY);
			for (DBRecord record :entities) {
				gen.writeStartObject();
				gen.writeStringField(SystemData.SENSOR_ID_KEY, record.getSensorID());
				gen.writeStringField(SystemData.CREATION_TIMESTAMP_KEY, SystemData.DATE_FORMAT.format(record.getCreationTimeStamp()));
				gen.writeStringField(DB_TIMESTAMP_KEY, SystemData.DATE_FORMAT.format(record.getDatabaseStamp()));
				gen.writeEndObject();
			}
			gen.writeEndArray();
			gen.writeEndObject();
			gen.close();
			return writer.toString();
		} catch (IOException e) {
			return "ERROR";
		}
	}

	public static QueryResult parseJSON() {
		//TODO TOM
		return null;
	}
}
