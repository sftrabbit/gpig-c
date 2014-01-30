package com.gpigc.dataemitter;

import java.util.Timer;
import java.util.TimerTask;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Monitors a process's resource usage, such as CPU load and heap memory usage.
 */
class ProcessMonitor {
	private static final int TOTAL_TIME_UPDATE_LIMIT = 2000;
	private static final int UPDATE_INTERVAL = 1000;

	private final Sigar sigar;
	private final long processId;
	private ProcCpu previousCpuInfo;
	private double cpuLoad;

	/**
	 * Monitor the process with the given process ID.
	 * 
	 * @param processId
	 *            Process ID of the process to monitor
	 * @throws SigarException
	 *             Unable to retrieve CPU information
	 */
	public ProcessMonitor(long processId) throws SigarException {
		sigar = new Sigar();
		this.processId = processId;

		previousCpuInfo = sigar.getProcCpu(processId);
		cpuLoad = 0;

		new Timer(true).schedule(new CpuLoadTask(), 0, UPDATE_INTERVAL);
	}

	/**
	 * Gets the CPU load of the process as a percentage of total CPU time.
	 * 
	 * @return CPU load of the process
	 */
	public double getCpuLoad() {
		return cpuLoad;
	}

	/**
	 * A task that computes the process's CPU load over an interval of time.
	 */
	private class CpuLoadTask extends TimerTask {
		@Override
		public void run() throws RuntimeException {
			try {
				ProcCpu currentCpuInfo = sigar.getProcCpu(processId);
				long totalDelta = currentCpuInfo.getTotal()
						- previousCpuInfo.getTotal();
				long timeDelta = currentCpuInfo.getLastTime()
						- previousCpuInfo.getLastTime();
				if (totalDelta == 0) {
					if (timeDelta > TOTAL_TIME_UPDATE_LIMIT) {
						cpuLoad = 0;
					}
					if (cpuLoad == 0) {
						previousCpuInfo = currentCpuInfo;
					}
				} else {
					cpuLoad = 100. * totalDelta / timeDelta;
					previousCpuInfo = currentCpuInfo;
				}
			} catch (SigarException ex) {
				throw new RuntimeException(ex);
			}
		}
	};
}