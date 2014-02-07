package com.gpigc.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import com.gpigc.dataabstractionlayer.client.GWTSystemDataGateway;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.datainput.DataInputServer;
import com.gpigc.core.notification.NotificationGenerator;

public class Core {
	protected final static String DB_SERVLET_URI = "http://gpigc-webapp.appspot.com/gpigc-webapp";

	public static void main(String args[]) throws IOException,
			InterruptedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		SystemDataGateway database = null;
		try {
			database = new GWTSystemDataGateway(new URI(DB_SERVLET_URI));
		} catch (URISyntaxException e) {
			System.err.println("Could not initialise database: "
					+ e.getMessage());
			System.exit(1);
		}

		NotificationGenerator notificationGenerator = new NotificationGenerator();
		AnalysisController analysisController = new AnalysisController(
				database, notificationGenerator);

		DataInputServer dis = new DataInputServer(analysisController, database);

		// Start your threads
		dis.start();

		System.out.println("Press enter to stop the server.");
		System.in.read();

		// Stop (order doesn't really matter)
		dis.stopserver();

		System.out.println("Server stopped.");
	}
}
