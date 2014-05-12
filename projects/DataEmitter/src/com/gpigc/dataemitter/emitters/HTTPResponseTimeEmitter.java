package com.gpigc.dataemitter.emitters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gpigc.dataemitter.monitors.HTTPMonitor;
import com.gpigc.proto.Protos.SystemData;

public class HTTPResponseTimeEmitter extends Emitter {
	protected static final int COLLECTION_INTERVAL = 3600000;

	protected URL url;
	protected HTTPMonitor httpMonitor;

	public HTTPResponseTimeEmitter() {
		super(COLLECTION_INTERVAL);
		System.out.println("HTTPResponseTimeEmitter");
		
		try {
			url = new URL("https://www.thalesgroup.com/en");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setup() throws Exception {
		httpMonitor = new HTTPMonitor(url);
	}

	@Override
	public List<SystemData> collectData() throws Exception {
		SystemData data = SystemData.newBuilder()
				.setSystemId("HTTPResponseTime")
				.setTimestamp(new Date().getTime())
				.addDatum(SystemData.Datum.newBuilder()
					.setKey("ResponseTime")
					.setValue(String.valueOf(httpMonitor.getResponseTime()))
					.build())
				.build();

		ArrayList<SystemData> dataList = new ArrayList<SystemData>();
		dataList.add(data);
		return dataList;
	}
}
