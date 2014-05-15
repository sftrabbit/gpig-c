package com.gpigc.core.event;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.event.DataEvent;

public class DataEventTest {

	private Map<String, String> testData;
	private ClientSystem testSystem;

	@Before
	public void before() {
		testSystem = new ClientSystem("TestSystem",
				new ArrayList<ClientSensor>(), new ArrayList<String>(), "",
				new HashMap<Parameter, String>());
		testData = new HashMap<String, String>();
		testData.put("Test", "Value");
	}

	@Test
	public void testGetData() {
		DataEvent event = new DataEvent(testData, testSystem);
		assertEquals(testData, event.getData());
	}

	@Test
	public void testGetSystem() {
		DataEvent event = new DataEvent(testData, testSystem);
		assertEquals(testSystem, event.getSystem());
	}

}
