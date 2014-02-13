package com.gpigc.core.analysis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.analysis.Result;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

/**
 * Mean analysis engine allowing for mean computation and bound checking on results
 * 
 * @author GPIGC
 */
public class MeanAnalysis extends AnalysisEngine {

	private static final double LOWER_BOUND = 0.0;
	private static final double UPPER_BOUND = 0.5;
	
	public static final int TEN_RECORDS = 10;
	private static final String MEAN = "Mean";

	private static final String SENSOR_ID = "CPU";

	/**
	 * Initialised the mean analysis engine
	 * 
	 * @param systemIDs The systems associated with this analysis engine
	 * @param database The database abstraction layer
	 */
	public MeanAnalysis(
			List<String> systemIDs, 
			SystemDataGateway database) {
		super("MeanAnalyis1", systemIDs, database);
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse()
	 */
	public Result analyse() {
		error = false;
		List<SensorState> sensorStates = getSensorStates(SENSOR_ID, TEN_RECORDS);
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
}