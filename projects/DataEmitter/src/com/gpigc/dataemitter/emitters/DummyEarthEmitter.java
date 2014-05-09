package com.gpigc.dataemitter.emitters;

import java.util.ArrayList;
import java.util.List;

import com.gpigc.proto.Protos.SystemData;

public class DummyEarthEmitter extends Emitter {
	protected static final int COLLECTION_INTERVAL = 60000;


	public DummyEarthEmitter() {
		super(COLLECTION_INTERVAL);
		System.out.println("DummyEarthEmitter");
	}

	@Override
	public void setup() throws Exception {
	}

	@Override
	public List<SystemData> collectData() throws Exception {
		ArrayList<SystemData> dataList = new ArrayList<SystemData>();
		String val = (Math.random()*10+"").substring(0, 4);

		double lat = 20 + Math.random()*20;
		double longi = -100 - Math.random()*20;

		SystemData.Datum earthquakeDatum = SystemData.Datum.newBuilder()
				.setKey("EQ")
				.setValue(val+","+lat+","+ longi)
				.build();
		SystemData data = SystemData.newBuilder().setSystemId("2")
				.setTimestamp(System.currentTimeMillis())
				.addDatum(earthquakeDatum)
				.build();
		dataList.add(data);


		return dataList;
	}
}
