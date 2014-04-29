package com.gpigc.core.notification.engine;

import java.util.List;

import org.apache.commons.mail.*;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationEngine;

/**
 * Email notification engine providing a way of sending an email to the specified recipient
 * 
 * @author GPIGC
 */
public class EmailNotificationEngine extends NotificationEngine {


	public EmailNotificationEngine(List<ClientSystem> registeredSystems, final int COOL_DOWN_SECS) {
		super(registeredSystems, COOL_DOWN_SECS);
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.notification.NotificationEngine#send(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean send(DataEvent event) {
		if(!getRecentlySent()){
			SimpleEmail email = new SimpleEmail();
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(465);
			email.setAuthenticator(new DefaultAuthenticator("gpigc.alerts@gmail.com", "59QEF-wKsaZUw^d"));
			email.setSSLOnConnect(true);
			try {
				email.setFrom("gpigc.alerts@gmail.com");
				String recepient = event.getData().get("Recepient");
				String subject = event.getData().get("Subject");
				String message = event.getData().get("Message");
				email.setSubject(subject);
				email.setMsg(message);
				email.addTo(recepient);
				email.send();
				setRecentlySent();
				System.out.println("Email Sent With Message: " + message);
				return true;
			} catch (EmailException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
