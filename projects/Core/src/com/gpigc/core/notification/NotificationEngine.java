package com.gpigc.core.notification;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides the basic functionality and abstract methods that all notification engines require
 * 
 * @author GPIGC
 */
public abstract class NotificationEngine {

	protected String engineName;
	protected List<String> associatedSystems;
	protected boolean recentlySent;

	private static final int COOLDOWN_MINS = 10;
 
	/**
	 * @return A list of systems ID associated with this instance of the notification engine
	 */
	public List<String> getAssociatedSystems() {
		return associatedSystems;
	}

	/**
	 * @return The name of the notification engine
	 */
	public String getEngineName() {
		return engineName;
	}

	/**
	 * @return Whether a notification has been sent by the engine within the last cooldown period
	 */
	public boolean getRecentlySent() {
		return recentlySent;
	}

	/**
	 * Sets a notification as being sent by the engine and initiates a cooldown period where no
	 * further notifications can be sent
	 */
	protected void setRecentlySent() {
		recentlySent = true;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				recentlySent = false;
			}
			
		}, COOLDOWN_MINS * 60 * 1000);
	}

	/**
	 * Sends a notification
	 * @param recepient Who the notification is for
	 * @param subject   What the notification is about
	 * @param message   Further information about the notification
	 */
	public abstract void send(String recepient, String subject, String message);

}