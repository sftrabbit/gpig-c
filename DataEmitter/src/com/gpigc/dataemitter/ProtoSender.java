package com.gpigc.dataemitter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.gpigc.proto.Protos.SystemData;

public class ProtoSender {
	protected final Socket sock;
	protected final OutputStream os;
	
	/* Construction */
	public ProtoSender(final Socket sock) throws IOException
	{
		this.sock = sock;
		this.os = sock.getOutputStream();
	}
	
	public ProtoSender(String hostname, int port) throws IOException
	{
		this(new Socket(hostname, port));
	}
	
	/* Send the message through the socket */
	public void send(SystemData msg) throws IOException
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
