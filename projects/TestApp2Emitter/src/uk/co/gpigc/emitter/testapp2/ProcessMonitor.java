package uk.co.gpigc.emitter.testapp2;

import java.util.Timer;
import java.util.TimerTask;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcExe;
import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

/**
 * Monitors a process's resource usage, such as CPU load and heap memory usage.
 */
public class ProcessMonitor {
	private static final int TOTAL_TIME_UPDATE_LIMIT = 2000;
	private static final int UPDATE_INTERVAL = 1000;

	private final Sigar sigar;
	private final long processId;
	private ProcCpu previousCpuInfo;
	private ProcMem previousMemInfo;
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
			previousMemInfo = sigar.getProcMem(processId);
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
	 * Gets the memory usage of the process
	 * @return Memory usage of the process
	 * @throws ProcessMonitorException 
	 */
	public long getMemUsage() {
		return previousMemInfo.getSize();
	}
	
	/**
	 * Get the swap usage of the process
	 * @return Swap usage of the process
	 */
	public String getProcState() {
		try {
			ProcState currentProcState = sigar.getProcState(this.processId);
			return currentProcState.getName();
		} catch(SigarException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Get the working directory of the process
	 * @return Working directory of the process
	 */
	public String getProcWorkDir() {
		try {
			ProcExe currentExeState = sigar.getProcExe(this.processId);
			return currentExeState.getCwd();
		} catch(SigarException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Get the number of threads of the process
	 * @return Number of current thread
	 */
	public long getNumThreads() {
		try {
			return sigar.getProcState(this.processId).getThreads();
		} catch(SigarException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Get the last processor it ran on
	 * @return Number of current thread
	 */
	public long getProcessorID() {
		try {
			return sigar.getProcState(this.processId).getProcessor();
		} catch(SigarException ex) {
			throw new RuntimeException(ex);
		}
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
	public static class ProcessMonitorException extends Exception {
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