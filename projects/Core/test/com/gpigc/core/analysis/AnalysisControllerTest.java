package com.gpigc.core.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Config;
import com.gpigc.core.Config.ConfigException;
import com.gpigc.core.Core;
import com.gpigc.core.Main;
import com.gpigc.core.analysis.AnalysisController;

public class AnalysisControllerTest {

	private String systemID = "Test";

	@Test
	public void testMakeEngines() throws ReflectiveOperationException,
			IOException, InterruptedException, ConfigException {
		Config config = new Config(Main.CONFIG_NAME);
		AnalysisController analysisController = new AnalysisController(
				new ArrayList<ClientSystem>(), new Core(
						"config/RegisteredSystems.config", config)); // no
																// exception
																// thrown
		assertNotNull(analysisController.getAnalysisEngines());
		assertTrue(analysisController.getAnalysisEngines().size() != 0);
	}

	@Test
	public void testSystemUpdate() throws ReflectiveOperationException,
			IOException, InterruptedException, ConfigException {
		Config config = new Config(Main.CONFIG_NAME);
		AnalysisController analysisController = new AnalysisController(
				new ArrayList<ClientSystem>(), new Core(
						"config/RegisteredSystems.config", config)); // no
																// exception
																// thrown
		analysisController.analyse(systemID);
	}

}
