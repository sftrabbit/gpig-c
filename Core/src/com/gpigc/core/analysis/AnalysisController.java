package com.gpigc.core.analysis;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gpig.client.SystemDataGateway;
import com.gpigc.core.eventnotify.EventNotify;
import com.gpigc.core.eventnotify.InvalidEventNotifyException;

/**
 * Interacts with the core application, performing analysis on the data persisted to the database.
 * 
 * @author GPIGC
 */
public class AnalysisController {

	private List<AnalysisEngine> engines;
	
	private SystemDataGateway database;
	
	/**
	 * Initialises analysis controller
	 * 
	 * @param database	Passes database abstraction layer in as a dependency
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public AnalysisController(SystemDataGateway database) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.database = database;
		engines = new ArrayList<AnalysisEngine>();
		instantiateEngines();
	}

	/**
	 * Performs analysis on a given system
	 * 
	 * @param systemId	The ID of the system to perform analysis upon
	 */
	public void systemUpdate(String systemId) {
		for(AnalysisEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId)) {
				processResult(engine.getEngineName(), engine.analyse());
			}
		}
	}

	/**
	 * Performs post processing on the analysis result object 
	 * 
	 * @param engineName	Name of the analysis engine that has analysed the data
	 * @param result		The result of the analysis.
	 */
	private void processResult(String engineName, Result result) {
		//database.write(engineName, result);
		// TODO write back data
		try {
			new EventNotify(result, engineName, "1");
		} catch (InvalidEventNotifyException e) {
			e.printStackTrace();
		}
		System.out.println("Bitches need to notify: " + result.getDataToSave().toString());
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
	private void instantiateEngines() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		File folder = new File("/"+System.getProperty("user.dir") + "/src/com/gpigc/core/analysis/engine");
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < listOfFiles.length; i++) {
			Constructor<?> constructor = Class.forName("com.gpigc.core.analysis.engine." + listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf('.'))).getConstructor(SystemDataGateway.class);
			AnalysisEngine engine = (AnalysisEngine) constructor.newInstance(database);
			engines.add(engine);
		}
	}	
}
