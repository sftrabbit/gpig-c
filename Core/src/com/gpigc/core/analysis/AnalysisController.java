package com.gpigc.core.analysis;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import com.gpig.client.SystemDataGateway;

public class AnalysisController {

	private List<AnalysisEngine> engines;
	
	private SystemDataGateway database;
	
	public AnalysisController(SystemDataGateway database) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.database = database;
		engines = new ArrayList<AnalysisEngine>();
		instantiateEngines();
	}

	public void systemUpdate(String systemId) {
		for(AnalysisEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId)) {
				processResult(engine.getEngineName(), engine.analyse());
			}
		}
	}

	private void processResult(String engineName, Result result) {
		//database.write(engineName, result);
		// TODO write back data
		if(result.isNotify()){
			// TODO Sort out notifications
			System.out.println("Bitches need to notify: " + result.getDataToSave().toString());
		}
	}

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