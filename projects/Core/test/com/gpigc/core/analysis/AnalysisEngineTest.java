package com.gpigc.core.analysis;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.SensorParameter;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.GWTSystemDataGateway;
import com.gpigc.dataabstractionlayer.client.SensorState;

public class AnalysisEngineTest {

	private AnalysisEngine analysisEngine;
	private List<ClientSystem> registeredSystems;
	private GWTSystemDataGateway datastore;

	@Before
	public void before() throws URISyntaxException {
		//Set up dummy system
		registeredSystems = new ArrayList<>();

		//Test Sensor
		Map<SensorParameter, Object> params = new HashMap<>();
		params.put(SensorParameter.LOWER_BOUND, new Long(10));
		params.put(SensorParameter.UPPER_BOUND, new Long(70));
		ArrayList<ClientSensor> sensors = new ArrayList<>();
		sensors.add(new ClientSensor("TestSensor", params));

		registeredSystems.add(new ClientSystem("1", sensors, new ArrayList<String>()));

		//Init datastore and dummy engine
		datastore = new GWTSystemDataGateway(new URI(Core.APPENGINE_SERVLET_URI));
		analysisEngine = new AnalysisEngine(registeredSystems, datastore) {
			@Override
			public DataEvent analyse(ClientSystem system) {
				return null;
			}
		};
	}


	@Test
	public void testCorrectFields(){
		assertTrue(analysisEngine.getAssociatedSystems().size() == 1);
		assertEquals(registeredSystems.get(0),analysisEngine.getAssociatedSystems().get(0));
	}

	@Test
	public void testRegisteredSystems(){
		assertNotNull(analysisEngine.getRegisteredSystem(registeredSystems.get(0).getID()));
		assertNull(analysisEngine.getRegisteredSystem(""));
	}

	@Test
	public void testGetData() throws FailedToReadFromDatastoreException{
		final int numToGet = 1;
		pushData(registeredSystems.get(0), numToGet);

		List<SensorState> states = analysisEngine.getSensorReadings(registeredSystems.get(0).getID(),
				registeredSystems.get(0).getSensors().get(0).getID(), numToGet);
		System.out.println(states);
		assertTrue(states.size() == numToGet);
		assertEquals(registeredSystems.get(0).getSensors().get(0).getID(), states.get(0).getSensorID());
	}


	private void pushData(ClientSystem clientSystem, int numToPush) {
		for(int i = 0; i < numToPush; i++){
			Map<String, String> testData = new HashMap<>();
			testData.put(clientSystem.getSensors().get(0).getID(), i + "");
			try {
				datastore.write(
						new EmitterSystemState(clientSystem.getID(), 
								new Date(),testData
								));
			} catch (FailedToWriteToDatastoreException e) {
				e.printStackTrace();
			}
		}

	}

}
