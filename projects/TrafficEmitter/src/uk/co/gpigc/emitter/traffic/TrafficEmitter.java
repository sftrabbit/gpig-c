package uk.co.gpigc.emitter.traffic;

import uk.co.gpigc.emitter.SimpleEmitter;

public class TrafficEmitter {
	private static final int COLLECTION_INTERVAL = 60000;

	public static void main(String[] args) throws Exception {	
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + getExpandedFilePath("binlib"));
		
		SimpleEmitter emitter = new SimpleEmitter(TrafficEmitter.class.getSimpleName(), new TrafficCollector(), COLLECTION_INTERVAL);
		emitter.start();
	}
	
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}

}