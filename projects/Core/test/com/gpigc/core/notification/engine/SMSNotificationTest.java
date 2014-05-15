package com.gpigc.core.notification.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import org.junit.Before;
import org.junit.Test;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.notification.engine.EmailNotificationEngine;

public class SMSNotificationTest {

	private NotificationEngine smsNotification;

	@Before
	public void setUp() throws Exception {
		smsNotification = new SMSNotificationEngine(
				new ArrayList<ClientSystem>(), 5000);
	}

	@Test
	public void smsNotificationTest() {
		assertTrue(sendAnSMS());
		checkDelivery();
		assertFalse(sendAnSMS()); // cooldown

	}

	private boolean sendAnSMS() {
		Map<Parameter, String> data = new HashMap<Parameter, String>();
		data.put(Parameter.MESSAGE, "Test message");
		data.put(Parameter.SUBJECT, "Test subject");
		data.put(Parameter.RECIPIENT, "07number");
		DataEvent event = new DataEvent(data, new ClientSystem("TestSystem",
				new ArrayList<ClientSensor>(), new ArrayList<String>(), "",
				new HashMap<Parameter, String>()));
		return smsNotification.send(event);
	}

	private void checkDelivery() {
		//
	}

}
