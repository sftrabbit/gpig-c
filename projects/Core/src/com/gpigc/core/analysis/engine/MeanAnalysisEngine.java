package com.gpigc.core.analysis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.SensorParameter;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class MeanAnalysisEngine extends AnalysisEngine {

	private static final int NUM_RECORDS = 5;

	public MeanAnalysisEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
	}

	@Override
	public DataEvent analyse(ClientSystem system) {
		boolean event = false;
		//Set up the data
		Map<String, String> data = new HashMap<>();
		data.put("Subject", "MeanEngine Notification");
		data.put("Recepient", "gpigc.alerts@gmail.com");
		data.put("Message", "");

		//Check the sensors
		for (ClientSensor sensor : system.getSensors()) {
			System.out.println("New Sensor: " + sensor.getID());
			boolean sensorDone =  checkSensor(sensor,data, system.getID());
			if(sensorDone)
				event = sensorDone;
		}
		if(event)
			return new DataEvent(data, system);
		// No event
		return null;
	}

	private boolean checkSensor(ClientSensor sensor, Map<String, String> data, String systemID) {
		// Do we have the correct parameters
		if (hasCorrectKeys(sensor)) {
			System.out.println("Correct Params: " + sensor.getID());
			long upperBound = Long.parseLong(sensor.getParameters().get(
					SensorParameter.UPPER_BOUND));
			long lowerBound = Long.parseLong(sensor.getParameters().get(
					SensorParameter.LOWER_BOUND));
			try {
				long mean = getMean(getSensorReadings(systemID,
						sensor.getID(), NUM_RECORDS));
				System.out.println("Mean for " + sensor.getID() +" is " + mean);
				if (mean > upperBound) {
					data.put("Message", data.get("Message")
							+ "Sensor with ID: " + sensor.getID()
							+ " has exceeded its upper limit. "
							+ "\nMean value was: " + mean + "\n\n");
					System.out.println("Over Upper Limit: " + sensor.getID());
					return true;
				}
				if (mean < lowerBound) {
					data.put("Message", data.get("Message")
							+ "Sensor with ID: " + sensor.getID()
							+ " has fallen below its lower limit. "
							+ "\nMean value was: " + mean + "\n\n");
					System.out.println("Under Lower Limit: " + sensor.getID());
					return true;
				}
			} catch (FailedToReadFromDatastoreException e) {
				System.out.println("Could not read data, will try on next update.");
				e.printStackTrace();
			}
		} else {
			System.out.println("System Passed in does not have the correct parameters. Not Analysing");
		}
		return false;
	}

	private boolean hasCorrectKeys(ClientSensor sensor) {
		if(sensor.getParameters().containsKey(SensorParameter.LOWER_BOUND)
				&& sensor.getParameters().containsKey(SensorParameter.UPPER_BOUND))
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
