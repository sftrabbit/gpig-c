package com.gpigc.dataemitter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.gpigc.proto.Protos.SystemData;

/**
 * Sends Protocol Buffer messages via a socket.
 */
public class ProtoSender {
	protected final Socket sock;
	protected final OutputStream os;

	/**
	 * Send Protocol Buffer messages via the given socket.
	 * 
	 * @param sock
	 *            Socket to send messages through.
	 * @throws IOException
	 */
	public ProtoSender(final Socket sock) throws IOException {
		this.sock = sock;
		this.os = sock.getOutputStream();
	}

	/**
	 * Send Protocol Buffer messages to the given hostname and port.
	 * 
	 * @param hostname
	 *            Hostname to send messages to
	 * @param port
	 *            Port to send messages to
	 * @throws IOException
	 */
	public ProtoSender(String hostname, int port) throws IOException {
		this(new Socket(hostname, port));
	}

	/**
	 * Send a SystemData message.
	 * 
	 * @param msg
	 *            Message to send
	 * @throws IOException
	 */
	public void send(SystemData msg) throws IOException {
		msg.writeDelimitedTo(os);
		os.flush();
	}

	/**
	 * Close the socket.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		os.close();
		sock.close();
	}
}
