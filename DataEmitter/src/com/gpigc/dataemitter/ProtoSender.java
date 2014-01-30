package com.gpigc.dataemitter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.gpigc.proto.Protos.SystemData;

/**
 * Sends Protocol Buffer messages via a socket.
 */
public class ProtoSender {
	protected final Socket socket;
	protected final OutputStream outputStream;

	/**
	 * Send Protocol Buffer messages via the given socket.
	 * 
	 * @param socket
	 *            Socket to send messages through.
	 * @throws IOException
	 */
	public ProtoSender(final Socket socket) throws IOException {
		this.socket = socket;
		this.outputStream = socket.getOutputStream();
	}

	/**
	 * Send Protocol Buffer messages to the given hostname and port.
	 * 
	 * @param host
	 *            Hostname to send messages to
	 * @param port
	 *            Port to send messages to
	 * @throws IOException
	 */
	public ProtoSender(String host, int port) throws IOException {
		this(new Socket(host, port));
	}

	/**
	 * Send a SystemData message.
	 * 
	 * @param message
	 *            Message to send
	 * @throws IOException
	 */
	public void send(SystemData message) throws IOException {
		message.writeDelimitedTo(outputStream);
		outputStream.flush();
	}

	/**
	 * Close the socket.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		outputStream.close();
		socket.close();
	}
}
