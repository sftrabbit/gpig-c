package com.gpigc.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.datainput.DataInputServer;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationController;
import com.gpigc.core.storage.StorageController;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;

public class Core {

	private final DataInputServer dataInputServer;
	private final StorageController datastoreController;
	private final AnalysisController analysisController;
	private final NotificationController notificationGenerator;
	private List<ClientSystem> systemsToMonitor;
	private String currentConfigFilePath;

	public Core(String configFilePath) throws IOException, ReflectiveOperationException, InterruptedException {
		currentConfigFilePath = configFilePath;
		systemsToMonitor = getSystems(currentConfigFilePath);
		datastoreController = new StorageController(systemsToMonitor);
		analysisController =  new AnalysisController(systemsToMonitor, this);
		notificationGenerator = new NotificationController(systemsToMonitor);
		dataInputServer = new DataInputServer(this);

		FileMonitor monitor = FileMonitor.getInstance();
		ConfigFileChangeListener listener = new ConfigFileChangeListener();
		monitor.addFileChangeListener(listener, currentConfigFilePath, 1000);
	}


	public void refreshSystems() throws IOException, ReflectiveOperationException {
		System.out.println("Re-registering systems...");
		systemsToMonitor = getSystems(currentConfigFilePath);
		datastoreController.refreshSystems(systemsToMonitor);
		analysisController.refreshSystems(systemsToMonitor);
		notificationGenerator.refreshSystems(systemsToMonitor);
	}
	
	public void updateDatastore(Map<String, List<EmitterSystemState>> systemStates) {
		getDatastoreController().push(systemStates);
		StandardMessageGenerator.dataRecieved();
		getAnalysisController().analyse(systemStates.keySet());
	}
	
	public void generateNotification(DataEvent event) {
		getNotificationGenerator().generate(event);
	}

	
	private List<ClientSystem> getSystems(String path) throws IOException {
		ConfigParser parser = new ConfigParser();
		return parser.parse(new File(path));
	}


	public DataInputServer getDataInputServer() {
		return dataInputServer;
	}


	public StorageController getDatastoreController() {
		return datastoreController;
	}


	public AnalysisController getAnalysisController() {
		return analysisController;
	}


	public NotificationController getNotificationGenerator() {
		return notificationGenerator;
	}


	public List<ClientSystem> getSystemsToMonitor() {
		return systemsToMonitor;
	}


	public void setSystemsToMonitor(List<ClientSystem> systemsToMonitor) {
		this.systemsToMonitor = systemsToMonitor;
	}


	private class ConfigFileChangeListener implements FileChangeListener {
		public void fileChanged(File file) {
			System.out.println("Config file modified");
			try {
				refreshSystems();
			} catch (IOException | ReflectiveOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
