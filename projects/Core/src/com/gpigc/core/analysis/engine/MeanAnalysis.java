package com.gpigc.core.analysis.engine;

import java.util.Observable;
import com.gpigc.core.analysis.System;
import com.gpigc.core.analysis.AnalysisEngine;


public class MeanAnalysis extends AnalysisEngine {

	private Observable observable;

	public MeanAnalysis(Observable observable) {
		this.observable = observable;
		observable.addObserver(this);
	}	
	
	public void update(Observable observable, Object arg) {
		if(observable instanceof System) {
			this.observable = observable;
		}
	}

	@Override
	public void analyse() {
		// TODO Do some analysis stuff (Mean ;) )
	}
}