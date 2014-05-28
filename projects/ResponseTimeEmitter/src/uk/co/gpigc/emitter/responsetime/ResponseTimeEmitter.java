package uk.co.gpigc.emitter.responsetime;

import uk.co.gpigc.emitter.SimpleEmitter;

public class ResponseTimeEmitter {
	private static final int COLLECTION_INTERVAL = 5000;

	public static void main(String[] args) throws Exception {	
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + getExpandedFilePath("binlib"));
		
		SimpleEmitter emitter = new SimpleEmitter(ResponseTimeEmitter.class.getSimpleName(), new ResponseTimeCollector(), COLLECTION_INTERVAL);
		emitter.run();
	}
	
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}

}