package com.gpigc.core.notification;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.event.DataEvent;

/**
 * Generates and sends notifications from event objects
 * 
 * @author GPIGC
 */
public class NotificationGenerator {

	private final List<NotificationEngine> notificationEngines;


	public NotificationGenerator( List<ClientSystem> systems)
			throws ReflectiveOperationException {
		notificationEngines = instantiateEngines(systems);
		if(getNotificationEngines() == null){
			throw new ReflectiveOperationException("Notification Engines could not be loaded");
		}
	}

	/**
	 * Generates notifications based on a given event
	 * 
	 * @param event
	 *            The event object containing information about the result
	 */
	public void generate(DataEvent event) {
		for (NotificationEngine engine : getNotificationEngines()) {
			List<ClientSystem> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(event.getSystem())) {
				engine.send(event);
			}
		}
	}

	private List<NotificationEngine> instantiateEngines(List<ClientSystem> systems)
	{
		File folder = new File(System.getProperty("user.dir")
				+ "/src/com/gpigc/core/notification/engine");
		File[] listOfFiles = folder.listFiles();

		List<NotificationEngine> engines = new ArrayList<>();

		for (int i = 0; i < listOfFiles.length; i++) {
			try {
			Constructor<?> constructor = Class.forName(
						"com.gpigc.core.notification.engine."
								+ listOfFiles[i].getName().substring(0,
										listOfFiles[i].getName().lastIndexOf('.')))
										.getConstructor(List.class, int.class);
			
			NotificationEngine engine = (NotificationEngine) constructor
					.newInstance(systems, 500); //XXX Could have this as a param
			engines.add(engine);
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException 
					| InstantiationException | IllegalAccessException | 
					IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		return engines;
	}

	public List<NotificationEngine> getNotificationEngines() {
		return notificationEngines;
	}
}
