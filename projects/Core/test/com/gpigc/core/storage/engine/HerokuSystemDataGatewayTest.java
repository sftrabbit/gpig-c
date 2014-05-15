package com.gpigc.core.storage.engine;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;

public class HerokuSystemDataGatewayTest {

	private static final String testSystemID = "TestSystemID";
	private static final int ONE_RECORD = 1;
	private List<ClientSystem> systems;
	private HerokuSystemDataGateway gateway;

	@Before
	public void setUp() throws ClassNotFoundException, URISyntaxException,
			SQLException {
		systems = new ArrayList<>();
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("TestSensor",
				new HashMap<Parameter, String>()));
		systems.add(new ClientSystem(testSystemID, sensors,
				new ArrayList<String>(), "", new HashMap<Parameter, String>()));
		gateway = new HerokuSystemDataGateway(systems);
		gateway.initialiseTables();
	}

	@Test
	public void testReadBetween() throws ClassNotFoundException,
			URISyntaxException, SQLException,
			FailedToWriteToDatastoreException,
			FailedToReadFromDatastoreException {
		Map<String, String> payloadOne = new HashMap<String, String>();
		payloadOne.put("Test1", "Red");

		Map<String, String> payloadTwo = new HashMap<String, String>();
		payloadTwo.put("Test1", "Blue");

		gateway.write(new EmitterSystemState(testSystemID, new Date(),
				payloadTwo));
		gateway.write(new EmitterSystemState(testSystemID, new Date(0xFFFFF),
				payloadOne));
		gateway.write(new EmitterSystemState(testSystemID,
				new Date(0xFFFFFFFF), payloadTwo));
		QueryResult result = gateway.readBetween(testSystemID, "Test1",
				new Date(0xFFFF), new Date(0xFFFFFF));
		assertEquals(1, result.getRecords().size());
		assertTrue("Red".equals(result.getRecords().get(0).getValue()));
		assertTrue("Test1".equals(result.getRecords().get(0).getSensorID()));
	}

	@Test
	public void testWriteAndRead() throws URISyntaxException, SQLException,
			ClassNotFoundException, FailedToWriteToDatastoreException,
			FailedToReadFromDatastoreException {
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("Test1", "Blue");
		payload.put("Test2", "Red");
		payload.put("Test3", "Green");
		gateway.write(new EmitterSystemState(testSystemID, new Date(0), payload));
		QueryResult result = gateway.readMostRecent(testSystemID, "Test1", 3);
		assertEquals(testSystemID, result.getSystemID());
		assertEquals(ONE_RECORD, result.getRecords().size());
		assertEquals("Test1", result.getRecords().get(0).getSensorID());
		assertEquals("Blue", result.getRecords().get(0).getValue());
	}

	@Test
	public void testBulkWrite() throws FailedToWriteToDatastoreException,
			SQLException, FailedToReadFromDatastoreException {
		List<EmitterSystemState> emmitterSystemStates = new ArrayList<EmitterSystemState>();
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("Test1", "Blue");
		payload.put("Test2", "Green");
		emmitterSystemStates.add(new EmitterSystemState(testSystemID, new Date(
				0), payload));
		emmitterSystemStates.add(new EmitterSystemState(testSystemID,
				new Date(), payload));
		gateway.write(emmitterSystemStates);
		QueryResult result = gateway.readMostRecent(testSystemID, "Test1", 5);
		assertEquals(2, result.getRecords().size());
	}
}