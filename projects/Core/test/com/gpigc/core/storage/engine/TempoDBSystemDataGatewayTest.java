package com.gpigc.core.storage.engine;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;

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
		gateway = new TempoDBSystemDataGateway(systems);
	}
	
	@Test
	public void testReadMostRecent() {
		
	}
	
}
