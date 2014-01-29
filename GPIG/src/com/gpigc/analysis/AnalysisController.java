package com.gpigc.analysis;
import java.util.List;

public class AnalysisController {

	private List<AnalysisEngine> engines;

	public void systemUpdate(String systemId) {
		for(AnalysisEngine engine : engines) {
			List<String> associatedSystems = engine.getAssociatedSystems();
			if (associatedSystems.contains(systemId)) {
				engine.analyse().process();
			}
		}
	}

}