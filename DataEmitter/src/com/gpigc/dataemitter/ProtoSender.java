package com.gpigc.dataemitter;

import java.io.*;
import java.net.*;
import com.google.protobuf.*;

public class ProtoSender<T extends GeneratedMessage> {
	/* Hostname and port number for the default server.
	 * Should these be parameters?
	 * To test this, use `netcat -l GPIG_PORT` */
	protected static String GPIG_SERVER = "localhost";
	protected static int GPIG_PORT = 8000;

	protected Socket sock;
	protected OutputStream os;
	
	/* Construction */
	public ProtoSender(Socket sock) throws IOException
	{
		this.sock = sock;
		this.os = sock.getOutputStream();
	}
	
	public ProtoSender(String hostname, int port) throws IOException
	{
		this(new Socket(hostname, port));
	}

	public ProtoSender() throws IOException
	{
		this(GPIG_SERVER, GPIG_PORT);
	}
	
	/* Send the message through the socket */
	public void send(T msg) throws IOException
	{
		msg.writeDelimitedTo(os);
		os.flush();
	}
	
	/* Close the connection - call before destruction */
	public void close() throws IOException
	{
		os.close();
		sock.close();
	}
}
