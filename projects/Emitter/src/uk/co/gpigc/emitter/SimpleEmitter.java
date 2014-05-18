package uk.co.gpigc.emitter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SimpleEmitter {
	private String name;
	private DataCollector collector;
	private EmitterService emitter;
	private boolean running = false;
	
	public SimpleEmitter(String name, DataCollector collector) {
		this(name, collector, EmitterService.DEFAULT_COLLECTION_INTERVAL);
	}
	
	public SimpleEmitter(String name, DataCollector collector, int collectionInterval) {
		this.name = name;
		this.collector = collector;
		this.emitter = new EmitterService(collectionInterval);
		
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	}

	public void run() {
		running = true;
		
		emitter.registerDataCollector(collector);
		emitter.start();
		System.out.println("-- " + name + " started --");
		
		try {
			emitter.waitFor();
		} catch (InterruptedException e) {
			System.out.println("Error: " + name + " was interrupted");
		} catch (ExecutionException e) {
			System.out.println("Error: " + e.getCause().getMessage());
		}
		
		stop();
	}
	
	private void stop() {
		if (running) {
			try {
				emitter.stop();
				System.out.println("-- " + name + " stopped --");
			} catch (IOException e) {
				System.out.println("Error: Could not stop " + name + " cleanly");
			}
			
			running = false;
		}
	}
	
	private class ShutdownHook extends Thread {
		@Override
		public void run() {
			SimpleEmitter.this.stop();
		}
	}
}
