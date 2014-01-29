package com.gpig.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.gwt.dev.util.collect.HashMap;

public class SystemData {

	private static final String SYSTEM_ID_KEY = "SystemID";
	private static final String SENSORS_KEY = "Sensors";
	private static final String CREATION_TIMESTAMP_KEY = "CreationTimestamp";
	private static final String SENSOR_ID_KEY = "SensorID";
	private static final String SENSOR_VALUE_KEY = "SensorValue";

	private static final SimpleDateFormat dateFormat = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	private final String systemID;
	private final Date timeStamp;
	private final Map<String, String> payload;

	/**
	 * @param systemID The system this data is about
	 * @param timeStamp The time that this data was received
	 * @param payload The contents of this data, i.e. the values of 0 or more
	 * sensors
	 */
	public SystemData(
			String systemID, 
			Date timeStamp, 
			Map<String, String> payload) {
		if (systemID == null) {
			throw new NullPointerException("System ID is null");
		}
		if (timeStamp == null) {
			throw new NullPointerException("Timestamp is null");
		}
		if (payload == null) {
			throw new NullPointerException("Payload is null");
		}
		this.systemID = systemID;
		this.timeStamp = timeStamp;
		this.payload = Collections.unmodifiableMap(payload);
	}

	public String getSystemID() {
		return systemID;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public Map<String, String> getPayload() {
		return payload;
	}

	public static SystemData parseJSON(BufferedReader bufferedReader) 
			throws JsonParseException, IOException, ParseException {

		String systemID = null;
		Date timeStamp = null;
		Map<String, String> payload = new HashMap<>();

		JsonFactory f = new JsonFactory();
		JsonParser parser = f.createParser(bufferedReader);
		parser.nextToken(); // Returns a start of object token
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String jsonKey = parser.getCurrentName();
			parser.nextToken(); // Move to value
			String jsonValue = parser.getCurrentName();
			switch (jsonKey) {
			case SYSTEM_ID_KEY:
				systemID = jsonValue;
				break;
			case CREATION_TIMESTAMP_KEY:
				timeStamp = dateFormat.parse(jsonValue);
				break;
			case SENSORS_KEY:
				while (parser.nextToken() != JsonToken.END_ARRAY) {
					parseSensor(parser, payload);
				}
				break;
			default:
				throw new IllegalArgumentException(
						"Unrecognised JSON key: " + jsonKey);
			}
		}
		parser.close(); // ensure resources get cleaned up timely and properly
		return new SystemData(systemID, timeStamp, payload);
	}

	private static void parseSensor(
			JsonParser parser, 
			Map<String, String> payload) 
					throws JsonParseException, IOException {
		String sensorID = null;
		String sensorValue = null;
		parser.nextToken(); // Returns a start of object token
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String jsonKey = parser.getCurrentName();
			parser.nextToken(); // Move to value
			String jsonValue = parser.getCurrentName();
			switch (jsonKey) {
			case SENSOR_ID_KEY:
				// We shouldn't have already seen a sensor ID
				if (sensorID != null) {
					throw new IllegalArgumentException(
							"Duplicate " + SENSOR_ID_KEY);
				}
				sensorID = jsonValue;
				break;
			case SENSOR_VALUE_KEY:
				// We shouldn't have already seen a sensor value
				if (sensorValue != null) {
					throw new IllegalArgumentException(
							"Duplicate " + SENSOR_VALUE_KEY);
				}
				sensorValue = jsonValue;
				break;
			default:
				throw new IllegalArgumentException(
						"Unrecognised JSON sensor key: " + jsonKey);
			}
			// Write values to the payload map
			if (sensorID != null && sensorValue != null) {
				payload.put(sensorID, sensorValue);
				sensorID = null;
				sensorValue = null;	
			}
		}
	}

	public String toJSON(){
		//TODO TOM
		/*JsonFactory f = new JsonFactory();
		JsonGenerator g = f.createJsonGenerator(new File("user.json"));
		g.writeStartObject();
		g.writeObjectFieldStart("name");
		g.writeStringField("first", "Joe");
		g.writeStringField("last", "Sixpack");
		g.writeEndObject(); // for field 'name'
		g.writeStringField("gender", Gender.MALE);
		g.writeBooleanField("verified", false);
		g.writeFieldName("userImage"); // no 'writeBinaryField' (yet?)
		byte[] binaryData = ...;
		g.writeBinary(binaryData);
		g.writeEndObject();
		g.close(); // important: will force flushing of output, close underlying output stream
		 */
		return null;
	}

}
