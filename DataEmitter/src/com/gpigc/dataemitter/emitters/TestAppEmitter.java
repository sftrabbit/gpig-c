package com.gpigc.dataemitter.emitters;

import java.io.IOException;

import com.gpigc.dataemitter.DataSender;
import com.gpigc.dataemitter.monitors.JavaVirtualMachineMonitor;
import com.gpigc.dataemitter.monitors.JavaVirtualMachineMonitor.MonitorJvmException;
import com.gpigc.dataemitter.monitors.JavaVirtualMachineMonitor.ServerFetchException;
import com.gpigc.dataemitter.monitors.ProcessMonitor;
import com.gpigc.proto.Protos.SystemData;

/**
 * Monitors the test application provided by the customer and emits appropriate
 * data to the GPIG-C system to be stored and analysed.
 * 
 * The test application must be running when this emitter application is
 * launched. It locates the JVM process running b.jar, connects to it and
 * monitors information such as its CPU and memory usage.
 */
public class TestAppEmitter {
	private static final String TEST_APP_NAME = "b.jar";
	protected static String CORE_HOST = "localhost";
	protected static int CORE_PORT = 8000;

	protected static int FAIL_SEND_LIMIT = 5;
	protected static int EXIT_SERVER_ERROR = 1;
	protected static int EXIT_JVM_ERROR = 2;

	public static void main(String[] args) {

		DataSender sender = null;
		try {
			sender = new DataSender(CORE_HOST, CORE_PORT);
		} catch (IOException e) {
			System.err.println("Could not connect to server. Terminating...");
			System.exit(EXIT_SERVER_ERROR);
		}

		JavaVirtualMachineMonitor jvmMonitor = null;
		ProcessMonitor processMonitor = null;
		try {
			jvmMonitor = new JavaVirtualMachineMonitor(TEST_APP_NAME);
			processMonitor = new ProcessMonitor(jvmMonitor.getProcessId());
		} catch (MonitorJvmException e) {
			System.err.println("Could not find JVM for test program. Terminating...");
			System.exit(EXIT_JVM_ERROR);
		}

		try {
			Thread.sleep(4500);
		} catch (InterruptedException e1) {
			System.err.println(
					"Interrupted whilst waiting for CPU load measurements to stabilise.\n" +
					"First few reported values will be inaccurate.");
		}

		// When this reaches the limit, give up and terminate
		int failcount = 0;
		while (true) {
			try {
				SystemData.Datum cpuDatum = SystemData.Datum.newBuilder()
					.setKey("CPU")
					.setValue(String.valueOf(processMonitor.getCpuLoad()))
					.build();
				SystemData.Datum memoryDatum = SystemData.Datum.newBuilder()
					.setKey("Mem")
					.setValue(String.valueOf(jvmMonitor.getUsedMemory()))
					.build();
				SystemData data = SystemData.newBuilder().setSystemId("1")
					.setTimestamp(System.nanoTime()).addDatum(cpuDatum)
					.addDatum(memoryDatum).build();

				sender.send(data);
				failcount = 0;
			} catch (IOException e) {
				System.err.println("Could not send data to server.");
				failcount ++;
			} catch (ServerFetchException e) {
				System.err.println("Could not get memory usage from JVM. Terminating...");
				System.exit(EXIT_JVM_ERROR);
			}

			if(failcount == FAIL_SEND_LIMIT) {
				System.err.println("Failed to send too much data. Terminating...");
				System.exit(EXIT_SERVER_ERROR);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
