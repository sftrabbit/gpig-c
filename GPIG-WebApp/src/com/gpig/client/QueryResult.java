package com.gpig.client;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.gpig.server.SensorState;

import static com.gpig.client.DataJSONAttribute.*;

/**
 * The result of querying the database
 * 
 * @author GPIG-C
 */
public class QueryResult {

	private final String systemID;
	private final List<SensorState> records;

	/**
	 * @param systemID The ID of the system that was queried
	 * @param records The records matching the query
	 */
	public QueryResult(String systemID, List<SensorState> records){
		this.systemID = systemID;
		this.records = records;
		if (systemID == null) {
			throw new NullPointerException("SystemID null");
		}
		if (records == null) {
			throw new NullPointerException("Records list is null");
		}
	}

	/**
	 * @return The ID of the system to which these results pertain
	 */
	public String getSystemID() {
		return systemID;
	}

	/**
	 * @return The records matching the submitted query
	 */
	public List<SensorState> getRecords() {
		return records;
	}

	/**
	 * @return This QueryResult formatted as a JSON string
	 */
	public String toJSON() {
		StringWriter writer = new StringWriter();
		JsonFactory factory = new JsonFactory();
		JsonGenerator gen;
		try {
			gen = factory.createGenerator(writer);
			gen.writeStartObject();
			gen.writeStringField(JSON_SYSTEM_ID.getKey(), this.systemID);
			gen.writeArrayFieldStart(JSON_RECORD_KEY.getKey());
			for (SensorState record :records) {
				gen.writeStartObject();
				gen.writeStringField(JSON_SENSOR_ID.getKey(),record.getSensorID());
				gen.writeFieldName(JSON_CREATION_TIMESTAMP.getKey());
				gen.writeNumber(record.getCreationTimestamp().getTime());
				gen.writeFieldName(JSON_DB_TIMESTAMP.getKey());
				gen.writeNumber(record.getDatabaseTimestamp().getTime());
				gen.writeStringField(JSON_VALUE.getKey(),record.getValue());
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

	/**
	 * @param string A JSON string
	 * @return The QueryObject represented by the JSON given by the reader
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static QueryResult parseJSON(String string) 
			throws JsonParseException, IOException {
		return parseJSON(new StringReader(string)); 
	}
	
	/**
	 * @param reader A source to read the JSON string from
	 * @return The QueryObject represented by the JSON given by the reader
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static QueryResult parseJSON(Reader reader) 
			throws JsonParseException, IOException {
		
		String systemID = null;
		List<SensorState> entities = new ArrayList<>();;
		
		JsonFactory f = new JsonFactory();
		JsonParser parser = f.createParser(reader);
		parser.nextToken(); // Returns a start of object token
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String jsonKey = parser.getCurrentName();
			if (jsonKey.equals(JSON_SYSTEM_ID.getKey())) {
				parser.nextToken(); // Move to value
				systemID = parser.getText();
				continue;
			}
			if (jsonKey.equals(JSON_RECORD_KEY.getKey())) {
				parser.nextToken(); // Start array
				while (parser.nextToken() == JsonToken.START_OBJECT)
					parseRecord(parser, entities);
				continue;
			}
			throw new IllegalArgumentException("Unrecognised JSON key: "
					+ jsonKey);
		}
		parser.close();
		return new QueryResult(systemID, entities);
	}
	
	/**
	 * Reads the next record into the records list
	 * 
	 * @param parser A parser
	 * @param records A list to be populated with records
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private static void parseRecord(JsonParser parser,
			List<SensorState> records) throws JsonParseException,IOException {
		String sensorID = null;
		Date creationTimestamp = null;
		Date databaseTimestamp = null;
		String value = null;
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String jsonKey = parser.getCurrentName();
			parser.nextToken(); // Move to value
			String jsonValue = parser.getText();

			if (jsonKey.equals(JSON_SENSOR_ID.getKey())) {
				if (sensorID != null)// Shouldn't have already seen a sensor ID
					throw new IllegalArgumentException("Duplicate "+ JSON_SENSOR_ID);
				sensorID = jsonValue;
				continue;
			}
			if (jsonKey.equals(JSON_CREATION_TIMESTAMP.getKey())) {
				if (creationTimestamp != null) // Shouldn't have already seen a sensor value
					throw new IllegalArgumentException("Duplicate "+ JSON_CREATION_TIMESTAMP);
				creationTimestamp = new Date(Long.parseLong(jsonValue));
				continue;
			}
			if (jsonKey.equals(JSON_DB_TIMESTAMP.getKey())) {
				if (databaseTimestamp != null) // Shouldn't have already seen a sensor value
					throw new IllegalArgumentException("Duplicate "+ JSON_DB_TIMESTAMP);
				databaseTimestamp = new Date(Long.parseLong(jsonValue));
				continue;
			}
			if (jsonKey.equals(JSON_VALUE.getKey())) {
				if (value != null) // Shouldn't have already seen a sensor value
					throw new IllegalArgumentException("Duplicate "+ JSON_VALUE);
				value = jsonValue;
				continue;
			}
			throw new IllegalArgumentException(
					"Unrecognised JSON sensor key: " + jsonKey);
		}

		// Store the record
		records.add(new SensorState(
				sensorID, 
				creationTimestamp, 
				databaseTimestamp, 
				value));
	}
}
