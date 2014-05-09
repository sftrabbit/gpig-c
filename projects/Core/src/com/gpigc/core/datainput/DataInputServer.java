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
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.proto.Protos;

public class DataInputServer extends Thread {
	protected boolean running = true;
	protected AnalysisController analysisController;
	protected SystemDataGateway database;
	private ProtoReceiver pr;
	private ConcurrentLinkedQueue<Protos.SystemData> queue;

	public DataInputServer(AnalysisController analysisController,
			SystemDataGateway database) {
		this.analysisController = analysisController;
		this.database = database;
	}

	public void stopserver() {
		running = false;
	}

	@Override
	public void run() {
		try {
			pr = new ProtoReceiver();
		} catch (IOException e1) {
			return;
		}

		pr.start();
		queue = pr.getQueue();
		
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
						StandardMessageGenerator.dataRecievedFrom(systemId);
						analysisController.systemUpdate(systemId);
					}
				} catch (FailedToWriteToDatastoreException e) {
					StandardMessageGenerator.failedToWrite();
					e.printStackTrace();
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
			StandardMessageGenerator.errorClosingProto();
		}

	}

}
