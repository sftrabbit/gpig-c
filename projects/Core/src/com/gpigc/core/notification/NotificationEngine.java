package com.gpigc.core.notification;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.event.DataEvent;

/**
 * Provides the basic functionality and abstract methods that all notification engines require
 * 
 * @author GPIGC
 */
public abstract class NotificationEngine {

	protected boolean recentlySent;

	private static final int COOLDOWN_MINS = 10;

	private final List<ClientSystem> registeredSystems;


	public NotificationEngine(List<ClientSystem>registeredSystems){
		this.registeredSystems = registeredSystems;
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
	 */
	public abstract void send(DataEvent event);

	public List<ClientSystem> getAssociatedSystems() {
		return registeredSystems;
	}

}