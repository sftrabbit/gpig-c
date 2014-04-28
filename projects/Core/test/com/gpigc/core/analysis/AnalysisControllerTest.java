package com.gpigc.core.analysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.SensorParameter;
import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.analysis.engine.MockDB;

public class AnalysisControllerTest {


	private List<ClientSystem> systems;


	@Before
	public void before() throws ReflectiveOperationException {
		systems = new ArrayList<>();
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		
		//TEST Sensor
		Map<SensorParameter, Object> params = new HashMap<>();
		params.put(SensorParameter.LOWER_BOUND, new Long(10));
		params.put(SensorParameter.UPPER_BOUND, new Long(70));
		sensors.add(new ClientSensor("TestSensor", params));
		
		systems.add(new ClientSystem("Test", sensors));
	}


	@Test
	public void testMakeEngines() throws ReflectiveOperationException{
		AnalysisController analysisController = new AnalysisController(
				new MockDB(), null,systems); //no exception should be thrown
		assertNotNull(analysisController.getAnalysisEngines());
		assertTrue(analysisController.getAnalysisEngines().size() != 0);
	}

	@Test
	public void testSystemUpdate() throws ReflectiveOperationException{
		AnalysisController analysisController = new AnalysisController(
				new MockDB(), null,systems); //no exception should be thrown
		analysisController.systemUpdate("Test");
	}

}
