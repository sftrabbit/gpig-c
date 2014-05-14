package com.gpigc.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
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
		System.out.println("Loading: " + engineType + " engines ---------");
		List<Object> engines = new ArrayList<>();
		String parentDir ="com/gpigc/core/"+ engineType + "/engine/";
		File folder = new File(Core.ENGINES_FOLDER_PATH + "/" + engineType + "/" + parentDir);
		if (folder.listFiles() == null) {
			StandardMessageGenerator.failedToFindEngines(folder.getAbsolutePath(), engineType.toString());
			return engines;
		}
		try {
			URL url = new File(Core.ENGINES_FOLDER_PATH + "/" + engineType).getCanonicalFile().toURI().toURL();  
			URL[] urls = new URL[]{url};
			ClassLoader cl = URLClassLoader.newInstance(urls, this.getClass().getClassLoader());
			for(File engineFile : folder.listFiles()){
				if(engineFile.getName().endsWith(".class")){   //if it is a class file
					String className = engineFile.getName().substring(0, engineFile.getName().length()-6);
					String engineBinaryName = GPIGC_CORE_PACKAGE +"."+ engineType + ".engine."+ className;
					Class<?> engineClass = cl.loadClass(engineBinaryName);	
					try {
						Constructor<?> constructor = engineClass.getConstructor(constructorParams);
						engines.add(makeEngine(allSystems,constructor,className));
						System.out.println(" Loaded "+engineType + " engine: " + className);
					} catch (Exception e) {
						System.out.println(" Could not load "+engineType + " engine: " + className);
						e.printStackTrace();
					}
				}
			}
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
			System.out.println(e1.getMessage());
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
