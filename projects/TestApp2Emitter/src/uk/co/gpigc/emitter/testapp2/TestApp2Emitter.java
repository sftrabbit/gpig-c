package uk.co.gpigc.emitter.testapp2;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import uk.co.gpigc.emitter.Emitter;
import uk.co.gpigc.emitter.testapp2.ProcessMonitor.ProcessMonitorException;

public class TestApp2Emitter {
	private static final int COLLECTION_INTERVAL = 1000;
	private static final Emitter emitter = new Emitter(COLLECTION_INTERVAL);

	public static void main(String[] args) throws ProcessMonitorException, InterruptedException, IOException {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + getExpandedFilePath("binlib"));
		
		emitter.registerDataCollector(new TestApp2Collector());
		emitter.start();
		
		System.out.println("Emitter started");
		
		try {
			emitter.waitFor();
		} catch (InterruptedException e) {
			System.err.println("Emitter was interrupted.");
		} catch (ExecutionException e) {
			System.err.println("The emitter threw an exception: " + e.getCause().getMessage());
		}
		
		System.out.println("Emitter stopped");
	}
	
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}
	
	private static class ShutdownHook extends Thread {
		@Override
		public void run() {
			try {
				emitter.stop();
			} catch (IOException | InterruptedException | ExecutionException e) {
				System.err.println("Could not stop emitter successfully.");
			}
		}
	}

}
