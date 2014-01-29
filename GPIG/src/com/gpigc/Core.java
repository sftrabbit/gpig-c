package com.gpigc;

import java.io.IOException;
import com.gpigc.datainput.DataInputServer;

public class Core {
	public static void main(String args[]) throws IOException, InterruptedException
	{
		DataInputServer dis = new DataInputServer();
		
		// Start your threads
		dis.start();
		
		System.out.println("Press enter to stop the server.");
		System.in.read();
		
		// Stop (order doesn't really matter)
		dis.stopserver();
		
		System.out.println("Server stopped.");
	}
}
