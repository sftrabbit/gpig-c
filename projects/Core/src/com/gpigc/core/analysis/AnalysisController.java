package com.gpigc.core.analysis;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.view.StandardMessageGenerator;

/**
 * Interacts with the core application, performing analysis on the data
 * persisted to the database.
 * 
 * @author GPIGC
 */
public class AnalysisController {

	private List<AnalysisEngine> analysisEngines;
	private final Core core;

	public AnalysisController(List<ClientSystem> systems, Core core) throws ReflectiveOperationException {
		this.core = core;
		refreshSystems(systems);
	}


	public void refreshSystems(List<ClientSystem> systems) throws ReflectiveOperationException {
		analysisEngines = instantiateEngines(systems);
		if (analysisEngines == null)
			throw new ReflectiveOperationException("Analysis Engines could not be loaded");
	}

	public void analyse(Set<String> systemIDs) {
		for(String currentSystemID: systemIDs){
			for (AnalysisEngine engine : analysisEngines) {
				//If this engine is registered to this system
				if (engine.getRegisteredSystem(currentSystemID) != null) {
					DataEvent event = engine.analyse(engine.getRegisteredSystem(currentSystemID));
					if (event != null) {
						core.generateNotification(event);
						StandardMessageGenerator.eventGenerated(engine.name, currentSystemID);
					}
				}
			}
		}
	}

	/**
	 * Class load Analysis engines and register all system with them - for
	 * prototype only
	 * 
	 * @param systemIDs
	 * @return engines
	 */
	private List<AnalysisEngine> instantiateEngines(
			List<ClientSystem> allSystems) {
		File folder = new File("./engines/analysis_engines");
		File[] listOfFiles = folder.listFiles();
		List<AnalysisEngine> engines = new ArrayList<>();
		
		try {
			for (int i = 0; i < listOfFiles.length; i++) {
				String name = listOfFiles[i].getName().substring(0,
						listOfFiles[i].getName().lastIndexOf('.'));
				Constructor<?> constructor = Class.forName(
						"com.gpigc.core.analysis.engine." + name)
						.getConstructor(List.class,Core.class);
				AnalysisEngine engine;
				engine = (AnalysisEngine) constructor.newInstance(
						getRegisteredSystems(name, allSystems),core);
				engines.add(engine);
			}
			return engines;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Issue when loading a AnalysisController: "+
					e.getMessage());
			return engines;
		}
	}

	private List<ClientSystem> getRegisteredSystems(String simpleName,
			List<ClientSystem> allSystems) {
		List<ClientSystem> registeredSystems = new ArrayList<ClientSystem>();
		for (ClientSystem system : allSystems) {
			if (system.getRegisteredEngineNames().contains(simpleName)) {
				registeredSystems.add(system);
			}
		}
		return registeredSystems;
	}

	public List<AnalysisEngine> getAnalysisEngines() {
		return analysisEngines;
	}

}
