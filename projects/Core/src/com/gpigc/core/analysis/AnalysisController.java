package com.gpigc.core.analysis;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationGenerator;

/**
 * Interacts with the core application, performing analysis on the data
 * persisted to the database.
 * 
 * @author GPIGC
 */
public class AnalysisController {

	private final List<AnalysisEngine> analysisEngines;

	private SystemDataGateway datastore;
	private NotificationGenerator notificationGenerator;


	public AnalysisController(SystemDataGateway datastore,
			NotificationGenerator notificationGenerator, List<ClientSystem> systems) throws ReflectiveOperationException{
		this.datastore = datastore;
		this.notificationGenerator = notificationGenerator;
		analysisEngines = instantiateEngines(systems);	
		if(analysisEngines == null)
			throw new ReflectiveOperationException("Analysis Engines could not be loaded");
	}

	/**
	 * Performs analysis on a given system
	 * @param systemId
	 *            The ID of the system to perform analysis upon
	 * @throws FailedToReadFromDatastoreException 
	 */
	public void systemUpdate(String systemID){
		System.out.println("System Update: " + systemID);
		for (AnalysisEngine engine : analysisEngines) {
			if (engine.getRegisteredSystem(systemID) != null) {
				System.out.println("Engine Registered: " + engine.getClass().getName());
				DataEvent event = engine.analyse(engine.getRegisteredSystem(systemID));
				if(event != null && notificationGenerator != null){
					notificationGenerator.generate(event);
					System.out.println("Notification triggered: " +engine.getClass().getName());
				}
			}
		}
	}

	/**
	 * Class load Analysis engines and register all system with them - for prototype only
	 * @param systemIDs
	 * @return engines
	 */
	private List<AnalysisEngine> instantiateEngines(List<ClientSystem> allSystems)  {
		File folder = new File(System.getProperty("user.dir") + "/src/com/gpigc/core/analysis/engine");
		File[] listOfFiles = folder.listFiles();
		List<AnalysisEngine> engines = new ArrayList<>();
		try {
			for (int i = 0; i < listOfFiles.length; i++) {
				String name = listOfFiles[i].getName().substring(0,
						listOfFiles[i].getName().lastIndexOf('.'));
				Constructor<?> constructor = Class.forName(
						"com.gpigc.core.analysis.engine."
								+ name)
										.getConstructor(List.class, SystemDataGateway.class);
				AnalysisEngine engine;
				engine = (AnalysisEngine) constructor.newInstance(
						getRegisteredSystems(name, allSystems), datastore);
				engines.add(engine);
			}
			return engines;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException |
				NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<ClientSystem> getRegisteredSystems(String simpleName, List<ClientSystem> allSystems) {
		System.out.println("Comparing: " + simpleName);
		List<ClientSystem> registeredSystems = new ArrayList<ClientSystem>();
		for(ClientSystem system : allSystems){
			if(system.getRegisteredEngineNames().contains(simpleName)){
				registeredSystems.add(system);
				System.out.println("Adding Engine: " + simpleName  + "  For System: "+ system.getID());
			}
		}
		return registeredSystems;
	}

	public List<AnalysisEngine> getAnalysisEngines() {
		return analysisEngines;
	}
}
