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

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 * Analyses the faces seen in an image detected by a sensor
 * 
 * Requires OpenCV
 * 
 * @author GPIG-C
 */
public class FaceAnalysisEngine extends AnalysisEngine {

	private Map<ClientSystem, List<Mat>> systemExampleFacesCache;

	public FaceAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
		systemExampleFacesCache = new HashMap<ClientSystem, List<Mat>>();
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
			List<Mat> exampleFaces = getExampleFaces(system);
			// Get data from sensor
			List<SensorState> values;
			try {
				values = getSensorData(system);
				for(SensorState sensorState: values){
					String faceMatrixString = sensorState.getValue();
					Mat faceMatrix = parseFace(faceMatrixString);
					System.err.println("Parsed face: " + faceMatrix.dump());
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
	private List<Mat> getExampleFaces(ClientSystem system) {
		List<Mat> exampleFaces;
		if (systemExampleFacesCache.keySet().contains(system)) {
			exampleFaces = systemExampleFacesCache.get(system);
		} else {
			String faceData = system.getParameters().get(
Parameter.EXAMPLE_FACES);
			exampleFaces = parseFaces(faceData);
		}
		return exampleFaces;
	}

	private List<Mat> parseFaces(String facesMatrixStr) {
		String[] faceStrings = facesMatrixStr.split("\n");
		List<Mat> faces = new ArrayList<>(faceStrings.length);
		for (String faceStr : faceStrings) {
			faces.add(parseFace(faceStr));
		}
		return faces;
	}
	
	private Mat parseFace(String faceMatrixStr) {
		// Parse face matrix
		String[] elements = faceMatrixStr.split(",");
		Mat faceMatrix = new Mat(new Size(elements.length, 1), CvType.CV_32SC1);
		for (int i = 0; i < elements.length; i++) {
			faceMatrix.put(0, i, new double[]{Double.parseDouble(elements[i])});
		}
		return faceMatrix;
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
