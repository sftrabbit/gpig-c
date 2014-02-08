package com.gpigc.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.google.gwt.dev.util.collect.HashMap;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;

public class EmitterSystemStateTest {

	@Test
	public void testParseJSON() throws IOException, ParseException {
		Map<String, String> payload = new HashMap<>();
		payload.put("PayloadKey", "PayloadValue");
		EmitterSystemState state = new EmitterSystemState(
				"ID", 
				new Date(100), 
				payload);
		String jsonString = state.toJSON();
		EmitterSystemState readState = 
				EmitterSystemState.parseJSON(new StringReader(jsonString));
		assertEquals(state.getSystemID(), readState.getSystemID());
		assertEquals(state.getTimeStamp(), readState.getTimeStamp());
		assertEquals(state.getSensorReadings(), readState.getSensorReadings());
	}

}
