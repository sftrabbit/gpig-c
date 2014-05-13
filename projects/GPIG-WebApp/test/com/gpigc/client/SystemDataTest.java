/**
 * 
 */
package com.gpigc.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.appengine.api.datastore.Text;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;

/**
 * @author Tom Davies
 */
public class SystemDataTest {
	
	private EmitterSystemState testData;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Map<String,Text> payload = new HashMap<String, Text>();
		payload.put("Engine Temp", new Text("100"));
		payload.put("RPM",  new Text("1512"));
		payload.put("Speed",  new Text("30"));
		payload.put("Awesomness",  new Text("MAXIMUM"));
		testData = new EmitterSystemState(
				"TestSystemID", 
				new Date(), 
				payload);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		testData = null;
	}

	/**
	 * Test going to JSON and back
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JsonParseException 
	 */
	@Test
	public void testJSON() throws JsonParseException, IOException, ParseException {
		String systemID = testData.getSystemID();
		Date timestamp = testData.getTimeStamp();
		Map<String, Text> payload = testData.getSensorReadings();
		EmitterSystemState parsedData = EmitterSystemState.parseJSON(
				new StringReader(testData.toJSON()));
		assertEquals(systemID, parsedData.getSystemID());
		assertEquals(timestamp, parsedData.getTimeStamp());
		assertEquals(payload, parsedData.getSensorReadings());
	}

}
