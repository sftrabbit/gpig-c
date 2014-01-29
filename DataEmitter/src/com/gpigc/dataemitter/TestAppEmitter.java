package com.gpigc.dataemitter;

public class TestAppEmitter {
	public static void main(String[] args) throws Exception
	{
		/*
		 * USAGE: test.jar PID PATH/TO/MANAGEMENT-AGENT.JAR
		 */
		long pid = Long.parseLong(args[0]);
		Attach att = new Attach(args[1], pid);

        SigarLoadMonitor slm = new SigarLoadMonitor(pid);
        Thread.sleep(4500);
        while(true)
        {
        	double percent = slm.getLoad();

        	System.out.println("CPU: " + percent + "\n" +
        			"Mem: " + att.getmem() + "\n");
        	Thread.sleep(1000);
        }

        //sigar.close();

	}
}
