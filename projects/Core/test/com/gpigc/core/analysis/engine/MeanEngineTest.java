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
import com.gpigc.core.SensorParameter;
import com.gpigc.core.analysis.engine.MeanAnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class MeanEngineTest {

	private MeanAnalysisEngine meanEngine;
	private List<ClientSystem> registeredSystems;
	private SystemDataGateway datastore;

	@Before
	public void before() throws URISyntaxException {
		//Set up dummy system
		registeredSystems = new ArrayList<>();

		//Test Sensor
		Map<SensorParameter, String> params = new HashMap<>();
		params.put(SensorParameter.LOWER_BOUND, "1");
		params.put(SensorParameter.UPPER_BOUND, "10");
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("Sens1", params));

		//Init datastore and engine
		datastore = new MockDB("4");
		meanEngine = new MeanAnalysisEngine(registeredSystems, datastore);
		ArrayList<String> registeredEngines = new ArrayList<>();
		registeredEngines.add(meanEngine.name);
		registeredSystems.add(new ClientSystem("TestSystem", sensors, registeredEngines));
	}

	@Test
	public void testGetMean() {
		List<SensorState> states = new ArrayList<>();
		states.add(new SensorState("Sens1", new Date(6), new Date(5), "10"));
		states.add(new SensorState("Sens1", new Date(6), new Date(5), "20"));
		assertEquals(MeanAnalysisEngine.getMean(states), 15);
	}

	@Test
	public void testAnalyseEventLower() {
		registeredSystems.get(0).getSensors().get(0).getParameters().put(SensorParameter.LOWER_BOUND, "5");
		//Mean should be 4;
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNotNull(event);
		assertTrue(event.getData().get("Message").contains("fallen"));
	}

	@Test
	public void testAnalyseEventUpper()  {
		registeredSystems.get(0).getSensors().get(0).getParameters().put(SensorParameter.UPPER_BOUND, "3");
		//Mean is 4
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNotNull(event);
		assertTrue(event.getData().get("Message").contains("exceeded"));
	}
	
	@Test
	public void testAnalyseNoEvent()  {
		//Mean should be 4;
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNull(event);
	}

	@Test
	public void testWrongParameters() {
		//Mean should be 4;
		registeredSystems.get(0).getSensors().get(0).getParameters().clear();
		DataEvent event = meanEngine.analyse(registeredSystems.get(0));
		assertNull(event);
	}

}
