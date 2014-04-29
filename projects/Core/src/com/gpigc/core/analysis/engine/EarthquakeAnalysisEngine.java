package com.gpigc.core.analysis.engine;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class EarthquakeAnalysisEngine extends AnalysisEngine {

	private final int NUM_RECORDS = 1;
	public final static String EARTHQUAKE_SENSOR_ID = "EQ";
	
	public EarthquakeAnalysisEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
	}

	@Override
	public DataEvent analyse(ClientSystem system) {
		System.out.println("Analyse -----");
		if(system.hasSensorWithID(EARTHQUAKE_SENSOR_ID)){
			try {
				SensorState sensorState = 
						getSensorReadings(system.getID(),EARTHQUAKE_SENSOR_ID, NUM_RECORDS).get(0);
				double magnitude = Double.parseDouble(sensorState.getValue().split(",")[0]);
				System.out.println("Magnitude: "+ magnitude);
			if(magnitude >= 1.0){
				System.out.println(" EarthQuake Over: 1.0 Detected");
				Map<String,String> data = new HashMap<>();
				data.put("Message", "Earthquake measuring " + magnitude + 
						" was detected by system: " + system.getID() + ". Time: "
						+ new SimpleDateFormat("HH:mm:ss").format(
								sensorState.getCreationTimestamp()));
				data.put("Subject", "Earthquake Notification");
				data.put("Recepient", "gpigc.alerts@gmail.com");
				data.put("Long", sensorState.getValue().split(",")[2]);
				data.put("Lat", sensorState.getValue().split(",")[1]);
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
