package com.gpigc.core.analysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.analysis.ClientSystem;
import com.gpigc.core.analysis.engine.MockDB;
import com.gpigc.core.notification.NotificationGenerator;

public class AnalysisControllerTest {


	private List<ClientSystem> systems;
	private Map<String, Map<String, Object>> parameters;


	@Before
	public void before() throws ReflectiveOperationException {
		systems = new ArrayList<>();

		//Set up the Params
		parameters = new HashMap<>();
		Map<String,Object> param1 = new HashMap<>();
		param1.put("Sens1", new Long(10));
		parameters.put("LOWER_BOUND", param1);
		Map<String,Object> param2 = new HashMap<>();
		param2.put("Sens1", new Long(50));
		parameters.put("UPPER_BOUND", param2);
		
		systems.add(new ClientSystem("Test", new ArrayList<String>(),
				parameters));
		systems.get(0).getSensorIDs().add("Sens1");
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
