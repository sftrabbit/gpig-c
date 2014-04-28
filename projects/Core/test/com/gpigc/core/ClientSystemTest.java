package com.gpigc.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSystem;

public class ClientSystemTest {

	private ClientSystem system;
	private List<ClientSensor> sensors;
	private String systemID;
	private Map<String,Map<String, Object>> parameters;
	
	@Before
	public void before() {
		systemID = "TestSystem";
		sensors = new ArrayList<ClientSensor>();
		
		//Test Sensor
		Map<SensorParameter, Object> CPUParams = new HashMap<>();
		CPUParams.put(SensorParameter.LOWER_BOUND, new Integer(10));
		CPUParams.put(SensorParameter.UPPER_BOUND, new Integer(70));
		sensors.add(new ClientSensor("TestSensor", CPUParams));
		
		system = new ClientSystem(systemID,sensors);
	}
	
	@Test
	public void testNewSystem() {
		assertEquals(systemID, system.getID());
		assertTrue(sensors.size() == system.getSensors().size());
		assertEquals(sensors.get(0), system.getSensors().get(0));
	}
	
	@Test
	public void testEqualsPass() {
		ClientSystem equalSystem = new ClientSystem(systemID, sensors);
		assertEquals(system, equalSystem);
	}
	
	@Test
	public void testEqualsFail() {
		ClientSystem equalSystem = new ClientSystem(systemID+"2", sensors);
		assertNotEquals(system, equalSystem);
	}
}
