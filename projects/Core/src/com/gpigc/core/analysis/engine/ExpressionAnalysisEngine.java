/**
 * 
 */
package com.gpigc.core.analysis.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSensor;
import com.gpigc.core.ClientSystem;
import com.gpigc.core.Core;
import com.gpigc.core.Parameter;
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.SensorState;

import expr.Parser;
import expr.SyntaxException;
import expr.Variable;

/**
 * Computes a value based on a given expression String
 * 
 * @author GPIG-C
 */
public class ExpressionAnalysisEngine extends AnalysisEngine {

	/**
	 * The variables of the expression
	 */
	private Map<String, Variable> variables;

	/**
	 * The parser used to parse the expression
	 */
	private Parser parser;

	public ExpressionAnalysisEngine(List<ClientSystem> registeredSystems, Core core) {
		super(registeredSystems,core);
		parser = new Parser();
		variables = new HashMap<String, Variable>();
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.analysis.AnalysisEngine#analyse()
	 */
	@Override
	public DataEvent analyse(ClientSystem system) {

		if(system.getParameters().containsKey(Parameter.EXPRESSION)){
			String exprStr = system.getParameters().get(Parameter.EXPRESSION);

			try {
				//Get Data
				List<SensorState> values = getSensorData(system);
				for(SensorState sensorState: values){
					Variable var = Variable.make(sensorState.getSensorID());
					var.setValue(Double.parseDouble(sensorState.getValue()));
					parser.allow(var);
					variables.put(sensorState.getSensorID(), var);	
				}
				double value = parser.parseString(exprStr).value();
				return generateEvent(system,exprStr, value);

			} catch (SyntaxException e) {
				StandardMessageGenerator.couldNotParse(exprStr);
				e.printStackTrace();
			}catch (FailedToReadFromDatastoreException e1) {
				StandardMessageGenerator.couldNotReadData();
				e1.printStackTrace();
			}
		}else{
			StandardMessageGenerator.wrongParams(system.getID(), name);
		}

		return null;
	}


	private DataEvent generateEvent(ClientSystem system, String exprStr, double value) {
		Map<String,String> data = new HashMap<>();
		data.put("Message", "Expression analysis of system " 
				+ system.getID() + " showed abnormal system behaviour: " + exprStr);
		data.put("Subject", this.name+ " Notification");
		data.put("Recepient", "gpigc.alerts@gmail.com");
		data.put("Value",value+"");
		return new DataEvent(data, system);
	}


	private List<SensorState> getSensorData(ClientSystem system) throws FailedToReadFromDatastoreException {
		List<SensorState> values = new ArrayList<>();
		for(ClientSensor sensor: system.getSensors()){
			SensorState state = getSensorReadings(system, sensor.getID(), 1).get(0);
			if(state!=null){
				values.add(state);
			}else{
				StandardMessageGenerator.sensorValueMissing(system.getID(),sensor.getID());
				return null;
			}
		}
		return values;
	}
}