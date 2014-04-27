package com.gpigc.core.analysis.engine;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.analysis.ClientSystem;
import com.gpigc.core.analysis.engine.MeanEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class MeanEngineTest {

	private MeanEngine meanEngine;
	private List<ClientSystem> registeredSystems;
	private SystemDataGateway datastore;
	private Map<String,Map<String, Object>> parameters ;

	@Before
	public void before() throws URISyntaxException {
		//Set up dummy system
		registeredSystems = new ArrayList<>();
		
		//Set up the Params
		parameters = new HashMap<>();
		Map<String,Object> param1 = new HashMap<>();
		param1.put("Sens1", new Long(5));
		parameters.put("LOWER_BOUND", param1);
		Map<String,Object> param2 = new HashMap<>();
		param2.put("Sens1", new Long(10));
		parameters.put("UPPER_BOUND", param2);
		
		registeredSystems.add(new ClientSystem("TestSystem", new ArrayList<String>(), parameters));
		registeredSystems.get(0).getSensorIDs().add("Sens1");
		//Init datastore and engine
		datastore = new MockDB();
		meanEngine = new MeanEngine(registeredSystems, datastore);
		
	}

	@Test
	public void testGetMean() {
		List<SensorState> states = new ArrayList<>();
		states.add(new SensorState("Sens1", new Date(6), new Date(5), "10"));
		states.add(new SensorState("Sens1", new Date(6), new Date(5), "20"));
		assertEquals(MeanEngine.getMean(states), 15);
	}
	
	@Test
	public void testAnalyseEventLower() throws FailedToReadFromDatastoreException {
		//Mean should be 4;
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNotNull(event);
		assertTrue(event.getData().get("Message").contains("fallen"));
	}
	
	@Test
	public void testAnalyseEventUpper() throws FailedToReadFromDatastoreException {
		registeredSystems.get(0).getParameters().get("LOWER_BOUND").put("Sens1", new Long(1));
		registeredSystems.get(0).getParameters().get("UPPER_BOUND").put("Sens1", new Long(3));
		//Mean is 4
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNotNull(event);
		assertTrue(event.getData().get("Message").contains("exceeded"));
	}

}
