package com.gpigc.core.notification;

import java.util.ArrayList;
import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Controller;
import com.gpigc.core.Core;
import com.gpigc.core.event.DataEvent;

/**
 * Handles all of the notification engines
 * 
 * @author GPIGC
 */
public class NotificationController extends Controller {

	private List<NotificationEngine> notificationEngines;

	public NotificationController(List<ClientSystem> systems, Core core) {
		super(ControllerType.notification, core);
		refreshSystems(systems);
	}

	@SuppressWarnings("unchecked")
	public void refreshSystems(List<ClientSystem> systems) {
		notificationEngines = (List<NotificationEngine>) instantiateEngines(
				systems, List.class, Integer.TYPE);
	}

	/**
	 * Generates notifications based on a given event
	 * 
	 * @param event
	 *            The event object containing information about the result
	 */
	public void generate(DataEvent event) {
		for (NotificationEngine engine : getNotificationEngines()) {
			List<ClientSystem> associatedSystems = engine
					.getAssociatedSystems();
			if (associatedSystems.contains(event.getSystem())) {
				engine.send(event);
			}
		}
	}

	protected List<ClientSystem> getRegisteredSystems(String name,
			List<ClientSystem> allSystems) {
		List<ClientSystem> registeredSystems = new ArrayList<ClientSystem>();
		for (ClientSystem system : allSystems) {
			if (system.getRegisteredEngineNames().contains(name)) {
				registeredSystems.add(system);
			}
		}
		return registeredSystems;
	}

	public List<NotificationEngine> getNotificationEngines() {
		return notificationEngines;
	}
}
