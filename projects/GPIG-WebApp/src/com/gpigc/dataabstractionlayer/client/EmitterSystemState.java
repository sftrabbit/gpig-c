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
		JsonFactory f = new JsonFactory();
		JsonParser parser = f.createParser(reader);
		EmitterSystemState state = readTokens(parser);
		parser.close();
		return state;
	}

	/**
	 * @param parser A JSON parser
	 * @return An EmitterSystemState
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static EmitterSystemState readTokens(JsonParser parser) 
			throws JsonParseException, IOException {
		String systemID = null;
		Date timeStamp = null;
		Map<String, String> payload = new HashMap<>();
		parser.nextToken(); // Returns a start of object token
		System.out.println("Object start token = " + parser.getCurrentToken());
		parser.nextToken();
		while (parser.getCurrentToken() != JsonToken.END_OBJECT &&
				parser.getCurrentToken() != JsonToken.END_ARRAY) {
			System.out.println("Last main loop token = " + parser.getCurrentToken());
			String jsonKey = parser.getCurrentName();
			System.out.println("Key = " + jsonKey);
			if (jsonKey.equals(JSON_SYSTEM_ID.getKey())) {
				parser.nextToken(); // Move to value
				systemID = parser.getText();
				System.out.println("System ID = " + parser.getText());
				parser.nextToken();
				continue;
			}
			if (jsonKey.equals(JSON_CREATION_TIMESTAMP.getKey())) {
				parser.nextToken(); // Move to value
				System.out.println(parser.getValueAsString());
				timeStamp = new Date(parser.getLongValue());
				parser.nextToken();
				continue;
			}
			if (jsonKey.equals(JSON_PAYLOAD.getKey())) {
				parser.nextToken(); // Start array
				System.out.println("Sensor start array = " + parser.getCurrentToken());
				while (parser.nextToken() == JsonToken.START_OBJECT) {
					// Start object
					parseSensor(parser, payload);
				}
				continue;
			}
			throw new IllegalArgumentException("Unrecognised JSON key: "
					+ jsonKey);
		}
		EmitterSystemState state = new EmitterSystemState(systemID, timeStamp, payload);
		return state;
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
				System.out.println("Sensor ID = " + sensorID);
				continue;
			}
			if (jsonKey.equals(JSON_VALUE.getKey())) {
				if (sensorValue != null) // Shouldn't have already seen a sensor value
					throw new IllegalArgumentException("Duplicate "+ JSON_VALUE);
				sensorValue = jsonValue;
				System.out.println("Sensor value = " + sensorValue);
				continue;
			}
			throw new IllegalArgumentException(
					"Unrecognised JSON sensor key: " + jsonKey);
		}
		System.out.println("Left sensor loop on " + parser.getCurrentToken());
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
