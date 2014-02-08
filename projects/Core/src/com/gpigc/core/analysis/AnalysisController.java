package com.gpigc.core.analysis;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.core.event.Event;
import com.gpigc.core.notification.NotificationGenerator;

/**
 * Interacts with the core application, performing analysis on the data
 * persisted to the database.
 * 
 * @author GPIGC
 */
public class AnalysisController {

	private List<AnalysisEngine> engines;

	private SystemDataGateway database;
	private NotificationGenerator notificationGenerator;

	/**
	 * Initialises analysis controller
	 * 
	 * @param database
	 *            Passes database abstraction layer in as a dependency
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public AnalysisController(SystemDataGateway database,
			NotificationGenerator notificationGenerator)
			throws MalformedURLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.database = database;
		this.notificationGenerator = notificationGenerator;
		engines = new ArrayList<AnalysisEngine>();
		instantiateEngines();
	}

	/**
	 * Performs analysis on a given system
	 * 
	 * @param systemId
	 *            The ID of the system to perform analysis upon
	 */
	public void systemUpdate(String systemId) {
		for (AnalysisEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId)) {
				processResult(engine.getEngineName(), engine.analyse(), systemId);
			}
		}
	}

	/**
	 * Performs post processing on the analysis result object
	 * 
	 * @param engineName
	 *            Name of the analysis engine that has analysed the data
	 * @param result
	 *            The result of the analysis.
	 */
	private void processResult(String engineName, Result result, String systemId) {
		// database.write(engineName, result);
		// TODO write back data
		if (result.isNotify()) {
			Event event = new Event(result, engineName, systemId);
			notificationGenerator.generate(event);

			System.out.println("Notification triggered: "
					+ result.getDataToSave().toString());
		}
	}

	/**
	 * Performs class loading of analysis engines allowing for runtime additions
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
		List<String> systemIds = new ArrayList<String>();
		systemIds.add("1");
		File folder = new File("/" + System.getProperty("user.dir")
				+ "/src/com/gpigc/core/analysis/engine");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			Constructor<?> constructor = Class.forName(
					"com.gpigc.core.analysis.engine."
							+ listOfFiles[i].getName().substring(0,
									listOfFiles[i].getName().lastIndexOf('.')))
					.getConstructor(List.class, SystemDataGateway.class);
			AnalysisEngine engine = (AnalysisEngine) constructor
					.newInstance(systemIds, database);
			engines.add(engine);
		}
	}
}
