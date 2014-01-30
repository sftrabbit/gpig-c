package com.gpigc.dataemitter;

import com.gpigc.proto.Protos.SystemData;

public class TestAppEmitter {
	private static final String TEST_APP_NAME = "b.jar";

	public static void main(String[] args) throws Exception {
		ProtoSender sender = new ProtoSender();
		
		JavaVirtualMachineMonitor jvmMonitor = new JavaVirtualMachineMonitor(
				TEST_APP_NAME);
		long pid = jvmMonitor.getProcessId();

		ProcessMonitor processMonitor = new ProcessMonitor(pid);
		Thread.sleep(4500);
		
		try {
			while (true) {
				SystemData.Datum cpuDatum = SystemData.Datum.newBuilder()
						.setKey("CPU").setValue(String.valueOf(processMonitor.getCpuLoad()))
						.build();
				SystemData.Datum memoryDatum = SystemData.Datum.newBuilder()
						.setKey("Mem")
						.setValue(String.valueOf(jvmMonitor.getUsedMemory())).build();
				SystemData data = SystemData.newBuilder().setSystemId("1")
						.setTimestamp(System.nanoTime()).addDatum(cpuDatum)
						.addDatum(memoryDatum).build();

				sender.send(data);

				Thread.sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("Server closed socket.");
		}
	}
}
