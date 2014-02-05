package com.gpigc.dataemitter.monitors;

import java.util.Timer;
import java.util.TimerTask;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.gpigc.dataemitter.monitors.JavaVirtualMachineMonitor.MonitorJvmException;

/**
 * Monitors a process's resource usage, such as CPU load and heap memory usage.
 */
public class ProcessMonitor {
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
	 * @throws ProcessMonitorException
	 *             Unable to retrieve process information
	 */
	public ProcessMonitor(long processId) throws ProcessMonitorException {
		sigar = new Sigar();
		this.processId = processId;
		cpuLoad = 0;

		try {
			previousCpuInfo = sigar.getProcCpu(processId);
		} catch (SigarException e) {
			throw new ProcessMonitorException(
					"Unable to retrieve process information", e);
		}

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

	/**
	 * Thrown if unable to retrieve information about a process.
	 */
	public static class ProcessMonitorException extends MonitorJvmException {
		private static final long serialVersionUID = 1L;

		public ProcessMonitorException() {
		}

		public ProcessMonitorException(String message) {
			super(message);
		}

		public ProcessMonitorException(String message, Exception cause) {
			super(message, cause);
		}
	}
}