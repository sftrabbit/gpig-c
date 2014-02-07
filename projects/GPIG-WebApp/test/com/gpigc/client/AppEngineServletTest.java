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
		assertEquals(3, states.size());
		assertEquals("TestSystemID0", states.get(0).getSystemID());
		assertEquals("Blue", states.get(2).getSensorReadings().get("Test1"));
	}

}
