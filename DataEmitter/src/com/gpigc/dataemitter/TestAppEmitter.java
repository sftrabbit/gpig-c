package com.gpigc.dataemitter;

import com.gpigc.proto.Protos.SystemData;

public class TestAppEmitter {
	private static final String TEST_APP_NAME = "b.jar";

	public static void main(String[] args) throws Exception {
		JavaVirtualMachine jvm = new JavaVirtualMachine(TEST_APP_NAME);
		long pid = jvm.getProcessId();

		SigarLoadMonitor slm = new SigarLoadMonitor(pid);
		Thread.sleep(4500);

		ProtoSender sender = new ProtoSender();
		try {
			while (true) {
				SystemData.Datum cpuDatum = SystemData.Datum.newBuilder()
						.setKey("CPU").setValue(String.valueOf(slm.getLoad()))
						.build();
				SystemData.Datum memoryDatum = SystemData.Datum.newBuilder()
						.setKey("Mem")
						.setValue(String.valueOf(jvm.getUsedMemory()))
						.build();
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
