package uk.co.gpigc.emitter.testapp2;

import uk.co.gpigc.emitter.SimpleEmitter;

public class TestApp2Emitter {
	private static final int COLLECTION_INTERVAL = 30000;

	public static void main(String[] args) throws Exception {	
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + getExpandedFilePath("binlib"));
		
		SimpleEmitter emitter = new SimpleEmitter(TestApp2Emitter.class.getSimpleName(), new TestApp2Collector(), COLLECTION_INTERVAL);
		emitter.start();
	}
	
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}

}