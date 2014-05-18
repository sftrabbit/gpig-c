package com.gpigc.core.view;

public class StandardMessageGenerator {

	public static void printException(Exception e) {
		print(e.getMessage());
	}

	public static void loadingConfigurationFile() {
		print("Loading configuration file:");
	}

	public static void registeredSystem(String systemID) {
		print("  Registered system: " + systemID);
	}

	public static void coreRunning() {
		print("Core Running");
	}

	public static void coreStopped() {
		print("Core Stopped");
	}

	public static void enableToSendNotification(String engineName,
			String systemID) {
		print("Enable to Send Notification From the " + engineName
				+ " for System " + systemID);
	}

	public static void eventGenerated(String engineName, String systemID) {
		print("Event Generated by " + engineName + " for System " + systemID);
	}

	public static void notificationGenerated(String engineName, String systemID) {
		print("Notification Sent by " + engineName + " for System " + systemID);
	}

	private static void print(String text) {
		System.out.println(' '+text);
	}

	public static void couldNotReadData() {
		print("Could Not Read Data From Datastore. Will Try on Next Update.");
	}

	public static void wrongParams(String systemID, String name) {
		print("System " + systemID + " Does Not Have The Correct Parameters."
				+ name + " Not Analysing.");
	}

	public static void couldNotParse(String exprStr) {
		print("Could not Parse " + exprStr + ", Ignoring Data");
	}

	public static void sensorValueMissing(String systemID, String sensorID) {
		print("Sensor Value: " + sensorID + " Missing For System " + systemID
				+ ": Can Not Analyse.");

	}

	public static void failedToWrite(String name, String id) {
		print("Failed to Write to Datastore: " + name
				+ ". Discarding Data From System: " + id);
	}

	public static void errorClosingProto() {
		print("An Error Occured Closing the ProtoReciever");

	}

	public static void phoneIPNotSpecified() {
		print("Phone IP Address Not Specified, Cannot Send Notification");
	}

	public static void couldNotUpdateStatus() {
		print("Could Not Update Status");
	}

	public static void dataRecieved() {
		print("New Data Recieved");
	}

	public static void couldNotReadConfig() {
		print("  Could Not Read Config File, No Systems Registered. Check the JSON");
	}

	public static void failedToSetup() {
		print("Failed to Set Up Controllers");
	}

	public static void failedToStart() {
		print("Failed to Start Data Input Server");
	}

	public static void couldNotReadSystemInConfig() {
		print("Could not read system from config file, skipping");
	}

	public static void failedToFindEngines(String absolutePath, String type) {
		print("Folder " + absolutePath + " does not " + "exist, so no " + type
				+ " engines could be loaded.");
	}
}
