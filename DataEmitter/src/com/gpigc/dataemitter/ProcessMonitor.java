package com.gpigc.dataemitter;

import java.util.*;

import org.hyperic.sigar.*;

class ProcessMonitor {

    private static final int TOTAL_TIME_UPDATE_LIMIT = 2000;

    private final Sigar sigar;
    private final long pid;
    private ProcCpu prevPc;
    private double load;

    private TimerTask updateLoadTask = new TimerTask() {
        @Override public void run() {
            try {
                ProcCpu curPc = sigar.getProcCpu(pid);
                long totalDelta = curPc.getTotal() - prevPc.getTotal();
                long timeDelta = curPc.getLastTime() - prevPc.getLastTime();
                if (totalDelta == 0) {
                    if (timeDelta > TOTAL_TIME_UPDATE_LIMIT) load = 0;
                    if (load == 0) prevPc = curPc;
                } else {
                    load = 100. * totalDelta / timeDelta;
                    prevPc = curPc;
                }
            } catch (SigarException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    public ProcessMonitor(long d) throws SigarException {
        sigar = new Sigar();
        this.pid = d;
        prevPc = sigar.getProcCpu(d);
        load = 0;
        new Timer(true).schedule(updateLoadTask, 0, 1000);
    }

    public double getLoad() {
        return load;
    }
}