package com.gpigc.core.analysis.engine;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.SensorParameter;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class EarthquakeAnalysisEngineTest {

	private EarthquakeAnalysisEngine earthEngine;
	private List<ClientSystem> registeredSystems;
	private SystemDataGateway datastore;
	private ArrayList<ClientSensor> sensors;

	@Before
	public void before() throws URISyntaxException {
		//Set up dummy system
		registeredSystems = new ArrayList<>();
		
		Map<SensorParameter,String> data = new HashMap<>();
		data.put(SensorParameter.LOWER_BOUND, "1.0");
		sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor(EarthquakeAnalysisEngine.EARTHQUAKE_SENSOR_ID,
				data));

	}

	@Test
	public void testAnalyseNoEvent() {
		setUp("0.8,1.4,-188.0");
		DataEvent event = earthEngine.analyse(registeredSystems.get(0));
		assertNull(event);
	}

	@Test
	public void testAnalyseEvent() {
		setUp("1.0,1.4,-188.0");
		DataEvent event = earthEngine.analyse(registeredSystems.get(0));
		assertNotNull(event);
	}
	
	@Test
	public void testNoEqSensor() {
		setUp("1.0,1.4,-188.0");
		sensors.clear();
		DataEvent event = earthEngine.analyse(registeredSystems.get(0));
		assertNull(event);
	}
	
	private void setUp(String string) {
		datastore = new MockDB(string);
		earthEngine = new EarthquakeAnalysisEngine(registeredSystems, datastore);

		ArrayList<String> registeredEngines = new ArrayList<>();
		registeredEngines.add(earthEngine.name);
		registeredSystems.add(new ClientSystem("TestEarthSystem", 
				sensors, registeredEngines));
	}
	
}
