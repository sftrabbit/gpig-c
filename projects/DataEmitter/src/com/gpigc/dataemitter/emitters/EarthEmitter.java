package com.gpigc.dataemitter.emitters;

import java.util.List;

import com.gpigc.dataemitter.monitors.EarthquakeMonitor;
import com.gpigc.dataemitter.monitors.EarthquakeMonitor.Earthquake;
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
		SystemData.Builder dataBuilder = SystemData.newBuilder().setSystemId("2")
				.setTimestamp(System.nanoTime());
		
		List<Earthquake> earthquakes = earthquakeMonitor.getNewEarthquakes();
		for (Earthquake earthquake : earthquakes) {
			SystemData.Datum earthquakeDatum = SystemData.Datum.newBuilder()
					.setKey("EQ")
					.setValue(earthquake.toString())
					.build();
			dataBuilder.addDatum(earthquakeDatum);
		}
		
		return dataBuilder.build();
	}
}
