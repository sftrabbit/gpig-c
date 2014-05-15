package uk.co.gpigc.emitter.testapp1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gpigc.proto.Protos.SystemData;

import uk.co.gpigc.emitter.DataCollector;
import uk.co.gpigc.emitter.testapp1.JavaVirtualMachineMonitor.MonitorJvmException;
import uk.co.gpigc.emitter.testapp1.JavaVirtualMachineMonitor.ServerFetchException;
import uk.co.gpigc.emitter.testapp1.ProcessMonitor.ProcessMonitorException;

public class TestApp1Collector implements DataCollector {
	public static final String TEST_APP_NAME = "b.jar";
	private static final int DELAY_BEFORE_MONITOR = 4500;
	
	private JavaVirtualMachineMonitor jvmMonitor;
	private ProcessMonitor processMonitor;
	
	public TestApp1Collector() throws MonitorJvmException, ProcessMonitorException, InterruptedException {
		jvmMonitor = new JavaVirtualMachineMonitor(
				TEST_APP_NAME);
		long pid = jvmMonitor.getProcessId();
		processMonitor = new ProcessMonitor(pid);
		Thread.sleep(DELAY_BEFORE_MONITOR);
	}

	@Override
	public List<SystemData> collect() throws ServerFetchException {
		SystemData.Datum cpuDatum = SystemData.Datum.newBuilder()
				.setKey("CPU")
				.setValue(String.valueOf(processMonitor.getCpuLoad()))
				.build();
		SystemData.Datum memoryDatum = SystemData.Datum.newBuilder()
				.setKey("Mem")
				.setValue(String.valueOf(jvmMonitor.getUsedMemory()))
				.build();
		SystemData data = SystemData.newBuilder().setSystemId("TestAppOneMonitor")
				.setTimestamp(new Date().getTime()).addDatum(cpuDatum)
				.addDatum(memoryDatum).build();
		ArrayList<SystemData> dataList = new ArrayList<SystemData>();
		dataList.add(data);
		System.out.println("Writing Data");
		return dataList;
	}

}
