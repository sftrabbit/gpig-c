package com.gpigc.core.storage.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;

public class TempoDBSystemDataGatewayTest {

	private static final String testSystemID = "TestSystemID";
	private List<ClientSystem> systems;
	private TempoDBSystemDataGateway gateway;

	@Before
	public void setUp() throws ClassNotFoundException, URISyntaxException,
			SQLException {
		systems = new ArrayList<>();
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("TestSensor",
				new HashMap<Parameter, String>()));
		systems.add(new ClientSystem(testSystemID, sensors,
				new ArrayList<String>(), "", new HashMap<Parameter, String>()));
	}
	
	@Test
	public void testReadBetween() throws FailedToWriteToDatastoreException, FailedToReadFromDatastoreException, InterruptedException {
		gateway = new TempoDBSystemDataGateway(systems);
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("Test1", "15");
		payload.put("Test2", "24");
		payload.put("Test3", "83"); 
		gateway.write(new EmitterSystemState("TEST_SYSTEM_ONE", new DateTime().minusDays(1).toDate(), payload));
		gateway.write(new EmitterSystemState("TEST_SYSTEM_TWO", new DateTime().plusDays(1).toDate(), payload));
		
		gateway.write(new EmitterSystemState(testSystemID, new DateTime().toDate(), payload));
		payload.put("Test1", "12135");
		gateway.write(new EmitterSystemState(testSystemID, new DateTime().plusMinutes(12).toDate(), payload));
		
		//Wait for persistence.
		Thread.sleep(3000);
		
		QueryResult result = gateway.readBetween(testSystemID, "Test1",  new DateTime().minusHours(1).toDate(),  new DateTime().plusHours(1).toDate());
		assertEquals(2, result.getRecords().size());
		assertTrue(testSystemID.equals(result.getSystemID()));
		assertTrue("15".equals(result.getRecords().get(0).getValue()) || "12135".equals(result.getRecords().get(0).getValue()));
		assertTrue("15".equals(result.getRecords().get(1).getValue()) || "12135".equals(result.getRecords().get(1).getValue()));
	}
}