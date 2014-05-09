package com.gpigc.core.datainput;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.gpigc.proto.Protos.*;

public class ProtoReceiver extends Thread {
	/* Port number for the default server.
	 * Should this be a parameter? */
	protected static int GPIG_PORT = 8000;

	protected ServerSocket sock;
	protected ConcurrentLinkedQueue<SystemData> queue;
	
	public ProtoReceiver(ServerSocket sock) throws IOException{
    	super("ProtoReceiver");
		this.sock = sock;
		this.queue = new ConcurrentLinkedQueue<SystemData>();
	}
	
	public ProtoReceiver(int port) throws IOException{
		this(new ServerSocket(port));
	}

	public ProtoReceiver() throws IOException{
		this(GPIG_PORT);
	}
	
	public void run(){
		try
		{
			while(true)
			{
				new ProtoMultiReceiver(sock.accept(), queue).start();
            }
        } catch (IOException e) {
            System.err.println("ProtoReceiver stopped");
            return;
        }
	}
	
	public ConcurrentLinkedQueue<SystemData> getQueue(){
		return queue;
	}
	
	public void close() throws IOException{
		sock.close();
	}
}
