package com.gpig.client;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.gpig.server.DBRecord;
import static com.gpig.client.DataJSONKey.*;
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
		StringWriter writer = new StringWriter();
		JsonFactory factory = new JsonFactory();
		JsonGenerator gen;
		try {
			gen = factory.createGenerator(writer);
			gen.writeStartObject();
			gen.writeStringField(JSON_SYSTEM_ID.getKey(), this.systemID);
			gen.writeArrayFieldStart(JSON_RECORD_KEY.getKey());
			for (DBRecord record :entities) {
				gen.writeStartObject();
				gen.writeStringField(JSON_SENSOR_ID.getKey(),record.getSensorID());
				gen.writeFieldName(JSON_CREATION_TIMESTAMP.getKey());
				gen.writeNumber(record.getCreationTimeStamp().getTime());
				gen.writeFieldName(JSON_DB_TIMESTAMP.getKey());
				gen.writeNumber(record.getDatabaseStamp().getTime());
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
