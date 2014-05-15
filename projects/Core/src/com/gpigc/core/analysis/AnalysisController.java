package com.gpigc.core.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Controller;
import com.gpigc.core.Core;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.view.StandardMessageGenerator;

/**
 * Interacts with the core application, performing analysis on the data
 * persisted to the database.
 * 
 * @author GPIGC
 */
public class AnalysisController extends Controller {

	private List<AnalysisEngine> analysisEngines;

	public AnalysisController(List<ClientSystem> systems, Core core) {
		super(ControllerType.analysis, core);
		refreshSystems(systems);
	}

	public void refreshSystems(List<ClientSystem> systems) {
		analysisEngines = (List<AnalysisEngine>) instantiateEngines(systems, List.class, Core.class);
	}

	public void analyse(Set<String> systemIDs) {
		System.out.println("Beginning Analysis");
		for (String currentSystemID : systemIDs) {
			for (AnalysisEngine engine : analysisEngines) {
				// If this engine is registered to this system
				if (engine.getRegisteredSystem(currentSystemID) != null) {
					System.out.println("Checking For Event");
					DataEvent event = engine.analyse(engine.getRegisteredSystem(currentSystemID));
					if (event != null) {
						StandardMessageGenerator.eventGenerated(engine.name, currentSystemID);
						core.generateNotification(event);
					}
				}
			}
		}
	}

	protected List<ClientSystem> getRegisteredSystems(String simpleName, List<ClientSystem> allSystems) {
		List<ClientSystem> registeredSystems = new ArrayList<ClientSystem>();
		for (ClientSystem system : allSystems) {
			if (system.getRegisteredEngineNames().contains(simpleName)) {
				registeredSystems.add(system);
			}
		}
		return registeredSystems;
	}

	public List<AnalysisEngine> getAnalysisEngines() {
		return analysisEngines;
	}

}
