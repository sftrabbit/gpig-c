package com.gpigc.core.notification.engine;

import java.util.ArrayList;
import org.apache.commons.mail.*;

import com.gpigc.core.notification.NotificationEngine;

/**
 * Email notification engine providing a way of sending an email to the specified recipient
 * 
 * @author GPIGC
 */
public class EmailNotification extends NotificationEngine {

	/**
	 * Initialises the email notification engine
	 */
	public EmailNotification() {
		 associatedSystems = new ArrayList<String>();
		 associatedSystems.add("1");
		 engineName = "EmailNotification1";
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.notification.NotificationEngine#send(java.lang.String, java.lang.String, java.lang.String)
	 */
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
