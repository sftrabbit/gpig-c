package com.gpigc.core.notification;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.SensorParameter;

public class NotificationGeneratorTest {

	private ArrayList<ClientSystem> systems;

	@Before
	public void before() throws ReflectiveOperationException {
		systems = new ArrayList<>();
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("TestSensor", new HashMap<SensorParameter, String>()));
		systems.add(new ClientSystem("Test", sensors, new ArrayList<String>()));
	}

	@Test
	public void testMakeEngines() throws ReflectiveOperationException{
		NotificationGenerator generator = new NotificationGenerator(systems); //no exception should be thrown
		assertNotNull(generator.getNotificationEngines());
		assertTrue(generator.getNotificationEngines().size() != 0);
	}

}
