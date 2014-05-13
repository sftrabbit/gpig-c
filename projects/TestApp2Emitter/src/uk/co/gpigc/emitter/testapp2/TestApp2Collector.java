package uk.co.gpigc.emitter.testapp2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.gpigc.emitter.DataCollector;
import uk.co.gpigc.emitter.testapp2.ProcessMonitor.ProcessMonitorException;

import com.gpigc.proto.Protos.SystemData;

public class TestApp2Collector implements DataCollector {
	private static final String PROCESS_NAME = "test2";
	private static final int DELAY_BEFORE_MONITOR = 4500;
	
	private ProcessMonitor processMonitor;
	
	public TestApp2Collector() throws ProcessMonitorException {
		try {
			/**
			 * This is horrible, and requires a Linux-like `pidof` command to work.
			 */
			ProcessBuilder pb = new ProcessBuilder("pidof", PROCESS_NAME);
			Process p = pb.start();  
			p.waitFor();
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			long pid = Long.parseLong(bri.readLine());
		
			processMonitor = new ProcessMonitor(pid);
			Thread.sleep(DELAY_BEFORE_MONITOR);
		} catch (Exception e) {
			throw new ProcessMonitorException(e.getMessage());
		}
	}

	@Override
	public List<SystemData> collect()  {
		SystemData.Datum cpuDatum = SystemData.Datum.newBuilder()
				.setKey("CPU")
				.setValue(String.valueOf(processMonitor.getCpuLoad()))
				.build();
		SystemData.Datum memoryDatum = SystemData.Datum.newBuilder()
				.setKey("Mem")
				.setValue(String.valueOf(processMonitor.getMemUsage()))
				.build();
		SystemData data = SystemData.newBuilder().setSystemId("testapp2")
				.setTimestamp(new Date().getTime())
					.addDatum(cpuDatum)
					.addDatum(memoryDatum)
				.build();
		ArrayList<SystemData> dataList = new ArrayList<SystemData>();
		dataList.add(data);
		return dataList;
	}

}
