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

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.analysis.engine.BoundedAnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class MeanEngineTest {

	private BoundedAnalysisEngine meanEngine;
	private List<ClientSystem> registeredSystems;
	private SystemDataGateway datastore;

	@Before
	public void before() throws URISyntaxException {
		// Set up dummy system
		registeredSystems = new ArrayList<>();

		// Test Sensor
		Map<Parameter, String> params = new HashMap<>();
		params.put(Parameter.LOWER_BOUND, "1");
		params.put(Parameter.UPPER_BOUND, "10");
		params.put(Parameter.NUM_RECORDS, "10");
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("Sens1", params));

		// Init datastore and engine
		datastore = new MockDB("4");
		meanEngine = new BoundedAnalysisEngine(registeredSystems, datastore);
		ArrayList<String> registeredEngines = new ArrayList<>();
		registeredEngines.add(meanEngine.name);
		registeredSystems.add(new ClientSystem("TestSystem", sensors,
				registeredEngines, new HashMap<Parameter, String>()));
	}

	@Test
	public void testGetMean() {
		List<SensorState> states = new ArrayList<>();
		states.add(new SensorState("Sens1", new Date(6), new Date(5), "10"));
		states.add(new SensorState("Sens1", new Date(6), new Date(5), "20"));
		assertEquals(BoundedAnalysisEngine.getMean(states), 15);
	}

	@Test
	public void testAnalyseEventLower() {
		registeredSystems.get(0).getSensors().get(0).getParameters()
				.put(Parameter.LOWER_BOUND, "5");
		// Mean should be 4;
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNotNull(event);
		assertTrue(event.getData().get("Message").contains("fallen"));
	}

	@Test
	public void testAnalyseEventUpper() {
		registeredSystems.get(0).getSensors().get(0).getParameters()
				.put(Parameter.UPPER_BOUND, "3");
		// Mean is 4
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNotNull(event);
		assertTrue(event.getData().get("Message").contains("exceeded"));
	}

	@Test
	public void testAnalyseNoEvent() {
		// Mean should be 4;
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNull(event);
	}

	@Test
	public void testWrongParameters() {
		// Mean should be 4;
		registeredSystems.get(0).getSensors().get(0).getParameters().clear();
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNull(event);
	}

}
