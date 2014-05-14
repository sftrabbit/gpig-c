package com.gpigc.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.core.view.StandardMessageGenerator;

public abstract class Controller {

	private static final String GPIGC_CORE_PACKAGE = "com.gpigc.core";

	private final ControllerType engineType;
	protected final Core core;

	public enum ControllerType{
		analysis,notification,storage;
	}

	public Controller(ControllerType engineType, Core core){
		this.engineType = engineType;
		this.core = core;
	}

	protected final List<? extends Object> instantiateEngines(
			List<ClientSystem> allSystems, Class<?>... constructorParams) {
		List<Object> engines = new ArrayList<>();
		File folder = new File(Core.ENGINES_FOLDER_PATH + "/");
		URL url;
		try {
			url = folder.getCanonicalFile().toURI().toURL();  
			System.out.println(url.toString());
			URL[] urls = new URL[]{url};
			ClassLoader cl = URLClassLoader.newInstance(urls);
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles == null) {
				StandardMessageGenerator.failedToFindEngines(folder.getAbsolutePath(), engineType.toString());
				return engines;
			}

			for (int i = 0; i < listOfFiles.length; i++) {
				try{
					String name = listOfFiles[i].getName().substring(0,
							listOfFiles[i].getName().lastIndexOf('.'));
					try{
						String engineBinaryName = GPIGC_CORE_PACKAGE +"."+ engineType + ".engine."+ name;
						System.out.println("Trying to load : " + engineBinaryName);
						Class<?> engineClass = cl.loadClass(engineBinaryName);
						Constructor<?> constructor = engineClass.getConstructor(constructorParams);
						engines.add(makeEngine(allSystems,constructor,name));
						System.out.println("Loaded Engine: "+ name);
					}catch (ClassNotFoundException error){
						System.out.println("Class: "+ name + " is not an " + engineType + " engine");
						error.printStackTrace();
					}
				} catch (InstantiationException | 
						NoSuchMethodException | SecurityException |
						IllegalAccessException | IllegalArgumentException |
						InvocationTargetException e) {
					e.printStackTrace();
					System.out.println("Issue when loading a AnalysisController: "+
							e.getMessage());
				}
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			System.out.println("URL was malformed in core: "+
					e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		return engines;
	}

	private final Object makeEngine(List<ClientSystem> allSystems,
			Constructor<?> constructor, String name) 
					throws InstantiationException, IllegalAccessException, 
					IllegalArgumentException, InvocationTargetException {
		switch(engineType){ 
		case analysis:
			AnalysisEngine analEngine = (AnalysisEngine) constructor.newInstance(
					getRegisteredSystems(name, allSystems),core);
			return analEngine;
		case notification:
			NotificationEngine noteEngine = (NotificationEngine) constructor
			.newInstance(getRegisteredSystems(name, allSystems), 30); //XXX Could have this as a param
			return noteEngine;
		case storage:
			SystemDataGateway storageEngine = (SystemDataGateway) constructor
			.newInstance(getRegisteredSystems(name, allSystems));
			return storageEngine;
		}	
		throw new RuntimeException("Engine Type now handled");
	}	


	public abstract void refreshSystems(List<ClientSystem> systems);

	protected abstract List<ClientSystem> getRegisteredSystems(String name,
			List<ClientSystem> allSystems);
}
