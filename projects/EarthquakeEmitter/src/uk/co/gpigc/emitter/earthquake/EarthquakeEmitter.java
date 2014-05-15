package uk.co.gpigc.emitter.earthquake;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import uk.co.gpigc.emitter.Emitter;

public class EarthquakeEmitter {
	private static final int COLLECTION_INTERVAL = 60000;
	private static final Emitter emitter = new Emitter(COLLECTION_INTERVAL);

	public static void main(String[] args) throws MalformedURLException {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		emitter.registerDataCollector(new EarthquakeCollector());
		emitter.start();
		
		System.out.println("Earth Emitter started");
		
		try {
			emitter.waitFor();
		} catch (InterruptedException e) {
			System.err.println("Earth Emitter was interrupted.");
		} catch (ExecutionException e) {
			System.err.println("The Earth Emitter threw an exception: " + e.getCause().getMessage());
		}
		
		System.out.println("Earth Emitter stopped");
	}
	
	private static class ShutdownHook extends Thread {
		@Override
		public void run() {
			try {
				emitter.stop();
			} catch (IOException e) {
				System.err.println("Could not stop the Earth Emitter successfully.");
			}
		}
	}

}
