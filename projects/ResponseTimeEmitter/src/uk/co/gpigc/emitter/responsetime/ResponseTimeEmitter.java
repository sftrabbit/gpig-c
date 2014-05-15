package uk.co.gpigc.emitter.responsetime;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import uk.co.gpigc.emitter.Emitter;

public class ResponseTimeEmitter {
	private static final int COLLECTION_INTERVAL = 120000;
	private static final Emitter emitter = new Emitter(COLLECTION_INTERVAL);

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		emitter.registerDataCollector(new ResponseTimeCollector());
		emitter.start();
		
		System.out.println("Response Emitter started");
		
		try {
			emitter.waitFor();
		} catch (InterruptedException e) {
			System.err.println("Response Emitter was interrupted.");
		} catch (ExecutionException e) {
			System.err.println("The Response Emitter threw an exception: " + e.getCause().getMessage());
		}
		
		System.out.println("Response Emitter stopped");
	}
	
	private static class ShutdownHook extends Thread {
		@Override
		public void run() {
			try {
				emitter.stop();
			} catch (IOException e) {
				System.err.println("Could not stop Response Emitter successfully.");
			}
		}
	}

}
