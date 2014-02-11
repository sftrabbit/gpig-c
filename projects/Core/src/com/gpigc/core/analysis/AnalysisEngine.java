package com.gpigc.core.analysis;

import java.util.ArrayList;
import java.util.List;

import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

/**
 * Provides the basic functionality and abstract methods that all analysis engines require
 * 
 * @author GPIGC
 */
public abstract class AnalysisEngine {
	
	protected String engineName;
	
	protected List<String> associatedSystems;
	
	protected SystemDataGateway database;
	
	protected boolean error;
	
	protected static final String ERROR = "Error";

	
	/**
	 * @param engineName The name of the analysis engine
	 * @param associatedSystems A list of systems ID associated with this 
	 * instance of the analysis engine
	 * @param database The database to collect data from and send results to
	 */
	public AnalysisEngine(
			String engineName, 
			List<String> associatedSystems, 
			SystemDataGateway database) {
				this.engineName = engineName;
				this.associatedSystems = associatedSystems;
				this.database = database;
	}
 	
	/**
	 * @return A list of systems ID associated with this instance of the analysis engine
	 */
	public List<String> getAssociatedSystems() {
		return associatedSystems;
	}
	
	/**
	 * @return The name of the analysis engine
	 */
	public String getEngineName() {
		return engineName;
	}
	
	/**
	 * Performs analysis upon the given data
	 * 
	 * @return The result of analysis in a result object
	 */
	public abstract Result analyse();

	/**
	 * Gets sensor states for system sensors associated with the analysis engine
	 * 
	 * @param numValues The number of values to return for each sensor
	 * @return	A list of sensor states for all systems associated with the analysis engine
	 */
	protected List<SensorState> getSensorStates(String sensorId, int numValues) {
		List<SensorState> sensorStates = new ArrayList<SensorState>();
		for (String systemId : associatedSystems) {
			sensorStates.addAll(readSensorStateFromDatabase(systemId, sensorId, numValues));
		}
		return sensorStates;
	}

	/**
	 * Returns the 10 most recent records from the database for a given system
	 * 
	 * @param systemId	The ID of the system to return records for
	 * @param numValues The number of values to return for each sensor
	 * @return			A list of the 10 most recent sensors states
	 */
	private List<SensorState> readSensorStateFromDatabase(String systemId, String sensorId, int numValues) {
		try {
			return database.readMostRecent(systemId, sensorId, numValues).getRecords();
		} catch (FailedToReadFromDatastoreException e) {
			error = true;
			System.out.println("Failed to find any sensor data for the system with ID: " + systemId);
			e.printStackTrace();
			return new ArrayList<SensorState>();
		}
	}
}