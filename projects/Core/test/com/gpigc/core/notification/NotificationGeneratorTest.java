package com.gpigc.core.notification;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;

public class NotificationGeneratorTest {

	private ArrayList<ClientSystem> systems;

	@Before
	public void before() throws ReflectiveOperationException {
		systems = new ArrayList<>();
		ArrayList<ClientSensor> sensors = new ArrayList<ClientSensor>();
		sensors.add(new ClientSensor("TestSensor",
				new HashMap<Parameter, String>()));
		systems.add(new ClientSystem("Test", sensors, new ArrayList<String>(),"",
				new HashMap<Parameter, String>()));
	}

	@Test //No exception
	public void testMakeEngines() throws ReflectiveOperationException {
		NotificationController generator = new NotificationController(systems);
		assertNotNull(generator.getNotificationEngines());
		assertTrue(generator.getNotificationEngines().size() != 0);
	}

}
