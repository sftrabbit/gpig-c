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

public class EmitterService {
	public static final String DEFAULT_CORE_HOST = "localhost";
	public static final int DEFAULT_CORE_PORT = 8000;
	public static final int DEFAULT_COLLECTION_INTERVAL = 1000;
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final EmitterCallable emitterCallable = new EmitterCallable();
	private final List<DataCollector> collectors = new ArrayList<DataCollector>();
	private Future<Void> emitterResult;
	private String host;
	private int port;
	private int collectionInterval;
	
	public EmitterService() {
		this(DEFAULT_CORE_HOST, DEFAULT_CORE_PORT, DEFAULT_COLLECTION_INTERVAL);
	}
	
	public EmitterService(int collectionInterval) {
		this(DEFAULT_CORE_HOST, DEFAULT_CORE_PORT, collectionInterval);
	}
	
	public EmitterService(String host, int port) {
		this(host, port, DEFAULT_COLLECTION_INTERVAL);
	}
	
	public EmitterService(String host, int port, int collectionInterval) {
		this.host = host;
		this.port = port;
		this.collectionInterval = collectionInterval;
	}
	
	public void start() {
		if (!emitterCallable.isRunning()) {
			emitterResult = executor.submit(emitterCallable);
		}
	}
	
	public void stop() throws IOException, InterruptedException, ExecutionException {
		emitterCallable.stop();
		this.waitFor();
	}
	
	public void waitFor() throws InterruptedException, ExecutionException {
		emitterResult.get();
		executor.shutdown();
	}
	
	public void registerDataCollector(DataCollector collector) {
		collectors.add(collector);
	}
	
	private class EmitterCallable implements Callable<Void> {
		private DataSender sender;
		private boolean running = false;

		@Override
		public Void call() throws Exception {
			try {
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
					
					Thread.sleep(collectionInterval);
				}
			} catch (Exception exception) {
				stop();
			}
			
			return null;
		}
		
		public void stop() throws IOException {
			if (sender != null) {
				sender.close();
			}
			running = false;
		}
		
		public boolean isRunning() {
			return running;
		}
	}

}
