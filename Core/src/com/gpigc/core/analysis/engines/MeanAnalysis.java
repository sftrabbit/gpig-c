package com.gpigc.core.analysis.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.analysis.Result;
import com.gpigc.core.database.SystemData;
import com.gpigc.core.database.SystemDataGateway;

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
		List<SystemData> systemDatas = getSystemData();
		Iterator<SystemData> iterator = systemDatas.iterator();
		double total = 0;
		
		while(iterator.hasNext()) {
			total = total + processPayload(iterator.next().getPayload());
		}
		
		Map<String, Double> result = new HashMap<String, Double>();
		result.put("Test", total / (double) systemDatas.size());
		
		return new Result(result, true);
	}
	
	private double processPayload(Map<String, String> payload) {
		Iterator<String> iterator = payload.keySet().iterator();
		int total = 0;
		
		while(iterator.hasNext()) {
			total = total + Integer.parseInt(payload.get(iterator.next()));
		}
		
		return total / (double) payload.size();
	}
	
	private List<SystemData> getSystemData() {
		List<SystemData> systemData = new ArrayList<SystemData>();
		for(String systemId : associatedSystems) {
			systemData.addAll(database.readSystemData(systemId, TEN_RECORDS));			
		}
		return systemData;
	}

}
