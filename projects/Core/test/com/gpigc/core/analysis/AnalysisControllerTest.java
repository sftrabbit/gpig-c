package com.gpigc.core.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.analysis.AnalysisController;

public class AnalysisControllerTest {

	private Set<String> systemIDs;

	@Before
	public void before() throws ReflectiveOperationException {
		systemIDs = new HashSet<>();
		systemIDs.add("Test");
	}

	@Test
	public void testMakeEngines() throws ReflectiveOperationException, IOException, InterruptedException {
		AnalysisController analysisController = new AnalysisController(new ArrayList<ClientSystem>(), new Core("config/RegisteredSystems.config")); // no
																																					// exception
																																					// thrown
		assertNotNull(analysisController.getAnalysisEngines());
		assertTrue(analysisController.getAnalysisEngines().size() != 0);
	}

	@Test
	public void testSystemUpdate() throws ReflectiveOperationException, IOException, InterruptedException {
		AnalysisController analysisController = new AnalysisController(new ArrayList<ClientSystem>(), new Core("config/RegisteredSystems.config")); // no
																																					// exception
																																					// thrown
		analysisController.analyse(systemIDs);
	}

}
