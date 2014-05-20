package com.gpigc.core.storage.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;

public class Neo4jSystemDataGatewayTest {

	private static final String testSystemID = "TestSystemID";
	private static List<ClientSystem> systems;
	private static Neo4jSystemDataGateway gateway;

	@BeforeClass
	public static void setUp() throws ClassNotFoundException, URISyntaxException, SQLException {
		systems = new ArrayList<>();
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("TestSensor", new HashMap<Parameter, String>()));
		systems.add(new ClientSystem(testSystemID, sensors, new ArrayList<String>(), "", new HashMap<Parameter, String>()));
		gateway = new Neo4jSystemDataGateway(systems);
	}
	
	@Test
	public void testReadMostRecent() throws FailedToWriteToDatastoreException, FailedToReadFromDatastoreException {
		
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("Test1", "Red");
		payload.put("Test2", "Red");
		payload.put("Test3", "Red");
		EmitterSystemState data = new EmitterSystemState(testSystemID, new Date(), payload);
		
		gateway.write(data);
		QueryResult result = gateway.readMostRecent(testSystemID, "Test1", 1);

		
		assertEquals(1, result.getRecords().size());
		assertTrue("Red".equals(result.getRecords().get(0).getValue()));
	}

	@Test
	public void testReadBetween() throws FailedToWriteToDatastoreException, FailedToReadFromDatastoreException {
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("Test1", "Red");
		EmitterSystemState data = new EmitterSystemState(testSystemID, new Date(0), payload);
		gateway.write(data);
		data = new EmitterSystemState(testSystemID, new Date(400), payload);
		gateway.write(data);
		data = new EmitterSystemState(testSystemID, new Date(800), payload);
		gateway.write(data);	
		
		QueryResult result = gateway.readBetween(testSystemID, "Test1", new Date(10), new Date(799));
		assertEquals(1, result.getRecords().size());
		assertEquals(new Date(400), result.getRecords().get(0).getDatabaseTimestamp());
	}

}
