package com.gpigc.core.analysis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.Parameter;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;

public class BoundedAnalysisEngine extends AnalysisEngine {

	public BoundedAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems,core);
	}

	@Override
	public DataEvent analyse(ClientSystem system) {

		boolean event = false;
		// Set up the data
		Map<Parameter, String> data = new HashMap<>();
		data.put(Parameter.SUBJECT, this.name+ " Notification");
		data.put(Parameter.RECIPIENT, "gpigc.alerts@gmail.com");
		data.put(Parameter.MESSAGE, "");

		// Check the sensors
		for (ClientSensor sensor : system.getSensors()) {
			boolean sensorResult = checkSensor(sensor, data, system);

			if (sensorResult)
				event = sensorResult;
		}
		if (event)
			return new DataEvent(data, system);
		// No event
		return null;
	}

	protected boolean checkSensor(ClientSensor sensor, Map<Parameter, String> data,
			ClientSystem system) {
		// Do we have the correct parameters
		if (hasCorrectKeys(sensor)) {
			double upperBound = Double.parseDouble(sensor.getParameters().get(
					Parameter.UPPER_BOUND));
			double lowerBound = Double.parseDouble(sensor.getParameters().get(
					Parameter.LOWER_BOUND));
			int numRecords = Integer.parseInt(sensor.getParameters().get(
					Parameter.NUM_RECORDS));
			try {
				double mean = getMean(getSensorReadings(system, sensor.getID(),
						numRecords));
				if (mean > upperBound) {
					data.put(Parameter.MESSAGE, data.get("Message")
							+ "Sensor with ID: " + sensor.getID()
							+ " has exceeded its upper limit. "
							+ "\nMean value was: " + mean + "\n\n");
					return true;
				}
				if (mean < lowerBound) {
					data.put(Parameter.MESSAGE, data.get("Message")
							+ "Sensor with ID: " + sensor.getID()
							+ " has fallen below its lower limit. "
							+ "\nMean value was: " + mean + "\n\n");
					return true;
				}
			} catch (FailedToReadFromDatastoreException e) {
				StandardMessageGenerator.couldNotReadData();
				e.printStackTrace();
			}
		} else {
			StandardMessageGenerator.wrongParams(name,system.getID());
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

	protected static double getMean(List<SensorState> sensorData) {
		if (sensorData.size() == 0)
			throw new IllegalArgumentException("Size cannot be 0");

		long sum = 0;
		for (int i = 0; i < sensorData.size(); i++) {
			sum += Double.parseDouble(sensorData.get(i).getValue());
		}
		return sum / sensorData.size();
	}

}
