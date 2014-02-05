package com.gpigc.core.analysis.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.analysis.Result;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

/**
 * Mean analysis engine allowing for mean computation and bound checking on results
 * 
 * @author GPIGC
 */
public class MeanAnalysis extends AnalysisEngine {

	private static final double LOWER_BOUND = 1.5;
	private static final double UPPER_BOUND = 50.0;
	
	private static final int TEN_RECORDS = 10;
	private static final String MEAN = "Mean";
	private static final String ERROR = "Error";
	private boolean error;

	/**
	 * Initialised the mean analysis engine
	 * 
	 * @param database	The database abstraction layer
	 */
	public MeanAnalysis(SystemDataGateway database) {
		associatedSystems = new ArrayList<String>();
		associatedSystems.add("1");
		engineName = "MeanAnalyis1";
		this.database = database;
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse()
	 */
	public Result analyse() {
		error = false;
		List<SensorState> sensorStates = getSensorStates();
		Double mean = computeMean(sensorStates);
		return computeResult(mean.toString());
	}
	
	/**
	 * Method for constructing the result object depending on the analysis outcome.
	 * 
	 * @param mean	Calculated mean value from given data
	 * @return		A populated result object
	 */
	private Result computeResult(String mean) {
		Map<String, String> payload = new HashMap<String, String>();
		if(error) {
			payload.put(ERROR, mean);
			return new Result(payload, true);
		}
		payload.put(MEAN, mean);
		if(meanIsAcceptable(mean)) {
			return new Result(payload, false);
		}
		return new Result(payload, true);	
	}

	/**
	 * Performs bounds checking of calculated mean
	 * 
	 * @param mean	Calculated mean value
	 * @return		True if mean falls within bounds, false otherwise
	 */
	private boolean meanIsAcceptable(String mean) {
		return Double.valueOf(mean) >= LOWER_BOUND && Double.valueOf(mean) <= UPPER_BOUND;
	}

	/**
	 * @param sensorStates	A list of sensor states
	 * @return				The mean of all given sensor state values
	 */
	private double computeMean(List<SensorState> sensorStates) {
		double total = 0;
		for (SensorState sensorState : sensorStates) {
			total += Double.parseDouble(sensorState.getValue());
		}
		return total / (double) sensorStates.size();
	}

	/**
	 * Gets sensor states for system sensors associated with the mean analysis engine
	 * 
	 * @return	A list of sensor states for all systems associated with the mean analysis engine
	 */
	private List<SensorState> getSensorStates() {
		List<SensorState> sensorStates = new ArrayList<SensorState>();
		for (String systemId : associatedSystems) {
			sensorStates.addAll(readSensorStateFromDatabase(systemId));
		}
		return sensorStates;
	}

	/**
	 * Returns the 10 most recent records from the database for a given system
	 * 
	 * @param systemId	The ID of the system to return records for
	 * @return			A list of the 10 most recent sensors states
	 */
	private List<SensorState> readSensorStateFromDatabase(String systemId) {
		try {
			return database.readMostRecent(systemId, TEN_RECORDS).getRecords();
		} catch (FailedToReadFromDatastoreException e) {
			error = true;
			System.out.println("Failed to find any sensor data for the system with ID: " + systemId);
			e.printStackTrace();
			return new ArrayList<SensorState>();
		}
	}
}