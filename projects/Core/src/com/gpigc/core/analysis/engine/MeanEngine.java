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

public class MeanEngine extends AnalysisEngine {

	private static final int NUM_RECORDS = 10;

	public MeanEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
	}

	@Override
	public DataEvent analyse(ClientSystem system){
		if(system.getParameters().containsKey("LOWER_BOUND") &&
				system.getParameters().containsKey("UPPER_BOUND")){
			try{

				for(String sensorID: system.getSensorIDs()){
					if(system.getParameters().get("LOWER_BOUND").containsKey(sensorID)
							&& system.getParameters().get("UPPER_BOUND").containsKey(sensorID)){
						Long upperBound = (Long) system.getParameters().get("UPPER_BOUND").get(sensorID);
						Long lowerBound = (Long) system.getParameters().get("LOWER_BOUND").get(sensorID);
						long mean = getMean(getSensorReadings(system.getSystemID(),sensorID, NUM_RECORDS));
						System.out.println("Mean is: " + mean);
						Map<String,String> data = new HashMap<>();
						if(mean > upperBound.longValue()){
							data.put("Message", "Sensor with ID: " + sensorID + "has exceeded its upper limit. "
									+ "\nMean value was: " + mean);
						}
						if(mean < lowerBound.longValue()){
							data.put("Message", "Sensor with ID: " + sensorID + "has fallen below its lower limit. "
									+ "\nMean value was: " + mean);
						}
						return new DataEvent(data, system);
					}
				}

			}catch(FailedToReadFromDatastoreException e){
				System.out.println("Failed to read from database, will try again on next update");
				e.printStackTrace();
			}
		}else{
		//TODO may want to throw exception
		System.out.println("System Passed in does not have the correct parameters. Not Analysing");
		}
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
