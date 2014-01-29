package com.gpigc.analysis;
import java.util.List;

public class AnalysisController {
	//private DataAccessObect database;
	
	public void sensorUpdate(Integer componentID) {
		List<AnalysisEngine> engines = null;//database.findEnginesForComponent(componentID);
		for(AnalysisEngine engine : engines) {
			Result result = engine.analyse(componentID);
			result.pushNotification();
		}
	}
}