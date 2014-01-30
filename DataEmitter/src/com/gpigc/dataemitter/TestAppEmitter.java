package com.gpigc.dataemitter;

import com.gpigc.proto.Protos.SystemData;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class TestAppEmitter {
	private static long getTestAppPid() {
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            if (descriptor.displayName().equals("b.jar")) {
            	return Long.parseLong(descriptor.id());
            }
        }
        
        return 0;
	}
	
	public static void main(String[] args) throws Exception
	{
        long pid = getTestAppPid();
        if (pid > 0) {
			Attach att = new Attach(pid);
			
			ProtoSender sender = new ProtoSender();
	
	        SigarLoadMonitor slm = new SigarLoadMonitor(pid);
	        Thread.sleep(4500);
	        try
	        {
		        while(true)
		        {
		        	SystemData data = SystemData.newBuilder()
		        		.setSystemId("1")
		        		.setTimestamp(System.nanoTime())
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
        } else {
	    	System.out.println("Test application not running");
	    }
	}
}
