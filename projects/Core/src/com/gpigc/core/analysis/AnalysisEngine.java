package com.gpigc.core.analysis;

import java.sql.SQLException;
import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;

public abstract class AnalysisEngine {


	private final List<ClientSystem> associatedSystems;
	public final String name;
	private final Core core;
	
	public AnalysisEngine(List<ClientSystem> registeredSystems, Core core){
		this.associatedSystems = registeredSystems;
		this.core = core;
		this.name = this.getClass().getSimpleName();
	}

	public List<ClientSystem> getAssociatedSystems() {
		return associatedSystems;
	}

	public abstract DataEvent analyse(ClientSystem system);

	public List<SensorState> getSensorReadings(ClientSystem system, String sensorID, int numberToGet) throws FailedToReadFromDatastoreException {
		return core.getDatastoreController().readMostRecent(system, sensorID, numberToGet).getRecords();
	}

	public ClientSystem getRegisteredSystem(String systemID){
		for(ClientSystem system: getAssociatedSystems()){
			if(system.getID().equals(systemID))
				return system;
		}
		return null;
	}
}
