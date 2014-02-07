package com.gpigc.core.notification;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.mail.EmailException;

import com.gpigc.core.event.Event;
import com.gpigc.core.notification.engine.EmailEngine;

public class NotificationGenerator {
	private boolean recentlySent = false;

	public void generate(Event event) {
		String systemId = event.getSystemId();
		if (systemId.equals("1") && !recentlySent) {
			EmailEngine emailEngine = new EmailEngine();
			String email = "sftrabbit@gmail.com";
			String subject = "Notification: CPU usage too high!";
			String message = "CPU usage of system " + systemId + " has exceeded threshold! Value: " + event.getResult().getDataToSave().get("Mean");
			try {
				emailEngine.send(email, subject, message);
				
				recentlySent = true;
				
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						recentlySent = false;
					}
					
				}, 600000);
			} catch (EmailException e) {
				e.printStackTrace();
			}
		}
	}
}
