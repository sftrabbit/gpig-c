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
import com.gpigc.core.Parameter;
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

	/**
	 * The variables of the expression
	 */
	private Map<String, Variable> variables;

	/**
	 * The parser used to parse the expression
	 */
	private Parser parser;

	public ExpressionAnalysisEngine(List<ClientSystem> registeredSystems,
			SystemDataGateway datastore) {
		super(registeredSystems, datastore);
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
				System.out.println("Expression Evaluated to: " + value);
				return generateEvent(system,exprStr, value);

			} catch (SyntaxException e) {
				System.out.println("Could not parse string, ignoring data");
				e.printStackTrace();
			}catch (FailedToReadFromDatastoreException e1) {
				System.out.println("Could not read data from datastore");
				e1.printStackTrace();
			}
		}else{
			System.out.println("System does not have an expression parameter");
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
			SensorState state = getSensorReadings(system.getID(), sensor.getID(), 1).get(0);
			if(state!=null){
				values.add(state);
			}else{
				System.out.println("Sensor Value: " + sensor.getID() +" Missing: Can not analyse");
				return null;
			}
		}
		return values;
	}
}