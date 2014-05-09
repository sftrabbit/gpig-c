package com.gpigc.core.datainput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gpigc.core.Core;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.proto.Protos;

public class DataInputServer extends Thread {
	protected boolean running = true;
	private ProtoReceiver pr;
	private ConcurrentLinkedQueue<Protos.SystemData> queue;
	private final Core core;

	public DataInputServer(Core core) {
		this.core = core;
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

			Map<String, List<EmitterSystemState>> systemStates = new HashMap<String,List<EmitterSystemState>>();

			while ((data = queue.poll()) != null) {
				Map<String, String> datamap = new HashMap<String, String>();
				for (Protos.SystemData.Datum datum : data.getDatumList()) {
					datamap.put(datum.getKey(), datum.getValue());
				}

				if(!systemStates.containsKey(data.getSystemId()))
					systemStates.put(data.getSystemId(),new ArrayList<EmitterSystemState>());

				systemStates.get(data.getSystemId()).add(new EmitterSystemState(data.getSystemId(),
						new Date(data.getTimestamp()), datamap));

			}

			if (!systemStates.isEmpty()) {
				core.updateDatastore(systemStates);
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
