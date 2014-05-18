package com.gpigc.core.storage;

import java.util.ArrayList;
import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Controller;
import com.gpigc.core.Core;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;

public class StorageController extends Controller {

	private List<SystemDataGateway> datastores;

	public StorageController(List<ClientSystem> systems, Core core) {
		super(ControllerType.storage, core);
		refreshSystems(systems);
	}

	@SuppressWarnings("unchecked")
	public void refreshSystems(List<ClientSystem> systems) {
		datastores = (List<SystemDataGateway>) instantiateEngines(systems,
				List.class);
	}

	public void push(EmitterSystemState state) {
		for (SystemDataGateway dataStore : getDatastores()) {
			for (ClientSystem system : dataStore.getAssociatedSystems()) {
				if (state.getSystemID().equals(system.getID())) {
					try {
						dataStore.write(state);
					} catch (FailedToWriteToDatastoreException e) {
						StandardMessageGenerator.failedToWrite(dataStore.name,
								system.getID());
						e.printStackTrace();
					}
				}
			}
		}
	}

	public QueryResult readMostRecent(ClientSystem system, String sensorID,
			int numberToGet) throws FailedToReadFromDatastoreException {
		for (SystemDataGateway dataStore : getDatastores()) {
			if (dataStore.name.equals(system.getSystemDataGatewayName()))
				return dataStore.readMostRecent(system.getID(), sensorID,
						numberToGet);
		}
		return null;
	}

	protected List<ClientSystem> getRegisteredSystems(String name,
			List<ClientSystem> allSystems) {
		List<ClientSystem> registeredSystems = new ArrayList<ClientSystem>();
		for (ClientSystem system : allSystems) {
			if (system.getSystemDataGatewayName().equals(name))
				registeredSystems.add(system);
		}
		return registeredSystems;
	}

	public List<SystemDataGateway> getDatastores() {
		return datastores;
	}

}
