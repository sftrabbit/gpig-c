/**
 * 
 */
package com.gpigc.core.analysis.engine;

import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;

/**
 * @author GPIG-C
 */
public class FaceAnalysisEngine extends AnalysisEngine {

	public FaceAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems, core);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse(com.gpigc.core.ClientSystem)
	 */
	@Override
	public DataEvent analyse(ClientSystem system) {
		// TODO Auto-generated method stub
		return null;
	}

}
