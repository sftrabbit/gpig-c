package com.gpigc.core.notification;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.gpigc.core.event.Event;

public class NotificationGenerator {

	private List<NotificationEngine> engines;

	public NotificationGenerator() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		engines = new ArrayList<NotificationEngine>();
		instantiateEngines();
	}

	public void generate(Event event) {
		String systemId = event.getSystemId();
		for (NotificationEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId) && !engine.getRecentlySent()) {
				String recepient = "sftrabbit@gmail.com";
				String subject = "Notification: CPU usage too high!";
				String message = "CPU usage of system " + systemId + " has exceeded threshold! Value: " + event.getResult().getDataToSave().get("Mean");
				engine.send(recepient, subject, message);
			}
		}
	}

	private void instantiateEngines() throws MalformedURLException,	ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		File folder = new File("/" + System.getProperty("user.dir") + "/src/com/gpigc/core/notification/engine");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			Constructor<?> constructor = Class.forName("com.gpigc.core.notification.engine." + listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf('.'))).getConstructor();
			NotificationEngine engine = (NotificationEngine) constructor.newInstance();
			engines.add(engine);
		}
	}
}
