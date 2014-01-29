package com.gpigc.dataemitter.testapp1;

import org.hyperic.sigar.*;

public class TestApp1 {
	public static void main(String[] args) throws SigarException
	{
		Sigar sigar = new Sigar();
        ProcState procState = sigar.getProcState("$$");

        System.out.println(procState.getName() + ": " +
                           getStateString(procState.getState()));

        sigar.close();

	}
	
    private static String getStateString(char state) {
        switch (state) {
          case ProcState.SLEEP:
            return "Sleeping";
          case ProcState.RUN:
            return "Running";
          case ProcState.STOP:
            return "Suspended";
          case ProcState.ZOMBIE:
            return "Zombie";
          case ProcState.IDLE:
            return "Idle";
          default:
            return String.valueOf(state);
        }
    }

}
