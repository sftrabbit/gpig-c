/**
 * 
 */
package com.gpigc.core.analysis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

import expr.Parser;
import expr.SyntaxException;
import expr.Variable;

/**
 * Computes a value based on a given expression String
 * 
 * @author GPIG-C
 */
public class ExpressionAnalysisEngine extends AnalysisEngine {

	private static final int NUM_TO_GET = 1;

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


	public ExpressionAnalysisEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
	}


	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse()
	 */
	@Override
	public DataEvent analyse(ClientSystem system) {

		//		if(system.getParameters().containsKey("Expression")){
		//			String exprStr = system.getParameters().get("Expression");
		//
		//		Map<String, String> dataToSave = new HashMap<>();
		//		try {
		//			List<SensorState> sensorStates = getSensorStates(ONE_VALUE_PER_SENSOR);
		//			if (error) {
		//				throw new FailedToReadFromDatastoreException(
		//						"Couldn't load sensor states from DB during analysis");
		//			}
		//			variables = new HashMap<>(associatedSystems.size());
		//			for (String systemID : associatedSystems) {
		//				// TODO Have a way of getting all sensors associated with a system
		//				for (String sensorID : getSensorsForSystem(systemID)) {
		//					Variable sensorIdVar = Variable.make(sensorID);
		//					parser.allow(sensorIdVar);
		//					variables.put(systemID, sensorIdVar);
		//				}
		//			}
		//			System.out.println("Variables = " + variables);
		//			for (SensorState sensorState : sensorStates) {
		//				System.out.println("Variable for ID " + 
		//						sensorState.getSensorID() + " = " + 
		//						variables.get(sensorState.getSensorID()));
		//				variables.get(sensorState.getSensorID())
		//					.setValue(Double.parseDouble(sensorState.getValue()));
		//			}
		//			double value = parser.parseString(exprStr).value();
		//			dataToSave.put(getEngineName() + " value", ""+value);
		//		} catch (FailedToReadFromDatastoreException | SyntaxException e) {
		//			dataToSave.put(ERROR, getEngineName() + " in error: " + 
		//					e.getClass().getCanonicalName() + ", " + 
		//					e.getMessage());
		//		}
		//		return new Result(dataToSave, notify);
		return null;
	}
}