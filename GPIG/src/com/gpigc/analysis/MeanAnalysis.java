package com.gpigc.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gpigc.database.SystemData;
import com.gpigc.database.SystemDataGateway;

public class MeanAnalysis extends AnalysisEngine {

	private static final int TEN_RECORDS = 10;
	
	public MeanAnalysis(String[] systemIDs, SystemDataGateway database) {
		associatedSystems = new ArrayList<String>();
		for(String s : systemIDs) {
			associatedSystems.add(s);
		}
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
	
	private double processPayload(Map<String, Integer> payload) {
		Iterator<String> iterator = payload.keySet().iterator();
		int total = 0;
		
		while(iterator.hasNext()) {
			total = total + payload.get(iterator.next());
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
