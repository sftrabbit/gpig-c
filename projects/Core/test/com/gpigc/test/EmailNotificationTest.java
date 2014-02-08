package com.gpigc.test;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.notification.engine.EmailNotification;

public class EmailNotificationTest {

	@Mock
	private NotificationEngine emailNotification;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		emailNotification = new EmailNotification();
	}

	@Test
	public void emailNotification() {
		sendAnEmail();
		checkDelivery();
	}

	private void sendAnEmail() {
		emailNotification.send("gpigc.alerts@gmail.com", "Test email", "Test message");
	}

	private void checkDelivery() {
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect("imap.gmail.com", "gpigc.alerts@gmail.com", "59QEF-wKsaZUw^d");
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			Message message = inbox.getMessage(inbox.getMessageCount());
			Address[] from = message.getFrom();
			for (Address address : from) {
				assertEquals(address.toString(), "gpigc.alerts@gmail.com");
			}
			assertEquals(message.getSubject(), "Test email");
			assertEquals(message.getContent().toString().substring(0, 12), "Test message");
			message.setFlag(Flags.Flag.DELETED, true);
			inbox.close(true);
			store.close();
		} catch (Exception mex) {
			mex.printStackTrace();
			fail("Exception raised");
		}
	}

}
