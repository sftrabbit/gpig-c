package uk.co.gpigc.androidapp.comms;

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
	 *            Socket to send messages through
	 * @throws IOException
	 *             Cannot create output stream or socket is not connected
	 */
	public DataSender(final Socket socket) throws IOException {
		this.socket = socket;
		this.outputStream = socket.getOutputStream();
	}

	/**
	 * Create a DataSender that will send messages to the given host and port.
	 * 
	 * @param host
	 *            Host to send messages to
	 * @param port
	 *            Port to send messages to
	 * @throws IOException
	 *             Cannot create output stream or socket failed to connect
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
	 *             Failed to write to the socket
	 */
	public void send(SystemData message) throws IOException {
		message.writeDelimitedTo(outputStream);
		outputStream.flush();
	}

	/**
	 * Close the socket.
	 * 
	 * @throws IOException
	 *             Unable to close socket.
	 */
	public void close() throws IOException {
		outputStream.close();
		socket.close();
	}
}
