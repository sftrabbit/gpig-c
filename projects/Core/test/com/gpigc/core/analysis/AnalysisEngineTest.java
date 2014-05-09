package com.gpigc.core.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.Parameter;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;

public class AnalysisEngineTest {

	private AnalysisEngine analysisEngine;
	private List<ClientSystem> registeredSystems;

	@Before
	public void before() throws URISyntaxException, IOException, ReflectiveOperationException {
		// Set up dummy system
		registeredSystems = new ArrayList<>();

		// Test Sensor
		Map<Parameter, String> params = new HashMap<>();
		params.put(Parameter.LOWER_BOUND, "10");
		params.put(Parameter.UPPER_BOUND, "70");
		ArrayList<ClientSensor> sensors = new ArrayList<>();
		sensors.add(new ClientSensor("TestSensor", params));

		registeredSystems.add(new ClientSystem("TestSystem", sensors,
				new ArrayList<String>(), "", new HashMap<Parameter, String>()));

		analysisEngine = new AnalysisEngine(registeredSystems, new Core("config/RegisteredSystems.config")) {
			@Override
			public DataEvent analyse(ClientSystem system) {
				return null;
			}
		};
	}

	@Test
	public void testCorrectFields() {
		assertTrue(analysisEngine.getAssociatedSystems().size() == 1);
		assertEquals(registeredSystems.get(0), analysisEngine
				.getAssociatedSystems().get(0));
	}

	@Test
	public void testRegisteredSystems() {
		assertNotNull(analysisEngine.getRegisteredSystem(registeredSystems.get(
				0).getID()));
		assertNull(analysisEngine.getRegisteredSystem(""));
	}

}
