package com.gpigc.core.notification;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class NotificationEngine {

	protected String engineName;
	protected List<String> associatedSystems;
	protected boolean recentlySent;

	private static final int COOLDOWN_MINS = 10;
 
	public List<String> getAssociatedSystems() {
		return associatedSystems;
	}

	public String getEngineName() {
		return engineName;
	}

	public boolean getRecentlySent() {
		return recentlySent;
	}
	
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

	public abstract void send(String recepient, String subject, String message);

}