package com.gpigc.core.analysis;

import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;

public abstract class AnalysisEngine {


	private final List<ClientSystem> associatedSystems;
	protected final SystemDataGateway datastore;
	public final String name;
	
	public AnalysisEngine(List<ClientSystem> registeredSystems, SystemDataGateway datastore){
		this.associatedSystems = registeredSystems;
		this.datastore = datastore;
		this.name = this.getClass().getSimpleName();
	}

	public List<ClientSystem> getAssociatedSystems() {
		return associatedSystems;
	}

	public abstract DataEvent analyse(ClientSystem system);

	public List<SensorState> getSensorReadings(String systemID, String sensorID, int numberToGet) throws FailedToReadFromDatastoreException{
		return datastore.readMostRecent(systemID, sensorID, numberToGet).getRecords();
	}

	public ClientSystem getRegisteredSystem(String systemID){
		for(ClientSystem system: getAssociatedSystems()){
			if(system.getID().equals(systemID))
				return system;
		}
		return null;
	}
}
