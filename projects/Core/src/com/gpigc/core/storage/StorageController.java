package com.gpigc.core.storage;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
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
		File folder = new File(Core.ENGINES_FOLDER_PATH); // TODO Not portable when jar is built. Need engines to be packages up and expanded by one-jar?
		File[] listOfFiles = folder.listFiles();
		List<SystemDataGateway> engines = new ArrayList<>();
		
		if (listOfFiles == null) {
			StandardMessageGenerator.failedToFindEngines(folder.getAbsolutePath(), "storage");
			return engines;
		}
		
		for (int i = 0; i < listOfFiles.length; i++) {
			try {
				String name = listOfFiles[i].getName().substring(0,
						listOfFiles[i].getName().lastIndexOf('.'));
				Constructor<?> constructor = Class.forName(
						"com.gpigc.core.storage.engine." + name)
						.getConstructor(List.class);
				SystemDataGateway engine = (SystemDataGateway) constructor
						.newInstance(getRegisteredSystems(name, systems));
				engines.add(engine);
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException 
					| InstantiationException | IllegalAccessException | 
					IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				System.out.println("Issue when loading a StorageController: "+
						e.getMessage());
				return engines;
			}
		}
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
