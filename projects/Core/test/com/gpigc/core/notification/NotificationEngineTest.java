package com.gpigc.core.notification;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.event.DataEvent;

public class NotificationEngineTest {

	private ArrayList<ClientSystem> testSystems;
	private NotificationEngine engine;

	@Before
	public void before() {

		testSystems = new ArrayList<ClientSystem>();
		testSystems.add(new ClientSystem("TestSystem",
				new ArrayList<ClientSensor>(), new ArrayList<String>(),"",
				new HashMap<Parameter, String>()));

		engine = new NotificationEngine(testSystems, 3) {
			@Override
			public boolean send(DataEvent event) {
				return false;
			}
		};
	}

	@Test
	public void testGetSystems() {
		assertTrue(engine.getAssociatedSystems().size() == 1);
		assertEquals(testSystems.get(0), engine.getAssociatedSystems().get(0));
	}

	@Test
	public void testCoolDown() throws InterruptedException {
		engine.setRecentlySent();
		assertTrue(engine.getRecentlySent());
		Thread.sleep(3050);
		assertFalse(engine.getRecentlySent());
	}

}
