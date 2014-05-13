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

import org.opencv.core.Mat;

/**
 * Analyses the faces seen in an image detected by a sensor
 * 
 * Requires OpenCV
 * 
 * @author GPIG-C
 */
public class FaceAnalysisEngine extends AnalysisEngine {

	private Map<ClientSystem, Mat> systemExampleFacesCache;

	public FaceAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
		systemExampleFacesCache = new HashMap<ClientSystem, Mat>();
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse(com.gpigc.core.ClientSystem)
	 */
	@Override
	public DataEvent analyse(ClientSystem system) {

		if(system.getParameters().containsKey(Parameter.EXAMPLE_FACES) &&
				system.getParameters().containsKey(Parameter.FACE_SIMILARITY_THRESHOLD)){
			double threshold = Double.parseDouble(
					system.getParameters()
					.get(Parameter.FACE_SIMILARITY_THRESHOLD));
			Mat exampleFaces = getExampleFaces(system);
			// Get data from sensor
			List<SensorState> values;
			try {
				values = getSensorData(system);
				for(SensorState sensorState: values){
					// TODO Actually test to see if face seen is allowed
					//if (isAuthorisedFace(testFace, exampleFaces, threshold)) {
					//	return generateSuccessEvent(system);
					//} else {
						return generateFailureEvent(system);
					//}
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

	/**
	 * @return The example faces for the given system, using the cache where 
	 * possible
	 */
	private Mat getExampleFaces(ClientSystem system) {
		Mat exampleFaces;
		if (systemExampleFacesCache.keySet().contains(system)) {
			exampleFaces = systemExampleFacesCache.get(system);
		} else {
			String base64faceData = system.getParameters().get(
					Parameter.EXAMPLE_FACES);
			exampleFaces = parseFaces(base64faceData);
		}
		return exampleFaces;
	}

	private Mat parseFaces(String base64) {
		// TODO Parse example faces matrix from base64
		return null;
	}
	
	private boolean isAuthorisedFace(Mat testFace, Mat exampleFaces, boolean threshold) {
		// TODO Check to see if close enough to an allowable example face using 
		//Chi-Squared Imgproc.compareHist()
		return false;
	}
	
	private DataEvent generateSuccessEvent(ClientSystem system) {
		Map<String,String> data = new HashMap<>();
		data.put("Message", "Face recognitition in system " 
				+ system.getID() + " detected an authorised person and is "
				+ "allowing them access.");
		data.put("Subject", this.name+ " Notification");
		data.put("Recipient", "gpigc.alerts@gmail.com");
		return new DataEvent(data, system);
	}

	private DataEvent generateFailureEvent(ClientSystem system) {
		Map<String,String> data = new HashMap<>();
		data.put("Message", "Face recognitition in system " 
				+ system.getID() + " detected an unauthorised person and is "
				+ "denying them access.");
		data.put("Subject", this.name+ " Notification");
		data.put("Recipient", "gpigc.alerts@gmail.com");
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
