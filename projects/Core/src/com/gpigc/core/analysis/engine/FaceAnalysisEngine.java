/**
 * 
 */
package com.gpigc.core.analysis.engine;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.FileUtils;
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
	
	/* Caches the example faces associated with each system to avoid frequently 
	 * re-parsing them
	 */
	private Map<ClientSystem, List<FaceExample>> systemExampleFacesCache;

	static { loadNativeLibs(); }

	public FaceAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
		systemExampleFacesCache = new HashMap<ClientSystem, List<FaceExample>>();
	}

	@Override
	public DataEvent analyse(ClientSystem system) {
		if (areParametersSet(system)) {
			double threshold;
			try {
				threshold = Double.parseDouble(system.getParameters().get(
						Parameter.FACE_SIMILARITY_THRESHOLD));
			} catch (NumberFormatException e) {
				System.out.println(" System \""+system.getID()+"\" does not have "
						+ "a valid threshold");
				System.err.println("System \""+system.getID()+"\" does not have "
						+ "a valid threshold: "+system.getParameters().get(
								Parameter.FACE_SIMILARITY_THRESHOLD));
				return null;
			}
			List<FaceExample> exampleFaces;
			try {
				exampleFaces = getExampleFaces(system);
			} catch (ParseException e) {
				System.out.println(" Failed to parse example faces. "
						+ "Error at character "+e.getErrorOffset()+". "
						+ "Check your example data .csv file.");
				System.err.println("Failed to parse example faces. "
						+ "Error at character "+e.getErrorOffset()+": "+
						e.getMessage());
				return null;
			}
			// Get data from sensor
			generateAppropriateEvent(system, threshold, exampleFaces);
		} else {
			StandardMessageGenerator.wrongParams(system.getID(), name);
		}
		return null; // No event
	}

	private DataEvent generateAppropriateEvent(ClientSystem system,
			double threshold, List<FaceExample> exampleFaces) {
		List<SensorState> values;
		try {
			values = getSensorData(system);
			for (SensorState sensorState : values) {
				System.err.println("Data creation time: "+
						sensorState.getCreationTimestamp());
				String faceMatrixString = sensorState.getValue();
				Mat faceMatrix = parseFace(faceMatrixString);
				// Actually test to see if face seen is allowed
				if (getMostLikelyClass(faceMatrix, exampleFaces, threshold)
						.equals(FaceClass.ROSY)) {
					System.out.println(" Authorised face detected");
					return generateSuccessEvent(system);
				} else {
					System.out.println(" No authorised face detected");
					return generateFailureEvent(system);
				}
			}
		} catch (FailedToReadFromDatastoreException e) {
			e.printStackTrace();
			StandardMessageGenerator.couldNotReadData();
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println(" Failed to parse face detected by sensor. Error at "
					+ "character "+e.getErrorOffset());
		}
		return null;
	}

	/**
	 * @return Whether the parameters this engine needs are set
	 */
	private boolean areParametersSet(ClientSystem system) {
		return system.getParameters().containsKey(Parameter.EXAMPLE_FACES)
				&& system.getParameters().containsKey(
						Parameter.FACE_SIMILARITY_THRESHOLD);
	}

	/**
	 * @return The example faces for the given system, using the cache where
	 *         possible
	 * @throws ParseException 
	 */
	private List<FaceExample> getExampleFaces(ClientSystem system) 
			throws ParseException {
		List<FaceExample> exampleFaces = new ArrayList<FaceExample>();
		if (systemExampleFacesCache.keySet().contains(system)) {
			exampleFaces = systemExampleFacesCache.get(system);
		} else {
			File faceDataFileName = this.core.getConfig().getConfigFile(system.getParameters().get(Parameter.EXAMPLE_FACES));
			try {
				String faceData = FileUtils.readString(faceDataFileName.toString());
				exampleFaces = parseExampleFaces(faceData);
				systemExampleFacesCache.put(system, exampleFaces);
			} catch (IOException e) {
				System.err.println("Failed to load face data from " + 
						faceDataFileName);
				throw new ParseException("IO error: "+e.getMessage(), -1);
			}
		}
		return exampleFaces;
	}

	/**
	 * public static for testing
	 * 
	 * @param facesMatrixStr
	 *            String encoding the example faces
	 * @return The example faces
	 * @throws ParseException 
	 * 				If the String given cannot be parsed as example faces
	 */
	public List<FaceExample> parseExampleFaces(String facesMatrixStr) 
			throws ParseException {
		final String LINE_SEP = "\n";
		String[] faceStrings = facesMatrixStr.split(LINE_SEP);
		System.out.println(" "+faceStrings.length+" example faces loaded.");
		List<FaceExample> faces = new ArrayList<>(faceStrings.length);
		for (String faceStr : faceStrings) {
			faces.add(parseFaceExample(faceStr));
		}
		return faces;
	}

	private FaceExample parseFaceExample(String faceStr) 
			throws ParseException {
		final String ELEMENT_SEP = ",";
		// Parse face matrix
		String[] elements = faceStr.split(ELEMENT_SEP);
		int offset = 0;
		Mat faceMatrix = new Mat(new Size(elements.length-1, 1), CvType.CV_32FC1);
		for (int i = 1; i < elements.length; i++) {
			double elementValue;
			try {
				elementValue = Double.parseDouble(elements[i]);
			} catch (NumberFormatException e) {
				throw new ParseException("Failed to parse example face on double: "+elements[i], offset);
			}
			faceMatrix.put(0, i-1, elementValue);
			offset += elements[i].length()+ELEMENT_SEP.length();
		}
		FaceClass exampleClass;
		try {
			exampleClass = FaceClass.valueOf(elements[0]);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ParseException("Failed to parse example face on class: "+elements[0], offset);
		}
		return new FaceExample(faceMatrix, exampleClass);
	}

	/**
	 * public static for testing
	 * 
	 * @param faceMatrixStr
	 *            String encoding a face
	 * @return The face as a matrix
	 * @throws ParseException 
	 */
	public static Mat parseFace(String faceMatrixStr) throws ParseException {
		String SPLIT_ON = ",";
		// Parse face matrix
		String[] elements = faceMatrixStr.split(SPLIT_ON);
		int offset = 0;
		Mat faceMatrix = new Mat(new Size(elements.length, 1), CvType.CV_32FC1);
		for (int i = 0; i < elements.length; i++) {
			double elementValue;
			try {
				elementValue = Double.parseDouble(elements[i]);
			} catch (NumberFormatException e) {
				throw new ParseException(faceMatrixStr, offset);
			}
			faceMatrix.put(0, i, elementValue);
			offset += elements[i].length()+SPLIT_ON.length();
		}
		return faceMatrix;
	}

	/**
	 * public static for testing
	 * 
	 * @param testFace
	 *            The face that we wish to test the authorisation of
	 * @param exampleFaces
	 *            The example allowable faces
	 * @param threshold
	 *            How similar the testFace must be to one of the exampleFaces
	 * @return The most likely class that testFace belongs to
	 */
	public static FaceClass getMostLikelyClass(Mat testFace,
			List<FaceExample> exampleFaces, double threshold) {
		System.err.println(" >>>>> Checking face authorisation at the "+
				threshold+" threshold");
		/*
		 * Check to see if close enough to an allowable example face using
		 * Chi-Squared method
		 */
		FaceClass bestExampleClass = FaceClass.NOT_ROSY;
		System.err.println("Assuming class "+bestExampleClass);
		double minDifference = threshold;
		for (FaceExample example : exampleFaces) {
			System.err.println("Example matrix dims = "+
					example.getLbp().width()+"x"+
					example.getLbp().height());
			System.err.println("Test matrix dims = "+
					testFace.width()+"x"+
					testFace.height());
			double faceDifference = Imgproc.compareHist(
					testFace, 
					example.getLbp(),
					Imgproc.CV_COMP_CHISQR);
			if (faceDifference < threshold && faceDifference < minDifference) {
				bestExampleClass = example.getFaceClass();
				minDifference = faceDifference;
				System.err.println("Found closer example image, of class "+
						example.getFaceClass() +
						" and difference "+faceDifference);
			} else {
				if (faceDifference < threshold) {
					System.err.println("Found example image but failed threshold"
							+ " test. Example is of class "+
							example.getFaceClass() +
							" and difference "+faceDifference);
				} else {
					System.err.println("Found worse example image, of class "+
							example.getFaceClass() +
							" and difference "+faceDifference);
				}
			}
		}
		return bestExampleClass;
	}

	private DataEvent generateSuccessEvent(ClientSystem system) {
		Map<Parameter, String> data = new HashMap<>();
		data.put(Parameter.MESSAGE,
				"Face recognitition in system " + system.getID()
				+ " detected an authorised person and is "
				+ "allowing them access.");
		data.put(Parameter.SUBJECT, this.name + " Notification");
		data.put(Parameter.RECIPIENT,
				system.getParameters().get(Parameter.RECIPIENT));
		return new DataEvent(data, system);
	}

	private DataEvent generateFailureEvent(ClientSystem system) {
		Map<Parameter, String> data = new HashMap<>();
		data.put(Parameter.MESSAGE,
				"Face recognitition in system " + system.getID()
				+ " detected an unauthorised person and is "
				+ "denying them access.");
		data.put(Parameter.SUBJECT, this.name + " Notification");
		data.put(Parameter.RECIPIENT,
				system.getParameters().get(Parameter.RECIPIENT));
		return new DataEvent(data, system);
	}

	private List<SensorState> getSensorData(ClientSystem system)
			throws FailedToReadFromDatastoreException {
		List<SensorState> values = new ArrayList<>();
		for (ClientSensor sensor : system.getSensors()) {
			SensorState state = getSensorReadings(system, sensor.getID(), 1)
					.get(0);
			if (state != null) {
				values.add(state);
			} else {
				StandardMessageGenerator.sensorValueMissing(system.getID(),
						sensor.getID());
				return null;
			}
		}
		return values;
	}

	private static void loadNativeLibs() {
		try {
			System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Failed to load OpenCV natives");
		}
	}

	/**
	 * Example LBP-class pairs
	 */
	class FaceExample {
		private final Mat lbp;
		private final FaceClass faceClass;

		public FaceExample(Mat lbp, FaceClass faceClass) {
			this.lbp = lbp;
			this.faceClass = faceClass;
		}

		public Mat getLbp() {
			return lbp;
		}

		public FaceClass getFaceClass() {
			return faceClass;
		}
	}

	public enum FaceClass { ROSY, NOT_ROSY }

}
