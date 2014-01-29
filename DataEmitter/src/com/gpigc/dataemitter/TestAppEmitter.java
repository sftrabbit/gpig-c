package com.gpigc.dataemitter;

import com.gpigc.proto.Protos.SystemData;

public class TestAppEmitter {
	public static void main(String[] args) throws Exception
	{
		/*
		 * USAGE: test.jar PID PATH/TO/MANAGEMENT-AGENT.JAR
		 */
		long pid = Long.parseLong(args[0]);
		Attach att = new Attach(args[1], pid);
		
		ProtoSender sender = new ProtoSender();

        SigarLoadMonitor slm = new SigarLoadMonitor(pid);
        Thread.sleep(4500);
        try
        {
	        while(true)
	        {
	        	SystemData data = SystemData.newBuilder()
	        		.setSystemId("1")
	        		.setTimestamp(0)
	        		.addDatum(SystemData.Datum.newBuilder()
	        			.setKey("CPU")
	        			.setValue(String.valueOf(slm.getLoad())))
	        		.addDatum(SystemData.Datum.newBuilder()
	        			.setKey("Mem")
	        			.setValue(String.valueOf(att.getmem())))
	        		.build();
	        	
	        	sender.send(data);
	        	
	        	Thread.sleep(1000);
	        }
        } catch (Exception e) {
        	System.out.println("Server closed socket.");
        }
	}
}
