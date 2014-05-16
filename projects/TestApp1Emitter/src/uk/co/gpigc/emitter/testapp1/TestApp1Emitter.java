package uk.co.gpigc.emitter.testapp1;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import uk.co.gpigc.emitter.Emitter;
import uk.co.gpigc.emitter.testapp1.JavaVirtualMachineMonitor.MonitorJvmException;
import uk.co.gpigc.emitter.testapp1.ProcessMonitor.ProcessMonitorException;

public class TestApp1Emitter {
	private static final int COLLECTION_INTERVAL = 1000;
	private static final Emitter emitter = new Emitter(COLLECTION_INTERVAL);

	public static void main(String[] args) throws MonitorJvmException, ProcessMonitorException, InterruptedException {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + getExpandedFilePath("binlib"));
		
		OpenJarThread bThread = new OpenJarThread(getExpandedFilePath("res/" + TestApp1Collector.TEST_APP_NAME));
		bThread.start();
		emitter.registerDataCollector(new TestApp1Collector());
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
		bThread.stopRunning();
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
