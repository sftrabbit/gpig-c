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
import org.opencv.imgproc.Imgproc;

/**
 * Analyses the faces seen in an image detected by a sensor
 * 
 * Requires OpenCV
 * 
 * @author GPIG-C
 */
public class FaceAnalysisEngine extends AnalysisEngine {

	private Map<ClientSystem, List<Mat>> systemExampleFacesCache;
	
	static{
		try {
			System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Failed to load OpenCV natives");
		}
	}

	public FaceAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
		systemExampleFacesCache = new HashMap<ClientSystem, List<Mat>>();
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse(com.gpigc.core.ClientSystem)
	 */
	@Override
	public DataEvent analyse(ClientSystem system) {
		if(areParametersSet(system)){
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
					//System.err.println("Face matrix string: " + faceMatrixString);
					Mat faceMatrix = parseFace(faceMatrixString);
					//System.err.println("Parsed face: " + faceMatrix.dump());
					// Actually test to see if face seen is allowed
					if (isAuthorisedFace(faceMatrix, exampleFaces, threshold)) {
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
		return null; // No event
	}

	private boolean areParametersSet(ClientSystem system) {
		return system.getParameters().containsKey(Parameter.EXAMPLE_FACES) &&
		system.getParameters().containsKey(Parameter.FACE_SIMILARITY_THRESHOLD);
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
			//System.err.println("Example faces loaded: " + faceData);
			exampleFaces = parseFaces(faceData);
			systemExampleFacesCache.put(system, exampleFaces);
		}
		return exampleFaces;
	}

	/**
	 * public static for testing
	 * @param facesMatrixStr String encoding the example faces
	 * @return The faces as matrices
	 */
	public static List<Mat> parseFaces(String facesMatrixStr) {
		String[] faceStrings = facesMatrixStr.split("\n");
		List<Mat> faces = new ArrayList<>(faceStrings.length);
		for (String faceStr : faceStrings) {
			faces.add(parseFace(faceStr));
		}
		return faces;
	}
	
	/**
	 * public static for testing
	 * @param faceMatrixStr String encoding a face
	 * @return The face as a matrix
	 */
	public static Mat parseFace(String faceMatrixStr) {
		// Parse face matrix
		String[] elements = faceMatrixStr.split(",");
		Mat faceMatrix = new Mat(new Size(elements.length, 1), CvType.CV_32FC1);
		for (int i = 0; i < elements.length; i++) {
			double elementValue = Double.parseDouble(elements[i]);
			faceMatrix.put(0, i, elementValue);
		}
		return faceMatrix;
	}
	
	/**
	 * public static for testing
	 * @param testFace The face that we wish to test the authorisation of
	 * @param exampleFaces The example allowable faces
	 * @param threshold How similar the testFace must be to one of the 
	 * exampleFaces
	 * @return Whether the testFace is authorised
	 */
	public static boolean isAuthorisedFace(Mat testFace, List<Mat> exampleFaces, 
			double threshold) {
		/* Check to see if close enough to an allowable example face using 
		 * Chi-Squared method
		 */
		for (Mat example : exampleFaces) {
			double faceSimilarity = Imgproc.compareHist(
					testFace, example, Imgproc.CV_COMP_CHISQR);
			if (faceSimilarity < threshold) {
				return true;
			}
		}
		return false;
	}
	
	private DataEvent generateSuccessEvent(ClientSystem system) {
		Map<Parameter,String> data = new HashMap<>();
		data.put(Parameter.MESSAGE, "Face recognitition in system " 
				+ system.getID() + " detected an authorised person and is "
				+ "allowing them access.");
		data.put(Parameter.SUBJECT, this.name+ " Notification");
		data.put(Parameter.RECIPIENT, "gpigc.alerts@gmail.com");
		return new DataEvent(data, system);
	}

	private DataEvent generateFailureEvent(ClientSystem system) {
		Map<Parameter,String> data = new HashMap<>();
		data.put(Parameter.MESSAGE, "Face recognitition in system " 
				+ system.getID() + " detected an unauthorised person and is "
				+ "denying them access.");
		data.put(Parameter.SUBJECT, this.name+ " Notification");
		data.put(Parameter.RECIPIENT, "gpigc.alerts@gmail.com");
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
