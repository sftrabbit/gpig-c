package com.gpigc.core.datainput;

import java.net.*;
import java.util.concurrent.*;
import java.io.*;

import com.gpigc.proto.Protos.*;

public class ProtoMultiReceiver extends Thread {
	private Socket sock;
	private ConcurrentLinkedQueue<SystemData> queue;
	 
    public ProtoMultiReceiver(Socket sock, ConcurrentLinkedQueue<SystemData> queue)
    {
    	super("ProtoMultiReceiver");
        this.sock = sock;
        this.queue = queue;
    }
     
    public void run()
    {
    	while(true)
    	{
    		try
    		{
    			SystemData data = SystemData.parseDelimitedFrom(sock.getInputStream());
    			if(data == null)
    				break;
    			queue.add(data);
    		}
    		catch (IOException e)
    		{
    			System.err.println("Error parsing Proto.");
    		}
    	}
    }
}
