package com.gpigc.core.analysis.engine;

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

public class TrafficAnalysisEngine extends AnalysisEngine {

	private static final String TRAFFIC_SENSOR_ID = "Incident";
	private static final int NUM_RECORDS = 1;

	public TrafficAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
		
	}

	@Override
	public DataEvent analyse(ClientSystem system) {
		if (system.hasSensorWithID(TRAFFIC_SENSOR_ID) && system.getParameters().containsKey(Parameter.RECIPIENT)) {
			System.out.println("Analysing for event");
			Map<Parameter,String> data = new HashMap<>();
			//Get the Data
			try{
			SensorState sensorState = getSensorReadings(system,
					TRAFFIC_SENSOR_ID, NUM_RECORDS).get(0);
			
			data.put(Parameter.RECIPIENT, system.getParameters().get(Parameter.RECIPIENT));
			data.put(Parameter.SUBJECT, "GPIG-C: Traffic Monitor");
			data.put(Parameter.MESSAGE, "An Accident Has Occured: \n" + sensorState.getValue());

			return new DataEvent(data, system);
			}catch(FailedToReadFromDatastoreException e){
				e.printStackTrace();
				StandardMessageGenerator.couldNotReadData();
			}
		}
		System.out.println("No event generated");
		return null;
	}

}
