package com.gpigc.core.notification.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.event.DataEvent;

public class TwitterNotificationEngineTest {


	private ArrayList<ClientSystem> testSystems;
	private TwitterNotificationEngine engine;

	@Before
	public void before() {
		testSystems = new ArrayList<>();
		testSystems.add(new ClientSystem("Test", new ArrayList<ClientSensor>(), new ArrayList<String>()));
		engine = new TwitterNotificationEngine(testSystems,5000);
	}

	/**
	 * This is done in one test to prevent spamming
	 */
	@Test
	public void testSendAndRepeat() {
		Map<String,String> testData = new HashMap<>();
		testData.put("Message","Test Message: " + System.currentTimeMillis());
		DataEvent testEvent = new DataEvent(testData, testSystems.get(0));
		assertTrue(engine.send(testEvent));
		assertFalse(engine.send(testEvent)); //cooldown
	}

}
