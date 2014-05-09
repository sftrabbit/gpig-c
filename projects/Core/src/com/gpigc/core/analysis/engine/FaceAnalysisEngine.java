/**
 * 
 */
package com.gpigc.core.analysis.engine;

import java.util.ArrayList;
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

/**
 * Analyses the faces seen in an image detected by a sensor
 * 
 * @author GPIG-C
 */
public class FaceAnalysisEngine extends AnalysisEngine {

	// Map<ClientSystem, Mat> systemExampleFacesCache;

	public FaceAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
		//systemExampleFacesCache = new HashMap<ClientSystem, Mat>();
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse(com.gpigc.core.ClientSystem)
	 */
	@Override
	public DataEvent analyse(ClientSystem system) {

		if(system.getParameters().containsKey(Parameter.FACES)){
//			Mat exampleFaces;
//			if (systemExampleFacesCache.keySet().contains(system)) {
//				exampleFaces = systemExampleFacesCache.get(system);
//			} else {
//				String base64faceData = system.getParameters().get(Parameter.FACES);
//				exampleFaces = parseFaces(base64faceData);
//			}
			
			// Get data from sensor
			List<SensorState> values;
			try {
				values = getSensorData(system);
				for(SensorState sensorState: values){
					// TODO Actually test to see if face seen is allowed
					boolean isValid = true;
					if (isValid) {
						return generateSuccessEvent(system);
					} else {
						return generateFailureEvent(system);
					}
				}
			} catch (FailedToReadFromDatastoreException e) {
				e.printStackTrace();
				StandardMessageGenerator.couldNotReadData();
			}
		}else{
			StandardMessageGenerator.wrongParams(system.getID(), name);
		}
		return null;
	}

//	private Mat parseFaces(String base64) {
//		TODO Parse example faces matrix from base64
//	}
	
	private DataEvent generateSuccessEvent(ClientSystem system) {
		Map<String,String> data = new HashMap<>();
		data.put("Message", "Face recognitition in system " 
				+ system.getID() + " detected an authorised person and is "
				+ "allowing them access.");
		data.put("Subject", this.name+ " Notification");
		data.put("Recepient", "gpigc.alerts@gmail.com");
		return new DataEvent(data, system);
	}

	private DataEvent generateFailureEvent(ClientSystem system) {
		Map<String,String> data = new HashMap<>();
		data.put("Message", "Face recognitition in system " 
				+ system.getID() + " detected an unauthorised person and is "
				+ "denying them access.");
		data.put("Subject", this.name+ " Notification");
		data.put("Recepient", "gpigc.alerts@gmail.com");
		return new DataEvent(data, system);
	}

	private List<SensorState> getSensorData(ClientSystem system) 
			throws FailedToReadFromDatastoreException {
		List<SensorState> values = new ArrayList<>();
		for(ClientSensor sensor: system.getSensors()){
			SensorState state = 
					getSensorReadings(system, sensor.getID(), 1).get(0);
			if(state!=null){
				values.add(state);
			}else{
				StandardMessageGenerator.sensorValueMissing(
						system.getID(),sensor.getID());
				return null;
			}
		}
		return values;
	}

}
