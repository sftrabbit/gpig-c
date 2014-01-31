package com.gpigc.core.analysis.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.analysis.Result;
import com.gpig.client.FailedToReadFromDatastoreException;
import com.gpig.client.SensorState;
import com.gpig.client.SystemDataGateway;

public class MeanAnalysis extends AnalysisEngine {

	private static final double LOWER_BOUND = 1.5;
	private static final double UPPER_BOUND = 50.0;
	
	private static final int TEN_RECORDS = 10;
	private static final String MEAN = "Mean";
	private static final String ERROR = "Error";
	private boolean error;

	public MeanAnalysis(SystemDataGateway database) {
		associatedSystems = new ArrayList<String>();
		associatedSystems.add("1");
		engineName = "MeanAnalyis1";
		this.database = database;
	}

	public Result analyse() {
		error = false;
		List<SensorState> sensorStates = getSensorStates();
		Double mean = computeMean(sensorStates);
		return computeResult(mean.toString());
	}
	
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

	private boolean meanIsAcceptable(String mean) {
		return Double.valueOf(mean) >= LOWER_BOUND && Double.valueOf(mean) <= UPPER_BOUND;
	}

	private double computeMean(List<SensorState> sensorStates) {
		double total = 0;
		for (SensorState sensorState : sensorStates) {
			total += Integer.parseInt(sensorState.getValue());
		}
		return total / (double) sensorStates.size();
	}

	private List<SensorState> getSensorStates() {
		List<SensorState> sensorStates = new ArrayList<SensorState>();
		for (String systemId : associatedSystems) {
			sensorStates.addAll(readSensorStateFromDatabase(systemId));
		}
		return sensorStates;
	}

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