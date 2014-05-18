package uk.co.gpigc.emitter;

import java.util.List;

import com.gpigc.proto.Protos.SystemData;

public abstract class DataCollector {
	
	public abstract List<SystemData> collect() throws Exception;
	
	public void shutdown() {}

}
