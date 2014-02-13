package com.gpigc.core.notification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import com.gpigc.core.event.Event;

/**
 * Generates and sends notifications from event objects
 * 
 * @author GPIGC
 */
public class NotificationGenerator {

	private List<NotificationEngine> engines;

	/**
	 * Initialises the notification generator
	 * 
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public NotificationGenerator() throws MalformedURLException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		engines = new ArrayList<NotificationEngine>();
		instantiateEngines();
	}

	/**
	 * Generates notifications based on a given event
	 * 
	 * @param event
	 *            The event object containing information about the result
	 */
	public void generate(Event event) {
		String systemId = event.getSystemId();
		for (NotificationEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId)
					&& !engine.getRecentlySent()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader("email.txt"));
					String recipient = br.readLine();
				
					String subject = "Notification: CPU usage too high!";
					String message = "CPU usage of system " + systemId
							+ " has exceeded threshold! Value: "
							+ event.getResult().getDataToSave().get("Mean");
					engine.send(recipient, subject, message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setEngines(List<NotificationEngine> engines) {
		this.engines = engines;
	}

	/**
	 * Performs class loading of notification engines allowing for runtime
	 * additions
	 * 
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private void instantiateEngines() throws MalformedURLException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		File folder = new File(System.getProperty("user.dir")
				+ "/src/com/gpigc/core/notification/engine");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			Constructor<?> constructor = Class.forName(
					"com.gpigc.core.notification.engine."
							+ listOfFiles[i].getName().substring(0,
									listOfFiles[i].getName().lastIndexOf('.')))
					.getConstructor();
			NotificationEngine engine = (NotificationEngine) constructor
					.newInstance();
			engines.add(engine);
		}
	}
}
