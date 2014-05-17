package uk.co.gpigc.emitter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SimpleEmitter {
	private String name;
	private DataCollector collector;
	private EmitterService emitter;
	
	public SimpleEmitter(String name, DataCollector collector) {
		this(name, collector, EmitterService.DEFAULT_COLLECTION_INTERVAL);
	}
	
	public SimpleEmitter(String name, DataCollector collector, int collectionInterval) {
		this.name = name;
		this.collector = collector;
		this.emitter = new EmitterService(collectionInterval);
	}

	public void start() {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		emitter.registerDataCollector(collector);
		emitter.start();
		System.out.println(name + " started");
		
		try {
			emitter.waitFor();
			System.out.println(name + " stopped");
		} catch (InterruptedException e) {
			System.err.println(name + " was interrupted");
		} catch (ExecutionException e) {
			System.err.println("The emitter threw an exception: " + e.getCause().getMessage());
		}
	}
	
	private class ShutdownHook extends Thread {
		@Override
		public void run() {
			try {
				System.out.println("Shutting down " + name);
				emitter.stop();
			} catch (IOException | InterruptedException | ExecutionException e) {
				System.err.println("Could not stop " + name + " cleanly");
			}
		}
	}
}
