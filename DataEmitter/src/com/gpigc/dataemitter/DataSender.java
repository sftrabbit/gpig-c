package com.gpigc.dataemitter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.gpigc.proto.Protos.SystemData;

/**
 * Sends messages containing system data via a socket. It is intended that this
 * is used to communicate with the GPIG-C data input interface.
 */
public class DataSender {
	protected final Socket socket;
	protected final OutputStream outputStream;

	/**
	 * Create a DataSender that will send messages via the given socket.
	 * 
	 * @param socket
	 *            Socket to send messages through.
	 * @throws IOException
	 */
	public DataSender(final Socket socket) throws IOException {
		this.socket = socket;
		this.outputStream = socket.getOutputStream();
	}

	/**
	 * Create a DataSender that will send messages to the given hostname and
	 * port.
	 * 
	 * @param host
	 *            Hostname to send messages to
	 * @param port
	 *            Port to send messages to
	 * @throws IOException
	 */
	public DataSender(String host, int port) throws IOException {
		this(new Socket(host, port));
	}

	/**
	 * Send a system data message.
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
