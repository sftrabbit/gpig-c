package com.gpigc.dataabstractionlayer.client;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static com.gpigc.dataabstractionlayer.client.DataJSONAttribute.*;

/**
 * Wraps up the state of a data emitter system that has been read at one point 
 * in time and is about to be written to the database
 * 
 * @author Tom Davies
 */
public class EmitterSystemState {

	private final String systemID;
	private final Date timestamp;
	private final Map<String, String> sensorReadings;

	/**
	 * @param systemID  The system this data is about
	 * @param timestamp The time that this data was received
	 * @param payload The state of this system, i.e. the values of 0 or
	 * more sensors at the given time
	 */
	public EmitterSystemState(String systemID, Date timestamp,
			Map<String, String> payload) {
		if (systemID == null)
			throw new NullPointerException("System ID is null");
		if (timestamp == null)
			throw new NullPointerException("Timestamp is null");
		if (payload == null)
			throw new NullPointerException("Payload is null");

		this.systemID = systemID;
		this.timestamp = timestamp;
		this.sensorReadings = Collections.unmodifiableMap(payload);
	}

	/**
	 * @return The ID of the system from which we have read this data
	 */
	public String getSystemID() {
		return systemID;
	}

	/**
	 * @return The time at which the data was received by the HUMS
	 */
	public Date getTimeStamp() {
		return timestamp;
	}

	/**
	 * @return The value read by each sensor
	 */
	public Map<String, String> getSensorReadings() {
		return sensorReadings;
	}

	/**
	 * @param reader
	 *            A data source for a JSON string
	 * @return The SystemData object represented by this JSON string
	 * @throws JsonParseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static EmitterSystemState parseJSON(Reader reader)
			throws JsonParseException, IOException, ParseException {

		String systemID = null;
		Date timeStamp = null;
		Map<String, String> payload = new HashMap<>();

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
			if (jsonKey.equals(JSON_CREATION_TIMESTAMP.getKey())) {
				parser.nextToken(); // Move to value
				timeStamp = new Date(parser.getLongValue());
				continue;
			}
			if (jsonKey.equals(JSON_PAYLOAD.getKey())) {
				parser.nextToken(); // Start array
				while (parser.nextToken() == JsonToken.START_OBJECT)
					// Start object
					parseSensor(parser, payload);
				continue;
			}
			throw new IllegalArgumentException("Unrecognised JSON key: "
					+ jsonKey);
		}
		parser.close();
		return new EmitterSystemState(systemID, timeStamp, payload);
	}

	/**
	 * Parses each sensor from SystemData JSON
	 * @param parser   The parser being used
	 * @param payload  The partially read payload
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private static void parseSensor(JsonParser parser,
			Map<String, String> payload) throws JsonParseException,IOException {
		String sensorID = null;
		String sensorValue = null;
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
			if (jsonKey.equals(JSON_VALUE.getKey())) {
				if (sensorValue != null) // Shouldn't have already seen a sensor value
					throw new IllegalArgumentException("Duplicate "+ JSON_VALUE);
				sensorValue = jsonValue;
				continue;
			}
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

	/**
	 * @return A JSON representation of this SystemData object
	 * @throws IOException
	 */
	public String toJSON() throws IOException {
		StringWriter writer = new StringWriter();
		JsonFactory factory = new JsonFactory();
		JsonGenerator gen = factory.createGenerator(writer);
		gen.writeStartObject();
		gen.writeStringField(JSON_SYSTEM_ID.getKey(), this.systemID);
		gen.writeFieldName(JSON_CREATION_TIMESTAMP.getKey());
		gen.writeNumber(this.timestamp.getTime());
		gen.writeArrayFieldStart(JSON_PAYLOAD.getKey());
		for (String key : this.sensorReadings.keySet()) {
			String value = sensorReadings.get(key);
			gen.writeStartObject();
			gen.writeStringField(JSON_SENSOR_ID.getKey(), key);
			gen.writeStringField(JSON_VALUE.getKey(), value);
			gen.writeEndObject();
		}
		gen.writeEndArray();
		gen.writeEndObject();
		gen.close();
		return writer.toString();
	}

}