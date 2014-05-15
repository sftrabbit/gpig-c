package com.gpigc.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

public class ConfigParserTest {

	private static final String testValidJSON = "{\"Systems\": ["
			+ "{\"SystemID\": \"2\"," + "\"Sensors\": [ "
			+ " {\"SensorID\": \"EQ\", " + "\"Params\": {"
			+ "  \"LOWER_BOUND\": \"10\", " + " \"UPPER_BOUND\": \"80\"  "
			+ " }}]," + " \"Engines\": [ " + " \"EarthquakeAnalysisEngine\", "
			+ " \"TwitterNotificationEngine\" " + "] }]}";
	private ConfigParser configParser;

	@Test
	public void testParseValidString() {
		configParser = new ConfigParser();
		ArrayList<ClientSystem> systems = configParser.parse(testValidJSON);
		checkCorrect(systems);
	}

	@Test
	public void testParseFile() throws IOException {
		configParser = new ConfigParser();
		ArrayList<ClientSystem> systems = configParser.parse(new File(
				"config/Test.config"));
		checkCorrect(systems);
	}

	private void checkCorrect(ArrayList<ClientSystem> systems) {
		assertNotNull(systems);
		assertTrue(systems.size() == 1);
		assertEquals("2", systems.get(0).getID());
		assertNotNull(systems.get(0).getSensors());
		assertNotNull(systems.get(0).getSensors().get(0));
		assertNotNull(systems.get(0).getSensors().get(0).getParameters());
		assertEquals("80", systems.get(0).getSensors().get(0).getParameters()
				.get(Parameter.UPPER_BOUND));
		assertEquals("10", systems.get(0).getSensors().get(0).getParameters()
				.get(Parameter.LOWER_BOUND));
		assertNotNull(systems.get(0).getRegisteredEngineNames());
	}

}
