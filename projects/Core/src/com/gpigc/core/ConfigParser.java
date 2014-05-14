package com.gpigc.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.gpigc.core.view.StandardMessageGenerator;

public class ConfigParser {


	private final String SYSTEM_KEY = "Systems";
	private final String SYSTEM_ID_KEY = "SystemID";
	private final String SENSORS_KEY = "Sensors";
	private final String SENSOR_ID_KEY = "SensorID";
	private final String PARAMS_KEY = "Params";
	private final String ENGINES_KEY = "Engines";
	private final String SYSTEM_GATEWAY_KEY = "DatastoreGateway";



	public ArrayList<ClientSystem> parse(File configFile) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(configFile.getPath()));
		return parse( new String(encoded, StandardCharsets.UTF_8));
	}

	public ArrayList<ClientSystem> parse(String json) {
		try {
			ArrayList<ClientSystem> systems = new ArrayList<>();
			JsonFactory jfactory = new JsonFactory();

			JsonParser jParser = jfactory
					.createParser(json);

			// LOOP UNTIL WE READ END OF JSON DATA, INDICATED BY }
			while (jParser.nextToken() != JsonToken.END_OBJECT) {

				String fieldname = jParser.getCurrentName();
				if (SYSTEM_KEY.equals(fieldname)) {
					// once we get the token name we are interested,
					// move next to get its value
					jParser.nextToken();
					while (jParser.nextToken() != JsonToken.END_ARRAY) {

						String systemID = null;
						String systemDataGatewayName = null;
						List<ClientSensor> sensors = new ArrayList<>();
						List<String> registeredEngines = new ArrayList<>();
						Map<Parameter, String> params = new HashMap<>();

						while (jParser.nextToken() != JsonToken.END_OBJECT) {
							String key = jParser.getCurrentName();
							if (SYSTEM_ID_KEY.equals(key) && jParser.getCurrentToken() == JsonToken.VALUE_STRING) {
								systemID = jParser.getText();
							}
							if (SENSORS_KEY.equals(key)) {
								while (jParser.nextToken() != JsonToken.END_ARRAY) {
									sensors.add(parseSensor(jParser));
								}
							}
							if (ENGINES_KEY.equals(key)) {
								while (jParser.nextToken() != JsonToken.END_ARRAY) {
									if(jParser.getCurrentToken() == JsonToken.VALUE_STRING){
										registeredEngines.add(jParser.getText());
									}
								}
							}
							if (SYSTEM_GATEWAY_KEY.equals(key) && jParser.getCurrentToken() == JsonToken.VALUE_STRING) {
								systemDataGatewayName = jParser.getText();
							}
							if (PARAMS_KEY.equals(key)) {
								while(jParser.nextToken() != JsonToken.END_OBJECT){
									String paramkey = jParser.getCurrentName();
									if(Parameter.isValid(paramkey)){
										jParser.nextToken();
										params.put(Parameter.valueOf(paramkey), jParser.getText());
										System.out.println("Valid Param: " + paramkey);
									}
								}
							}
						}
						if(systemID!=null && systemDataGatewayName!= null){
							systems.add(new ClientSystem(systemID, sensors, registeredEngines, systemDataGatewayName, params));
							StandardMessageGenerator.registeredSystem(systemID);
						}else{
							StandardMessageGenerator.couldNotReadSystemInConfig();
						}
					}

				}
			}
			jParser.close();

			return systems;
		} catch ( IOException e) {
			StandardMessageGenerator.couldNotReadConfig();
			StandardMessageGenerator.printException(e);
			return new ArrayList<ClientSystem>();
		}
	}

	protected ClientSensor parseSensor(JsonParser jParser) throws IOException {
		Map<Parameter,String> sensorParams = new HashMap<Parameter,String>();
		String sensorID = null;

		while(jParser.nextToken() != JsonToken.END_OBJECT){
			String key = jParser.getCurrentName();
			if (SENSOR_ID_KEY.equals(key)) {
				jParser.nextToken();
				sensorID = jParser.getText();
			}
			if (PARAMS_KEY.equals(key)) {
				while(jParser.nextToken() != JsonToken.END_OBJECT){
					String paramkey = jParser.getCurrentName();
					if(Parameter.isValid(paramkey)){
						jParser.nextToken();
						sensorParams.put(Parameter.valueOf(paramkey), jParser.getText());
					}
				}
			}

		}
		return new ClientSensor(sensorID, sensorParams);
	}


}
