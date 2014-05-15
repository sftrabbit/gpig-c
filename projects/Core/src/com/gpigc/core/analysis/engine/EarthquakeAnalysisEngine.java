package com.gpigc.core.analysis.engine;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.Parameter;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;

public class EarthquakeAnalysisEngine extends AnalysisEngine {

	private final int NUM_RECORDS = 1;
	public final static String EARTHQUAKE_SENSOR_ID = "EQ";

	public EarthquakeAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
	}

	@Override
	public DataEvent analyse(ClientSystem system) {
		if (system.hasSensorWithID(EARTHQUAKE_SENSOR_ID)) {
			try {
				// Get the Data
				SensorState sensorState = getSensorReadings(system, EARTHQUAKE_SENSOR_ID, NUM_RECORDS).get(0);

				// Get The magnitude
				double magnitude = Double.parseDouble(sensorState.getValue().split(",")[0]);

				// If we have a bound
				if (system.getSensorWithID(EARTHQUAKE_SENSOR_ID).getParameters().containsKey(Parameter.LOWER_BOUND)) {
					double lowerBound = Double.parseDouble(system.getSensorWithID(EARTHQUAKE_SENSOR_ID).getParameters().get(Parameter.LOWER_BOUND));

					if (magnitude >= lowerBound) {
						Map<Parameter, String> data = new HashMap<>();
						// Write the message
						data.put(Parameter.MESSAGE, "Earthquake measuring " + magnitude + " was detected by system: " + system.getID() + ". Time: "
								+ new SimpleDateFormat("HH:mm:ss").format(sensorState.getCreationTimestamp()));

						data.put(Parameter.SUBJECT, this.name + " Notification");
						data.put(Parameter.RECIPIENT, "gpigc.alerts@gmail.com");
						data.put(Parameter.LONG, sensorState.getValue().split(",")[2]);
						data.put(Parameter.LAT, sensorState.getValue().split(",")[1]);

						return new DataEvent(data, system);
					}
				} else {
					StandardMessageGenerator.wrongParams(system.getID(), name);
				}
			} catch (FailedToReadFromDatastoreException e) {
				StandardMessageGenerator.couldNotReadData();
				e.printStackTrace();
			}
		}
		return null;
	}

}
