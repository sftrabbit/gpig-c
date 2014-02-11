package com.gpigc.core.datainput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.proto.Protos;

public class DataInputServer extends Thread {
	protected boolean running = true;
	protected AnalysisController analysisController;
	protected SystemDataGateway database;

	public DataInputServer(AnalysisController analysisController,
			SystemDataGateway database) {
		this.analysisController = analysisController;
		this.database = database;
	}

	public void stopserver() {
		running = false;
	}

	public void run() {
		ProtoReceiver pr;

		try {
			pr = new ProtoReceiver();
		} catch (IOException e1) {
			return;
		}

		pr.start();
		ConcurrentLinkedQueue<Protos.SystemData> queue = pr.getQueue();

		while (running) {
			Protos.SystemData data = null;

			List<EmitterSystemState> systemStates = new ArrayList<EmitterSystemState>();
			Set<String> systemIds = new HashSet<String>();

			while ((data = queue.poll()) != null) {
				Map<String, String> datamap = new HashMap<String, String>();
				for (Protos.SystemData.Datum datum : data.getDatumList()) {
					datamap.put(datum.getKey(), datum.getValue());
				}

				systemStates.add(new EmitterSystemState(data.getSystemId(),
						new Date(data.getTimestamp()), datamap));
				
				systemIds.add(data.getSystemId());
			}

			if (!systemStates.isEmpty()) {
				try {
					database.write(systemStates);
					
					for (String systemId : systemIds) {
						analysisController.systemUpdate(systemId);
					}
				} catch (FailedToWriteToDatastoreException e) {
					System.out.println("Failed to write to database. Discarding data.");
					System.out.println(e);
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		try {
			pr.close();
		} catch (IOException e) {
		}
	}

}
