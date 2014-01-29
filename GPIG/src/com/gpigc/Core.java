package com.gpigc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gpigc.database.SystemData;
import com.gpigc.datainput.*;
import com.gpigc.proto.Protos;

public class Core {
	public static void main(String args[]) throws IOException, InterruptedException
	{
		ProtoReceiver pr = new ProtoReceiver();
		pr.start();
		ConcurrentLinkedQueue<Protos.SystemData> queue = pr.getQueue();
		
		while(true)
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
			Thread.sleep(100);
		}
	}
}
