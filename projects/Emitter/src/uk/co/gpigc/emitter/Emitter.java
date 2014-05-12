package uk.co.gpigc.emitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.gpigc.proto.Protos.SystemData;

public class Emitter {
	private final int COLLECTION_INTERVAL = 1000;
	protected static String DEFAULT_CORE_HOST = "localhost";
	protected static int DEFAULT_CORE_PORT = 8000;
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final EmitterCallable emitterCallable = new EmitterCallable();
	private final List<DataCollector> collectors = new ArrayList<DataCollector>();
	private Future<Void> emitterResult;
	private String host;
	private int port;
	
	public Emitter() {
		this(DEFAULT_CORE_HOST, DEFAULT_CORE_PORT);
	}
	
	public Emitter(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void start() {
		if (!emitterCallable.isRunning()) {
			emitterResult = executor.submit(emitterCallable);
		}
	}
	
	public void stop() throws IOException {
		emitterCallable.stop();
	}
	
	public void waitFor() throws InterruptedException, ExecutionException {
		emitterResult.get();
	}
	
	public void registerDataCollector(DataCollector collector) {
		collectors.add(collector);
	}
	
	private class EmitterCallable implements Callable<Void> {
		private DataSender sender;
		private boolean running = false;

		@Override
		public Void call() throws Exception {
			sender = new DataSender(host, port);
			
			running = true;
			
			while (running) {
				for (DataCollector collector : collectors) {
					List<SystemData> dataList = collector.collect();

					if (dataList != null) {
						for (SystemData data : dataList) {
							sender.send(data);
						}
					}
				}
				
				Thread.sleep(COLLECTION_INTERVAL);
			}
			
			return null;
		}
		
		public void stop() throws IOException {
			sender.close();
			running = false;
		}
		
		public boolean isRunning() {
			return running;
		}
	}

}
