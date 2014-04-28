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
import com.gpigc.core.datainput.DataInputServer;
import com.gpigc.core.notification.NotificationGenerator;

public class Core {
	public final static String APPENGINE_SERVLET_URI = "http://gpigc-webapp.appspot.com/gpigc-webapp";
	
	/* Use this one if other instance is kaput */
	//  public final static String APPENGINE_SERVLET_URI = "http://gpigc-beta.appspot.com/gpigc-webapp";
	 

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
		List<ClientSensor> sensors = new ArrayList<>();
		
		//CPU Sensor
		Map<SensorParameter, Object> CPUParams = new HashMap<>();
		CPUParams.put(SensorParameter.LOWER_BOUND, new Integer(10));
		CPUParams.put(SensorParameter.UPPER_BOUND, new Integer(70));
		sensors.add(new ClientSensor("CPU", CPUParams));
		
		//Memory Sensor
		Map<SensorParameter, Object> memParams = new HashMap<>();
		memParams.put(SensorParameter.LOWER_BOUND, new Integer(100));
		memParams.put(SensorParameter.UPPER_BOUND, new Integer(7000));
		sensors.add(new ClientSensor("CPU", memParams));
		return new ClientSystem("1", sensors);
	}

	private static ClientSystem getDummyEarthApp() {
		List<ClientSensor> sensors = new ArrayList<>();
		//EQ Sensor
		sensors.add(new ClientSensor("EQ", new HashMap<SensorParameter,Object>()));
		return new ClientSystem("2", sensors);
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
