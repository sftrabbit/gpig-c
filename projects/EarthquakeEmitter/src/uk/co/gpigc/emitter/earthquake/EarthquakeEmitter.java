package uk.co.gpigc.emitter.earthquake;

import uk.co.gpigc.emitter.SimpleEmitter;

public class EarthquakeEmitter {
	private static final int COLLECTION_INTERVAL = 60000;

	public static void main(String[] args) throws Exception {	
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + getExpandedFilePath("binlib"));
		
		SimpleEmitter emitter = new SimpleEmitter(EarthquakeEmitter.class.getSimpleName(), new EarthquakeCollector(), COLLECTION_INTERVAL);
		emitter.run();
	}
	
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}

}