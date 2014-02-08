package com.gpigc.dataemitter.emitters;

import java.sql.Date;
import java.util.ArrayList;
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
	public List<SystemData> collectData() throws Exception {
		ArrayList<SystemData> dataList = new ArrayList<SystemData>();
		
		List<Earthquake> earthquakes = earthquakeMonitor.getNewEarthquakes();
		if (earthquakes.size() > 0) {
			for (Earthquake earthquake : earthquakes) {
				SystemData.Datum earthquakeDatum = SystemData.Datum.newBuilder()
						.setKey("EQ")
						.setValue(earthquake.toCsv())
						.build();
				SystemData data = SystemData.newBuilder().setSystemId("2")
						.setTimestamp(earthquake.getTime())
						.addDatum(earthquakeDatum)
						.build();
				dataList.add(data);
			}
		}
		
		return dataList;
	}
}
