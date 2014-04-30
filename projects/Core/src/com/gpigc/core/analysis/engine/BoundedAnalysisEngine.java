package com.gpigc.core.analysis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class BoundedAnalysisEngine extends AnalysisEngine {

	public BoundedAnalysisEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
	}

	@Override
	public DataEvent analyse(ClientSystem system) {
		boolean event = false;
		// Set up the data
		Map<String, String> data = new HashMap<>();
		data.put("Subject", this.name+ " Notification");
		data.put("Recepient", "gpigc.alerts@gmail.com");
		data.put("Message", "");

		// Check the sensors
		for (ClientSensor sensor : system.getSensors()) {
			boolean sensorDone = checkSensor(sensor, data, system.getID());
			if (sensorDone)
				event = sensorDone;
		}
		if (event)
			return new DataEvent(data, system);
		// No event
		return null;
	}

	protected boolean checkSensor(ClientSensor sensor, Map<String, String> data,
			String systemID) {
		// Do we have the correct parameters
		if (hasCorrectKeys(sensor)) {
			double upperBound = Double.parseDouble(sensor.getParameters().get(
					Parameter.UPPER_BOUND));
			double lowerBound = Double.parseDouble(sensor.getParameters().get(
					Parameter.LOWER_BOUND));
			int numRecords = Integer.parseInt(sensor.getParameters().get(
					Parameter.NUM_RECORDS));
			try {
				long mean = getMean(getSensorReadings(systemID, sensor.getID(),
						numRecords));
				if (mean > upperBound) {
					data.put("Message", data.get("Message")
							+ "Sensor with ID: " + sensor.getID()
							+ " has exceeded its upper limit. "
							+ "\nMean value was: " + mean + "\n\n");
					return true;
				}
				if (mean < lowerBound) {
					data.put("Message", data.get("Message")
							+ "Sensor with ID: " + sensor.getID()
							+ " has fallen below its lower limit. "
							+ "\nMean value was: " + mean + "\n\n");
					return true;
				}
			} catch (FailedToReadFromDatastoreException e) {
				System.out
						.println("Could not read data, will try on next update.");
				e.printStackTrace();
			}
		} else {
			System.out
					.println("System Passed in does not have the correct parameters. Not Analysing");
		}
		return false;
	}

	private boolean hasCorrectKeys(ClientSensor sensor) {
		if (sensor.getParameters().containsKey(Parameter.LOWER_BOUND)
				&& sensor.getParameters().containsKey(
						Parameter.UPPER_BOUND)
				&& sensor.getParameters().containsKey(
						Parameter.NUM_RECORDS))
			return true;

		return false;
	}

	protected static long getMean(List<SensorState> sensorData) {
		if (sensorData.size() == 0)
			throw new IllegalArgumentException("Size cannot be 0");

		long sum = 0;
		for (int i = 0; i < sensorData.size(); i++) {
			sum += Long.parseLong(sensorData.get(i).getValue());
		}
		return sum / sensorData.size();
	}

}
