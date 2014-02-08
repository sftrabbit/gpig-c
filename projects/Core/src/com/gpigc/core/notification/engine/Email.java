package com.gpigc.core.notification.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.*;

import com.gpigc.core.notification.NotificationEngine;

public class Email extends NotificationEngine {

	public Email() {
		 associatedSystems = new ArrayList<String>();
		 associatedSystems.add("1");
		 engineName = "EmailNotification1";
	}

	public void send(String recepient, String subject, String message) {
		SimpleEmail email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("gpigc.alerts@gmail.com", "59QEF-wKsaZUw^d"));
		email.setSSLOnConnect(true);
		try {
			email.setFrom("gpigc.alerts@gmail.com");
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(recepient);
			email.send();
			setRecentlySent();
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
