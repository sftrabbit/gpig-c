package com.gpigc.core.analysis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.analysis.ClientSystem;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class EarthquakeAnalysisEngine extends AnalysisEngine {

	private final int NUM_RECORDS = 1;
	
	public EarthquakeAnalysisEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
	}

	@Override
	public DataEvent analyse(ClientSystem system) {
		System.out.println("Analyse -----");
		if(system.getSensorIDs().contains("EQ")){
			try {
				SensorState sensorState = 
						getSensorReadings(system.getSystemID(),"EQ", NUM_RECORDS).get(0);
				double magnitude = Double.parseDouble(sensorState.getValue().split(",")[0]);
				System.out.println("Magnitude: "+ magnitude);
			if(magnitude > 1.0){
				System.out.println(" EarthQuake Over: 1.0 Detected");
				Map<String,String> data = new HashMap<>();
				data.put("Message", "Earthquake measuring " + magnitude + 
						" was detected by system: " + system.getSystemID());
				data.put("Subject", "Earthquake Notification");
				data.put("Recepient", "gpigc.alerts@gmail.com");
				return new DataEvent(data, system);
			}
			} catch (FailedToReadFromDatastoreException e) {
				System.out.println("Failed to read from database, will try again on next update");
				e.printStackTrace();
			}
		}
			
		return null;
	}

}
