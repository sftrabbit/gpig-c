package com.gpigc.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.gpigc.dataabstractionlayer.client.GWTSystemDataGateway;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.analysis.ClientSystem;
import com.gpigc.core.datainput.DataInputServer;
import com.gpigc.core.notification.NotificationGenerator;

public class Core {
	public final static String APPENGINE_SERVLET_URI = "http://gpigc-beta.appspot.com/gpigc-webapp";
	
	public static void main(String args[]) throws IOException,
	IllegalArgumentException, SecurityException, ReflectiveOperationException {

		SystemDataGateway datastore = getDatastore(APPENGINE_SERVLET_URI);

		
		//TODO load this from a config file ---not hardcoded
		List<ClientSystem> systemsToMonitor = new ArrayList<>();
		systemsToMonitor.add(getTestAppSystem());
		systemsToMonitor.add(getDummyEarthApp());
		
		if(datastore != null){
			NotificationGenerator notificationGenerator = new NotificationGenerator(systemsToMonitor);
			//Create the other engines
			AnalysisController analysisController = new AnalysisController(
					datastore, notificationGenerator, systemsToMonitor);
			DataInputServer dis = new DataInputServer(analysisController, datastore);
			readData(dis);
		}else{
			//Abort
			System.exit(0);
		}
	}

	/**
	 * Get the ClientSystem for the first Test App
	 * @return
	 */
	private static ClientSystem getTestAppSystem() {
		List<String> sensorIDs = new ArrayList<>();
		sensorIDs.add("CPU");
		sensorIDs.add("Mem");
		Map<String,Map<String, Object>> parameters = new HashMap<>();
		Map<String,Object> lowerBounds = new HashMap<>();
		lowerBounds.put("CPU", new Integer(10));
		lowerBounds.put("Mem", new Long(50));
		parameters.put("LOWER_BOUND", lowerBounds);
		Map<String,Object> upperBounds = new HashMap<>();
		upperBounds.put("CPU", new Long(70));
		upperBounds.put("Mem", new Long(10000));
		parameters.put("UPPER_BOUND", upperBounds);
		return new ClientSystem("1", sensorIDs,parameters);
	}

	private static ClientSystem getDummyEarthApp() {
		List<String> sensorIDs = new ArrayList<>();
		sensorIDs.add("EQ");
		return new ClientSystem("2", sensorIDs,new HashMap<String,Map<String,Object>>());
	}

	

	/**
	 * Start receiving input from DataEmitters
	 * @param dis
	 * @throws IOException
	 */
	private static void readData(DataInputServer dis) throws IOException {
		dis.start();
		System.out.println("Press enter to stop the server.");
		System.in.read();
		dis.stopserver();
		System.out.println("Server stopped.");
	}


	/**
	 * Get a GWT datastore
	 * @param servletUri
	 * @return
	 */
	private static SystemDataGateway getDatastore(String servletUri) {
		try {
			return new GWTSystemDataGateway(new URI(servletUri));
		} catch (URISyntaxException e) {
			System.err.println("Could not initialise datastore: "
					+ e.getMessage());
			return null;
		}
	}
}
