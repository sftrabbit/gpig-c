package com.gpigc;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gpigc.datainput.*;
import com.gpigc.proto.Protos.SystemData;

public class Core {
	public static void main(String args[]) throws IOException, InterruptedException
	{
		ProtoReceiver pr = new ProtoReceiver();
		pr.start();
		ConcurrentLinkedQueue<SystemData> queue = pr.getQueue();
		
		while(true)
		{
			SystemData data = null;
			while((data = queue.poll()) != null)
			{
				System.out.println(data);
			}
			Thread.sleep(5000);
		}
	}
}
