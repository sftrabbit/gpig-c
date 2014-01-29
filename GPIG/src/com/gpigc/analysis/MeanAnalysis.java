package com.gpigc.analysis;

import java.util.ArrayList;
import java.util.List;

import com.gpigc.database.SystemData;

public class MeanAnalysis extends AnalysisEngine {

	private static final int TEN_RECORDS = 10;
	
	public MeanAnalysis() {
		associatedSystems = new ArrayList<String>();
		associatedSystems.add("1");
		associatedSystems.add("2");
		associatedSystems.add("3");
	}

	public Result analyse() {
		
		List<SystemData> systemData = getSystemData();
		
		// List<TDObject> data = dao.getDataBetween(componentID, Date.OneHourAgo(), Date.now());
		// Get data for last 10 minutes
		// Sum then divide by total
		// is it higher than it should be? if so trigger event
		return new Result(null, true);
	}
	
	private List<SystemData> getSystemData() {
		List<SystemData> systemData = new ArrayList<SystemData>();
		for(String systemId : associatedSystems) {
			systemData.addAll(database.readSystemData(systemId, TEN_RECORDS));			
		}
		return systemData;
	}

}
