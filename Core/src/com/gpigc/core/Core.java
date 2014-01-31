package com.gpigc.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import com.gpig.client.GWTSystemDataGateway;
import com.gpig.client.SystemDataGateway;
import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.datainput.DataInputServer;

public class Core {
	protected final static String DB_SERVLET_URI = "http://localhost/";
	public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		SystemDataGateway database = null;
		try {
			database = new GWTSystemDataGateway(new URI(DB_SERVLET_URI));
		} catch (URISyntaxException e) {
			System.err.println("Could not initialise database: " + e.getMessage());
			System.exit(1);
		}
		
		AnalysisController analysisController = new AnalysisController(database);
		
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
