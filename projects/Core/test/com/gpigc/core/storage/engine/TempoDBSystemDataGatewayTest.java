package com.gpigc.core.storage.engine;

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

public class TempoDBSystemDataGatewayTest {

	private static final String testSystemID = "TestSystemID";
	private static final int ONE_RECORD = 1;
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
	public void testReadMostRecent() throws FailedToWriteToDatastoreException, FailedToReadFromDatastoreException {
		gateway = new TempoDBSystemDataGateway(systems);
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("Test1", "Blue");
		payload.put("Test2", "Red");
		payload.put("Test3", "Green");
		gateway.write(new EmitterSystemState(testSystemID, new Date(0), payload));
		gateway.write(new EmitterSystemState(testSystemID, new Date(10), payload));
		gateway.write(new EmitterSystemState(testSystemID, new Date(20), payload));
		
		gateway.readBetween(testSystemID, "Test1", new Date(1), new Date(19));
	}
	
}
