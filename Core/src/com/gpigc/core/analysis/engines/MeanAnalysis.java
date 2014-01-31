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
	
	//public MeanAnalysis() {
	//	associatedSystems = new ArrayList<String>();
	//}
	
	public MeanAnalysis(SystemDataGateway database) {
		associatedSystems = new ArrayList<String>();
		
		associatedSystems.add("1");
		
		this.database = database;
	}

	public Result analyse() {
		Map<String, Double> result = new HashMap<String, Double>();

		try {
			List<SensorState> sensorDatas = getSensorData();
			double total = 0;
		
			for(SensorState ss : sensorDatas) {
				total += Integer.parseInt(ss.getValue());
			}
		
			result.put("Test", total / (double) sensorDatas.size());
		} catch (FailedToReadFromDatastoreException e) {
			result.put("Fail", 0.0);
		}
		
		return new Result(result, true);
	}
	
	private List<SensorState> getSensorData() throws FailedToReadFromDatastoreException {
		List<SensorState> systemData = new ArrayList<SensorState>();
		for(String systemId : associatedSystems) {
			systemData.addAll(database.readMostRecent(systemId, TEN_RECORDS).getRecords());
		}
		return systemData;
	}

}
