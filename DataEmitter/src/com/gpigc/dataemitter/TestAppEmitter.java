package com.gpigc.dataemitter;

import java.io.IOException;

import com.gpigc.dataemitter.comms.DataSender;
import com.gpigc.dataemitter.monitors.JavaVirtualMachineMonitor;
import com.gpigc.dataemitter.monitors.ProcessMonitor;
import com.gpigc.dataemitter.monitors.JavaVirtualMachineMonitor.MonitorJvmException;
import com.gpigc.dataemitter.monitors.ProcessMonitor.ProcessMonitorException;
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
	protected static int DELAY_BEFORE_MONITOR = 4500;
	protected static final int MONITOR_INTERVAL = 1000;

	public static void main(String[] args) throws MonitorJvmException,
			ProcessMonitorException, InterruptedException, IOException {
		DataSender sender = new DataSender(CORE_HOST, CORE_PORT);

		JavaVirtualMachineMonitor jvmMonitor = new JavaVirtualMachineMonitor(
				TEST_APP_NAME);
		long pid = jvmMonitor.getProcessId();

		ProcessMonitor processMonitor = new ProcessMonitor(pid);
		Thread.sleep(DELAY_BEFORE_MONITOR);

		while (true) {
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

			Thread.sleep(MONITOR_INTERVAL);
		}
	}
}
