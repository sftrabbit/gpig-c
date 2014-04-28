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

	private static final int NUM_RECORDS = 10;

	public MeanAnalysisEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
	}

	@Override
	public DataEvent analyse(ClientSystem system){

		for(ClientSensor sensor: system.getSensors()){
			//Do we have the correct parameters
			if(sensor.getParameters().containsKey(SensorParameter.LOWER_BOUND) &&
					sensor.getParameters().containsKey(SensorParameter.UPPER_BOUND)){
				Long upperBound = (Long) sensor.getParameters().get(SensorParameter.UPPER_BOUND);
				Long lowerBound = (Long) sensor.getParameters().get(SensorParameter.LOWER_BOUND);
				try{
					long mean = getMean(getSensorReadings(system.getID(),sensor.getID(), NUM_RECORDS));
					System.out.println("Mean is: " + mean);
					Map<String,String> data = new HashMap<>();
					if(mean > upperBound.longValue()){
						data.put("Message", "Sensor with ID: " + sensor.getID() + "has exceeded its upper limit. "
								+ "\nMean value was: " + mean);
					}
					if(mean < lowerBound.longValue()){
						data.put("Message", "Sensor with ID: " + sensor.getID() + "has fallen below its lower limit. "
								+ "\nMean value was: " + mean);
					}
					return new DataEvent(data, system);
				} catch (FailedToReadFromDatastoreException e) {
					System.out.println("Could not read data, will try on next update.");
					e.printStackTrace();
				}
			}else{
				System.out.println("System Passed in does not have the correct parameters. Not Analysing");
			}
		}
		//No event
		return null;
	}

	protected static long getMean(List<SensorState> sensorData) {
		if(sensorData.size() == 0)
			throw new IllegalArgumentException("Size cannot be 0");

		System.out.println(sensorData.size());
		long sum = 0;
		for (int i = 0; i < sensorData.size(); i++) {
			sum += Long.parseLong(sensorData.get(i).getValue());
		}
		return sum/sensorData.size();
	}

}
