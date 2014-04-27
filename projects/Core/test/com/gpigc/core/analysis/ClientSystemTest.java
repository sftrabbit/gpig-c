package com.gpigc.core.analysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.analysis.ClientSystem;

public class ClientSystemTest {

	private ClientSystem system;
	private List<String> sensorIDs;
	private String systemID;
	private Map<String,Map<String, Object>> parameters;
	@Before
	public void before() {
		systemID = "Test";
		sensorIDs = new ArrayList<String>();
		sensorIDs.add("1");
		parameters = new HashMap<>();
		Map<String,Object> param1 = new HashMap<>();
		param1.put("1", new Integer(10));
		parameters.put("PARAM1", param1);
		Map<String,Object> param2 = new HashMap<>();
		param2.put("1", new Integer(50));
		parameters.put("PARAM2", param2);
		system = new ClientSystem(systemID,sensorIDs, parameters);
	}
	
	@Test
	public void testNewSystem() {
		assertEquals(systemID, system.getSystemID());
		assertTrue(sensorIDs.size() == system.getSensorIDs().size());
		assertEquals(sensorIDs.get(0), system.getSensorIDs().get(0));
	}
	
	@Test
	public void testEqualsPass() {
		ClientSystem equalSystem = new ClientSystem(systemID, sensorIDs,parameters);
		assertEquals(system, equalSystem);
	}
	
	@Test
	public void testEqualsFail() {
		ClientSystem equalSystem = new ClientSystem(systemID+"2", sensorIDs,parameters);
		assertNotEquals(system, equalSystem);
	}
}
