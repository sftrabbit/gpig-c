package com.gpigc.analysis;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

//import com.gpigc.database.SystemData;
import com.gpigc.database.SystemDataGateway;

public class AnalysisController {

	private List<AnalysisEngine> engines;
	
	private SystemDataGateway database;
	
	public AnalysisController() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		//database = new SystemDataGateway();
		
		engines = new ArrayList<AnalysisEngine>();
		//engines.addall(new MeanAnalysis());
		
		instantiateEngines();
	}

	public void systemUpdate(String systemId) {
		for(AnalysisEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId)) {
				engine.analyse().process();
			}
		}
	}

	private void instantiateEngines() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
//		File folder = new File("/"+System.getProperty("user.dir") + "/resources/engines");
//        File[] listOfFiles = folder.listFiles(); 
//        ClassLoader loader;
//        String className;
//        URLConnection theURL;
//        for(int i = 0; i < listOfFiles.length; i++) {
//        	theURL = new URL(listOfFiles[i].toURI().toURL()).openConnection();
//        	loader = new URLClassLoader(new URL[]{listOfFiles[i].toURI().toURL()});
//        	if(listOfFiles[i].getName().contains(".")) {
//        		className = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf('.'));
//        	} else {
//        		className = listOfFiles[i].getName();
//        	}
//            Class<?> engine = loader.loadClass("com.gpigc.analysis."+className);
//            try {
//            	engines.add((AnalysisEngine) engine.newInstance());
//            } catch(Throwable t) {
//            	t.printStackTrace();
//            	throw new RuntimeException(t);
//            }
//        }
		File folder = new File("/"+System.getProperty("user.dir") + "/src/com/gpigc/analysis/engines");
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < listOfFiles.length; i++) {
			Constructor<?> engCon = Class.forName("com.gpigc.analysis.engines." + listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf('.'))).getConstructor(SystemDataGateway.class);
			AnalysisEngine engine = (AnalysisEngine) engCon.newInstance(database);
			engines.add(engine);
		}
	}	
}