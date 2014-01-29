package com.gpigc.datainput;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gpigc.database.SystemData;
import com.gpigc.proto.Protos;

public class DataInputServer extends Thread {
	protected boolean running = true;
	
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
				SystemData sd = new SystemData(data.getSystemId(), data.getTimestamp(), datamap);
				// database.add(sd) <-- uncomment when we have a database.
				System.out.println(sd);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
		pr.stop();
	}

}
