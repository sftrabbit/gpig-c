package com.gpigc.core.storage.engine;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.core.storage.engine.GWTSystemDataGateway;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;

public class GWTSystemDataGatewayTest {
	
	private static final String testSystemID = "TestSystemID";
	private List<ClientSystem> systems;
	
	
	@Before
	public void setUp(){
		systems = new ArrayList<>();
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("TestSensor",
				new HashMap<Parameter, String>()));
		systems.add(new ClientSystem(testSystemID, sensors, new ArrayList<String>(),"",
				new HashMap<Parameter, String>()));
	}
	
	@Test
	public void testReadAndWrite() throws FailedToReadFromDatastoreException, 
								URISyntaxException, 
								ParseException, 
								IOException, FailedToWriteToDatastoreException, SQLException {
		SystemDataGateway gateway = new GWTSystemDataGateway(systems);
		Map<String,String> payload = new HashMap<String,String>();
		payload.put("Test1", "Blue");
		payload.put("Test2", "Red");
		payload.put("Test3", "Green");
		gateway.write(new EmitterSystemState(testSystemID, new Date(6),payload));
		QueryResult result = gateway.readMostRecent(testSystemID,null, 3);
		assertEquals(testSystemID, result.getSystemID());
		Map<String,String> readPayload = new HashMap<String,String>();
		readPayload.put(
				result.getRecords().get(0).getSensorID(), 
				result.getRecords().get(0).getValue());
		readPayload.put(
				result.getRecords().get(1).getSensorID(), 
				result.getRecords().get(1).getValue());
		readPayload.put(
				result.getRecords().get(2).getSensorID(), 
				result.getRecords().get(2).getValue());
		assertEquals(payload, readPayload);
	}
	
	@Test
	public void testCreateJSONArray() throws URISyntaxException, IOException {
		List<EmitterSystemState> states = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Map<String,String> payload = new HashMap<String,String>();
			payload.put("Test1", "Blue");
			payload.put("Test2", "Red");
			payload.put("Test3", "Green");
			states.add(new EmitterSystemState(testSystemID+i, new Date(6),payload));
		}
		GWTSystemDataGateway gateway = new GWTSystemDataGateway(systems);
		System.out.println("JSON Array = " + gateway.createJSONArray(states));
	}
}