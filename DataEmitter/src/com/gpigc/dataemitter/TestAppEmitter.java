package com.gpigc.dataemitter;

import com.gpigc.proto.Protos.SystemData;

public class TestAppEmitter {
	private static final String TEST_APP_NAME = "b.jar";
	
	public static void main(String[] args) throws Exception {
		JvmHook jvmHook = new JvmHook(TEST_APP_NAME);
		long pid = jvmHook.getPid();
		
		ProtoSender sender = new ProtoSender();

        SigarLoadMonitor slm = new SigarLoadMonitor(pid);
        Thread.sleep(4500);
        try {
	        while (true) {
	        	SystemData data = SystemData.newBuilder()
	        		.setSystemId("1")
	        		.setTimestamp(System.nanoTime())
	        		.addDatum(SystemData.Datum.newBuilder()
	        			.setKey("CPU")
	        			.setValue(String.valueOf(slm.getLoad())))
	        		.addDatum(SystemData.Datum.newBuilder()
	        			.setKey("Mem")
	        			.setValue(String.valueOf(jvmHook.getUsedMemory())))
	        		.build();
	        	
	        	sender.send(data);
	        	
	        	Thread.sleep(1000);
	        }
        } catch (Exception e) {
        	System.out.println("Server closed socket.");
        }
	}
}
