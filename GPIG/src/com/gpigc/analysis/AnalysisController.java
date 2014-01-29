package com.gpigc.analysis;
import java.util.ArrayList;
import java.util.List;

public class AnalysisController {

	private List<AnalysisEngine> engines;
	
	public AnalysisController() {
		
		engines = new ArrayList<AnalysisEngine>();
		//engines.addall(new MeanAnalysis());
		
		instantiateEngines();
	}

	public void systemUpdate(String systemId) {
		for(AnalysisEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId)) {
				engine.analyse().process();
			}
		}
	}

	private void instantiateEngines() {
		
	}	
}