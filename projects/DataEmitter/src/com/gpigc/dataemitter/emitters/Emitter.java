package com.gpigc.dataemitter.emitters;

import java.util.concurrent.Callable;

import com.gpigc.dataemitter.comms.DataSender;
import com.gpigc.proto.Protos.SystemData;

public abstract class Emitter implements Callable<Void> {
	private boolean running = true;
	protected static String CORE_HOST = "localhost";
	protected static int CORE_PORT = 8000;
	protected static final int MONITOR_INTERVAL = 1000;
	
	public abstract Void setup() throws Exception;
	
	public abstract SystemData collectData() throws Exception;
	
	public Void call() throws Exception {
		DataSender sender = new DataSender(CORE_HOST, CORE_PORT);
		
		setup();
		
		while (continueRunning()) {
			SystemData data = collectData();

			sender.send(data);
			
			Thread.sleep(MONITOR_INTERVAL);
		}

		return null;
	}
	
	public void stop() {
		running = false;
	}
	
	private boolean continueRunning() {
		return running;
	}
}
