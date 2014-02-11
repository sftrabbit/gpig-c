package com.gpigc.core.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * Single instance of the analysis controller which holds a list of systems
 * 
 * @author GPIGC
 */
public class AnalysisController {
	
	// Need to initialise this manually until functionality is available to do it through the UI
	private Map<String, System> systems;
	
	private static final AnalysisController ANALYSIS_CONTROLLER = new AnalysisController();
	
	private AnalysisController() { 
		systems = new HashMap<String, System>();
	}
	
	public static AnalysisController getInstance() {
		return ANALYSIS_CONTROLLER;
	}
	
	public void systemUpdate(String systemId) {
		systems.get(systemId).systemStateUpdate();
	}
}