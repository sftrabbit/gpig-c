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

		SystemData.Datum earthquakeDatum = SystemData.Datum.newBuilder()
				.setKey("EQ")
				.setValue("1.39,36.123333,-118.041832")
				.build();
		SystemData data = SystemData.newBuilder().setSystemId("2")
				.setTimestamp(System.currentTimeMillis())
				.addDatum(earthquakeDatum)
				.build();
		dataList.add(data);


		return dataList;
	}
}
