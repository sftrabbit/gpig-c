package com.gpigc.core.datainput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gpigc.core.analysis.AnalysisController;
import com.gpig.client.EmitterSystemState;
import com.gpig.client.FailedToWriteToDatastoreException;
import com.gpig.client.SystemDataGateway;
import com.gpigc.proto.Protos;

public class DataInputServer extends Thread {
	protected boolean running = true;
	protected AnalysisController analysisController;
	protected SystemDataGateway database;
	
	public DataInputServer(AnalysisController analysisController,
							SystemDataGateway database)
	{
		this.analysisController = analysisController;
		this.database = database;
	}
	
	public void stopserver()
	{
		running = false;
	}
	
	public void run() 
	{
		ProtoReceiver pr;

		try {
			pr = new ProtoReceiver();
		} catch (IOException e1) {
			return;
		}
		
		pr.start();
		ConcurrentLinkedQueue<Protos.SystemData> queue = pr.getQueue();
		List<Protos.SystemData> failed = new ArrayList<Protos.SystemData>();
		
		while(running)
		{
			Protos.SystemData data = null;
			while((data = queue.poll()) != null)
			{
				Map<String, String> datamap = new HashMap<String, String>();
				for(Protos.SystemData.Datum datum : data.getDatumList())
				{
					datamap.put(datum.getKey(), datum.getValue());
				}

				try {
					database.write(new EmitterSystemState(
						data.getSystemId(),
						new Date(data.getTimestamp()),
						datamap));
				
					analysisController.systemUpdate(data.getSystemId());
				} catch (FailedToWriteToDatastoreException e) {
					// If we failed to write, add it to a queue to process in the next batch.
					failed.add(data);
				}
			}

			queue.addAll(failed);
			failed.clear();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
		
		pr.stop();
	}

}
