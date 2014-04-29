package com.gpigc.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ClientSensorTest {

	private Map<SensorParameter, Object> testParameters;
	private ClientSensor testSensor;
	

	@Before
	public void before() {
		testParameters = new HashMap<SensorParameter,Object>();
		testParameters.put(SensorParameter.LOWER_BOUND, new Long(10));
		testParameters.put(SensorParameter.UPPER_BOUND, new Long(100));
		testSensor = new ClientSensor("TestID", testParameters);
	}
	
	@Test
	public void testGetID() {
		assertEquals("TestID", testSensor.getID());
	}

	@Test
	public void testGetParams() {
		assertEquals(testParameters, testSensor.getParameters());
	}
}
