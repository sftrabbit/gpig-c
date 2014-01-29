package com.gpigc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.gpigc.analysis.AnalysisController;
import com.gpigc.datainput.DataInputServer;

public class Core {
	public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		AnalysisController analysisController = new AnalysisController();
		
		DataInputServer dis = new DataInputServer(analysisController);
		
		// Start your threads
		dis.start();
		
		System.out.println("Press enter to stop the server.");
		System.in.read();
		
		// Stop (order doesn't really matter)
		dis.stopserver();
		
		System.out.println("Server stopped.");
	}
}
