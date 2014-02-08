package com.gpigc.dataemitter.emitters;

import com.gpigc.dataemitter.monitors.EarthquakeMonitor;
import com.gpigc.proto.Protos.SystemData;

public class EarthEmitter extends Emitter {
	protected static final int COLLECTION_INTERVAL = 60000;

	private EarthquakeMonitor earthquakeMonitor;

	public EarthEmitter() {
		super(COLLECTION_INTERVAL);
	}

	@Override
	public void setup() throws Exception {
		earthquakeMonitor = new EarthquakeMonitor();
	}

	@Override
	public SystemData collectData() throws Exception {
		return null;
	}
}
