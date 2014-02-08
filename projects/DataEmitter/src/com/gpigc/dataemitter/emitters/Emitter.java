package com.gpigc.dataemitter.emitters;

import java.util.concurrent.Callable;

import com.gpigc.dataemitter.comms.DataSender;
import com.gpigc.proto.Protos.SystemData;

public abstract class Emitter implements Callable<Void> {
	private boolean running = true;
	private int collectionInterval;
	protected static String CORE_HOST = "localhost";
	protected static int CORE_PORT = 8000;
	
	public Emitter(int collectionInterval) {
		this.collectionInterval = collectionInterval;
	}
	
	public Void call() throws Exception {
		DataSender sender = new DataSender(CORE_HOST, CORE_PORT);
		
		setup();
		
		while (continueRunning()) {
			SystemData data = collectData();

			if (data != null) {
				sender.send(data);
			}
			
			Thread.sleep(collectionInterval);
		}

		return null;
	}
	
	public void stop() {
		running = false;
	}
	
	private boolean continueRunning() {
		return running;
	}
	
	public abstract void setup() throws Exception;
	
	public abstract SystemData collectData() throws Exception;
}
