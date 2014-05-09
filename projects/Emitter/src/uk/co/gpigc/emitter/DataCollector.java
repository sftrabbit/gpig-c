package uk.co.gpigc.emitter;

import java.util.List;

import com.gpigc.proto.Protos.SystemData;

public interface DataCollector {
	
	public List<SystemData> collect();

}
