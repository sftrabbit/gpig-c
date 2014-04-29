package com.gpigc.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.gpigc.dataabstractionlayer.client.GWTSystemDataGateway;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.datainput.DataInputServer;
import com.gpigc.core.notification.NotificationGenerator;

public class Core {
	public final static String APPENGINE_SERVLET_URI = "http://gpigc-webapp.appspot.com/gpigc-webapp";
	private static final String CONFIG_FILE_PATH = "config/RegisteredSystems.config";

	/* Use this one if other instance is kaput */
	//  public final static String APPENGINE_SERVLET_URI = "http://gpigc-beta.appspot.com/gpigc-webapp";


	public static void main(String args[]) throws IOException,
	IllegalArgumentException, SecurityException, ReflectiveOperationException {

		SystemDataGateway datastore = getDatastore(APPENGINE_SERVLET_URI);

		List<ClientSystem> systemsToMonitor = getSystems();
		System.out.println(systemsToMonitor);
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

	private static List<ClientSystem> getSystems() throws IOException {
		ConfigParser parser = new ConfigParser();
		return parser.parse(new File(CONFIG_FILE_PATH));
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
