/**
 * 
 */
package com.gpigc.core.analysis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.analysis.Result;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

import expr.Parser;
import expr.Variable;

/**
 * Computes a value based on a given expression String
 * 
 * @author GPIG-C
 */
public class ExpressionAnanlysis extends AnalysisEngine {

	private static final int ONE_VALUE_PER_SENSOR = 1;

	/**
	 * The expression to evaluate as part of analysis
	 */
	private final String exprStr;
	
	/**
	 * The variables of the expression
	 */
	private Map<String, Variable> variables;
	
	/**
	 * The parser used to parse the expression
	 */
	private Parser parser;

	/**
	 * Whether to generate a notification on analysis
	 */
	private boolean notify;

	/**
	 * @param name A descriptive name for this engine
	 * @param expression The expression to evaluate
	 * @param associatedSystems The systems to analyse
	 * @param database The database to use
	 * @param notify Whether to notify the user on analysis
	 */
	public ExpressionAnanlysis(
			String name, 
			String expression, 
			List<String> associatedSystems, 
			SystemDataGateway database,
			boolean notify) {
		super(name, associatedSystems, database);
		this.exprStr = expression;
		this.notify = notify;
		parser = new Parser();
		variables = new HashMap<>(associatedSystems.size());
		for (String systemID : associatedSystems) {
			Variable systemIdVar = Variable.make(systemID);
			parser.allow(systemIdVar);
			variables.put(systemID, systemIdVar);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse()
	 */
	@Override
	public Result analyse() {
		Map<String, String> dataToSave = new HashMap<>();
		try {
			List<SensorState> sensorStates = getSensorStates(ONE_VALUE_PER_SENSOR);
			if (error) 
				throw new Exception(
						"Couldn't load sensor states from DB during analysis");
			for (SensorState sensorState : sensorStates) {
				variables.get(sensorState.getSensorID())
					.setValue(Double.parseDouble(sensorState.getValue()));
			}
			Double value = parser.parseString(exprStr).value();
			dataToSave.put(getEngineName() + " value", value.toString());
		} catch (Exception e) {
			dataToSave.put(ERROR, getEngineName() + ": " + e.getMessage());
		}
		return new Result(dataToSave, notify);
	}

	
	
}
