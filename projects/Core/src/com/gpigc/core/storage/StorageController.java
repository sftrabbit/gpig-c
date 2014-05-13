package com.gpigc.core.storage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;

public class StorageController {

	private List<SystemDataGateway> datastores;


	public StorageController( List<ClientSystem> systems) throws ReflectiveOperationException {
		refreshSystems(systems);
	}

	public void refreshSystems(List<ClientSystem> systems) throws ReflectiveOperationException {
		datastores = instantiateDatastores(systems);
		if (datastores == null)
			throw new ReflectiveOperationException("Datastores could not be loaded");
	}

	public void push(Map<String, List<EmitterSystemState>> systemStates) {
		for (SystemDataGateway dataStore : getDatastores()) {
			for(ClientSystem system: dataStore.getAssociatedSystems()){
				if(systemStates.containsKey(system.getID())){
					try {
						dataStore.write(systemStates.get(system.getID()));
					} catch (FailedToWriteToDatastoreException e) {
						StandardMessageGenerator.failedToWrite(dataStore.name, system.getID());
						e.printStackTrace();
					}
				}
			}
		}
	}

	public QueryResult readMostRecent(ClientSystem system, String sensorID,
			int numberToGet) throws FailedToReadFromDatastoreException {
		for (SystemDataGateway dataStore : getDatastores()) {
			if(dataStore.name.equals(system.getSystemDataGatewayName()))
				return dataStore.readMostRecent(system.getID(), sensorID, numberToGet);
		}
		return null;
	}


	private List<SystemDataGateway> instantiateDatastores(List<ClientSystem> systems){
		System.err.println(1);
		File folder = new File(System.getProperty("user.dir")
				+ "/src/com/gpigc/core/storage/engine"); // TODO Not portable when jar is built. Need engines to be packages up and expanded by one-jar?
		System.err.println(2);
		File[] listOfFiles = folder.listFiles();
		System.err.println(3);
		List<SystemDataGateway> engines = new ArrayList<>();
		System.err.println(4);
		System.err.println("List of files: "+listOfFiles);
		System.err.println("Folder: "+folder.getAbsolutePath());
		
		if (listOfFiles == null) {
			System.err.println("Folder "+folder+" does not exist, so no "
					+ "datastores could be loaded.");
			return engines;
		}
		
		for (int i = 0; i < listOfFiles.length; i++) {
			System.err.println(5);
			try {
				String name = listOfFiles[i].getName().substring(0,
						listOfFiles[i].getName().lastIndexOf('.'));
				System.err.println(6);
				Constructor<?> constructor = Class.forName(
						"com.gpigc.core.storage.engine." + name)
						.getConstructor(List.class);
				System.err.println(7);
				SystemDataGateway engine = (SystemDataGateway) constructor
						.newInstance(getRegisteredSystems(name, systems));
				System.err.println(8);
				engines.add(engine);
				System.err.println(9);
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException 
					| InstantiationException | IllegalAccessException | 
					IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				System.err.println(10);
				return null;
			}
		}
		System.err.println(11);
		return engines;
	}

	private List<ClientSystem> getRegisteredSystems(String name, List<ClientSystem> allSystems) {
		List<ClientSystem> registeredSystems = new ArrayList<ClientSystem>();
		for(ClientSystem system : allSystems){
			if(system.getSystemDataGatewayName().equals(name))
				registeredSystems.add(system);
		}
		return registeredSystems;
	}

	public List<SystemDataGateway> getDatastores() {
		return datastores;
	}


}
