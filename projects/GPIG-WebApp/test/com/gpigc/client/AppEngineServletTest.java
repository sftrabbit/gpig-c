/**
 * 
 */
package com.gpigc.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.server.AppEngineServlet;

/**
 * @author GPIG-C
 */
public class AppEngineServletTest {

	@Test
	public void testParseJSON() throws JsonParseException, IOException {
		String json = "{\"States\":[{\"SystemID\":\"TestSystemID0\",\"CreationTimestamp\":6,\"Sensors\":[{\"SensorID\":\"Test1\",\"Value\":\"Blue\"},{\"SensorID\":\"Test2\",\"Value\":\"Red\"},{\"SensorID\":\"Test3\",\"Value\":\"Green\"}]},{\"SystemID\":\"TestSystemID1\",\"CreationTimestamp\":6,\"Sensors\":[{\"SensorID\":\"Test1\",\"Value\":\"Blue\"},{\"SensorID\":\"Test2\",\"Value\":\"Red\"},{\"SensorID\":\"Test3\",\"Value\":\"Green\"}]},{\"SystemID\":\"TestSystemID2\",\"CreationTimestamp\":6,\"Sensors\":[{\"SensorID\":\"Test1\",\"Value\":\"Blue\"},{\"SensorID\":\"Test2\",\"Value\":\"Red\"},{\"SensorID\":\"Test3\",\"Value\":\"Green\"}]},{\"SystemID\":\"TestSystemID3\",\"CreationTimestamp\":6,\"Sensors\":[{\"SensorID\":\"Test1\",\"Value\":\"Blue\"},{\"SensorID\":\"Test2\",\"Value\":\"Red\"},{\"SensorID\":\"Test3\",\"Value\":\"Green\"}]}]}";
		System.out.println("JSON string = " + json);
		AppEngineServlet servlet = new AppEngineServlet();
		StringReader reader = new StringReader(json);
		List<EmitterSystemState> states = 
				servlet.parseJSON(reader);
		assertEquals(4, states.size());
		assertEquals("TestSystemID0", states.get(0).getSystemID());
		assertEquals("Blue", states.get(2).getSensorReadings().get("Test1"));
		assertEquals("TestSystemID3", states.get(3).getSystemID());
	}
	
	@Test
	public void testParseJSONTwo() throws JsonParseException, IOException {
		String json = "{\"States\":[{\"SystemID\":\"1\",\"CreationTimestamp\":2462230871816,\"Sensors\":[{\"SensorID\":\"Mem\",\"Value\":\"37935888\"},{\"SensorID\":\"CPU\",\"Value\":\"0.5\"}]},{\"SystemID\":\"1\",\"CreationTimestamp\":2463237414448,\"Sensors\":[{\"SensorID\":\"Mem\",\"Value\":\"37935888\"},{\"SensorID\":\"CPU\",\"Value\":\"0.0\"}]}]}";
		System.out.println("JSON string = " + json);
		AppEngineServlet servlet = new AppEngineServlet();
		StringReader reader = new StringReader(json);
		List<EmitterSystemState> states = 
				servlet.parseJSON(reader);
		assertEquals("1", states.get(0).getSystemID());
	}
}