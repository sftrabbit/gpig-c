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

	@Before
	public void before() {
		systemID = "TestSystem";
		sensors = new ArrayList<ClientSensor>();

		// Test Sensor
		Map<Parameter, String> params = new HashMap<>();
		params.put(Parameter.LOWER_BOUND, "10");
		params.put(Parameter.UPPER_BOUND, "70");
		sensors.add(new ClientSensor("TestSensor", params));

		system = new ClientSystem(systemID, sensors, new ArrayList<String>(), "",
				new HashMap<Parameter, String>());
	}

	@Test
	public void testNewSystem() {
		assertEquals(systemID, system.getID());
		assertTrue(sensors.size() == system.getSensors().size());
		assertEquals(sensors.get(0), system.getSensors().get(0));
	}

	@Test
	public void testEqualsPass() {
		ClientSystem equalSystem = new ClientSystem(systemID, sensors,
				new ArrayList<String>(),"", new HashMap<Parameter, String>());
		assertEquals(system, equalSystem);
	}

	@Test
	public void testEqualsFail() {
		ClientSystem equalSystem = new ClientSystem(systemID + "2", sensors,
				new ArrayList<String>(),"", new HashMap<Parameter,String>());
		assertNotSame(system, equalSystem);
	}

	@Test
	public void tesHasSensor() {
		assertTrue(system.hasSensorWithID("TestSensor"));
		assertFalse(system.hasSensorWithID("Not_a_TestSensor"));
	}

	@Test
	public void teGetSensor() {
		assertEquals(system.getSensorWithID("TestSensor"), sensors.get(0));
		assertNull(system.getSensorWithID("Not_a_TestSensor"));
	}
}
