package uk.co.gpigc.emitter.traffic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import uk.co.gpigc.emitter.Emitter;

public class TrafficEmitter {
	private static final int COLLECTION_INTERVAL = 60000;
	private static final Emitter emitter = new Emitter(COLLECTION_INTERVAL);

	public static void main(String[] args) throws MalformedURLException {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		emitter.registerDataCollector(new TrafficCollector());
		emitter.start();
		
		System.out.println("Traffic Emitter started");
		
		try {
			emitter.waitFor();
		} catch (InterruptedException e) {
			System.err.println("Traffic Emitter was interrupted.");
			e.printStackTrace();
		} catch (ExecutionException e) {
			System.err.println("The Traffic Emitter threw an exception: " + e.getCause().getMessage());
			e.printStackTrace();
		}
		
		System.out.println("Traffic Emitter stopped");
	}
	
	private static class ShutdownHook extends Thread {
		@Override
		public void run() {
			try {
				emitter.stop();
			} catch (IOException e) {
				System.err.println("Could not stop the Traffic Emitter successfully.");
			}
		}
	}

}