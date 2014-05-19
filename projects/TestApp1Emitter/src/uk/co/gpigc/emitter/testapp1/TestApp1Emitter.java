package uk.co.gpigc.emitter.testapp1;

import uk.co.gpigc.emitter.SimpleEmitter;

public class TestApp1Emitter {
	private static final int COLLECTION_INTERVAL = 5000;

	public static void main(String[] args) throws Exception {	
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + getExpandedFilePath("binlib"));
		
		OpenJarThread bThread = new OpenJarThread(getExpandedFilePath("res/" + TestApp1Collector.TEST_APP_NAME));
		bThread.start();
		
		SimpleEmitter emitter = new SimpleEmitter(TestApp1Emitter.class.getSimpleName(), new TestApp1Collector(), COLLECTION_INTERVAL);
		emitter.run();
		
		bThread.stopRunning();
	}
	
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}

}