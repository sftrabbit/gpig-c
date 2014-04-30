package com.gpigc.core.analysis.engine;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
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
		if (system.hasSensorWithID(EARTHQUAKE_SENSOR_ID)) {
			try {
				//Get the Data
				SensorState sensorState = getSensorReadings(system.getID(),
						EARTHQUAKE_SENSOR_ID, NUM_RECORDS).get(0);

				//Get The magnitude
				double magnitude = Double.parseDouble(sensorState.getValue()
						.split(",")[0]);

				//If we have a bound
				if (system.getSensorWithID(EARTHQUAKE_SENSOR_ID)
						.getParameters()
						.containsKey(Parameter.LOWER_BOUND)) {
					double lowerBound = Double.parseDouble(system
							.getSensorWithID(EARTHQUAKE_SENSOR_ID)
							.getParameters().get(Parameter.LOWER_BOUND));
					
					if (magnitude >= lowerBound) {
						Map<String, String> data = new HashMap<>();
						//Write the message
						data.put("Message","Earthquake measuring "
										+ magnitude + " was detected by system: "
										+ system.getID() + ". Time: "
										+ new SimpleDateFormat("HH:mm:ss").format(sensorState
												.getCreationTimestamp()));
						
						data.put("Subject", this.name+ " Notification");
						data.put("Recepient", "gpigc.alerts@gmail.com");
						data.put("Long", sensorState.getValue().split(",")[2]);
						data.put("Lat", sensorState.getValue().split(",")[1]);
				
						return new DataEvent(data, system);
					}
				}else{
					System.out.println("No Bound specified");
				}
			} catch (FailedToReadFromDatastoreException e) {
				System.out.println("Failed to read from database, will try "
						+ "again on next update");
				e.printStackTrace();
			}
		}
		return null;
	}

}
