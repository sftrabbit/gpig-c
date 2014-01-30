package com.gpigc.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.gpig.client.QueryResult;
import com.gpig.client.SensorState;

public class QueryResultTest {

	private QueryResult testData;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		String systemID = "SomeSystemTestID";
		List<SensorState> records = new ArrayList<>();
		records.add(new SensorState(
				"SensorA", new Date(14334L), new Date(14231L), "SensorAValue"));
		records.add(new SensorState(
				"SensorB", new Date(15000L), new Date(15001L), "SensorBValue"));
		records.add(new SensorState(
				"SensorC", new Date(100898L), new Date(100891L), "SensorCValue"));
		records.add(new SensorState(
				"SensorD", new Date(133712L), new Date(133719L), "SensorDValue"));
		testData = new QueryResult(systemID, records);
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
		List<SensorState> records = testData.getRecords();
		QueryResult parsedData = QueryResult.parseJSON(
				new StringReader(testData.toJSON()));
		assertEquals(systemID, parsedData.getSystemID());
		assertEquals(records, parsedData.getRecords());
	}

}
