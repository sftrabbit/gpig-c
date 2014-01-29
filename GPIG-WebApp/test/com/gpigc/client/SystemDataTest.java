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
import com.gpig.client.SystemData;

/**
 * @author Tom Davies
 */
public class SystemDataTest {
	
	private SystemData testData;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("Engine Temp", "100");
		payload.put("RPM", "1512");
		payload.put("Speed", "30");
		payload.put("Awesomness", "MAXIMUM");
		testData = new SystemData(
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
	 * Test method for {@link com.gpig.client.SystemData#SystemData(java.lang.String, java.util.Date, java.util.Map)}.
	 */
	@Test
	public void testSystemData() {
		fail("Not yet implemented");
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
		Map<String, String> payload = testData.getPayload();
		SystemData parsedData = SystemData.parseJSON(
				new StringReader(testData.toJSON()));
		assertEquals(systemID, parsedData.getSystemID());
		assertEquals(timestamp, parsedData.getTimeStamp());
		assertEquals(payload, parsedData.getPayload());
	}

}