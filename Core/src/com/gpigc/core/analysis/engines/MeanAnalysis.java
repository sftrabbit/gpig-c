package com.gpigc.core.analysis.engines;

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

	private static final int TEN_RECORDS = 10;
	private String mapKey = "Mean";

	public MeanAnalysis(SystemDataGateway database) {
		associatedSystems = new ArrayList<String>();
		associatedSystems.add("1");
		this.database = database;
	}

	public Result analyse() {
		mapKey = "Mean";
		Map<String, Double> result = new HashMap<String, Double>();
		List<SensorState> sensorStates = getSensorStates();
		double total = 0;
		for (SensorState sensorState : sensorStates) {
			total += Integer.parseInt(sensorState.getValue());
		}
		result.put(mapKey, total / (double) sensorStates.size());
		return new Result(result, true);
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
			System.out.println("Failed to find any sensor data for the system with ID: " + systemId);
			e.printStackTrace();
			mapKey = "No sensor data found";
			return new ArrayList<SensorState>();
		}
	}

}
