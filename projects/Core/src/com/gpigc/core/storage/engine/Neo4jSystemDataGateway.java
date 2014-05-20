package com.gpigc.core.storage.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;

public class Neo4jSystemDataGateway extends SystemDataGateway {

	private static final String DB_PATH = System.getProperty("user.home") + "/neo4j-GPIG-C-db";
	private GraphDatabaseService graphDb;
	private ExecutionEngine engine;
	private String readBetweenQuery = "MATCH p=(a)-->(b)-->(c) WHERE a.system_id={systemID} AND c.measured_at > {start} AND c.measured_at < {end} RETURN b.sensor_id, c.value, c.measured_at ORDER BY c.measured_at";
	private String readMostRecent = "MATCH p=(a)-->(b)-->(c) WHERE a.system_id = {systemID} RETURN b.sensor_id, c.value, c.measured_at ORDER BY c.measured_at LIMIT {numberOfRecords}";
	
	public Neo4jSystemDataGateway(List<ClientSystem> registeredSystems) {
		super(registeredSystems);
		createDb();
		engine = new ExecutionEngine(graphDb);
	}

	public QueryResult readMostRecent(String systemID, String sensorID, int numRecords) throws FailedToReadFromDatastoreException {
		List<SensorState> sensorStates = new ArrayList<SensorState>();
		Transaction tx = graphDb.beginTx();
		try {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("systemID", systemID);
			params.put("numberOfRecords", numRecords);
			ExecutionResult executionResult = engine.execute(readMostRecent, params);

			ResourceIterator<Map<String, Object>> resultSet = executionResult.iterator();

			SensorState sensorState;
			while (resultSet.hasNext()) {
				Map<String, Object> resultMap = resultSet.next();
				sensorState = new SensorState((String) resultMap.get("b.sensor_id"), new Date(), new Date((long) resultMap.get("c.measured_at")),
						(String) resultMap.get("c.value"));
				sensorStates.add(sensorState);
			}
			
			resultSet.close();
			tx.success();
			
		} catch (Exception e) {
		    tx.failure();
		    e.printStackTrace();
		} finally {
			tx.close();
		}
		return new QueryResult(systemID, sensorStates);
	}

	public QueryResult readBetween(String systemID, String sensorID, Date start, Date end) throws FailedToReadFromDatastoreException {
		List<SensorState> sensorStates = new ArrayList<SensorState>();
		ResourceIterator<Map<String, Object>> resultSet = null;
		Transaction tx = graphDb.beginTx();
		try {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("systemID", systemID);
			params.put("start", start.getTime());
			params.put("end", end.getTime());
			
			ExecutionResult executionResult = engine.execute(readBetweenQuery, params);

			resultSet = executionResult.iterator();

			SensorState sensorState;
			while (resultSet.hasNext()) {
				Map<String, Object> resultMap = resultSet.next();
				sensorState = new SensorState(	(String) resultMap.get("b.sensor_id"), 
												new Date(), 
												new Date((long) resultMap.get("c.measured_at")),
												(String) resultMap.get("c.value"));
				sensorStates.add(sensorState);
			}
			
			tx.success();
		} catch (Exception e) {
		    tx.failure();
		    e.printStackTrace();
		} finally {
			resultSet.close();
			tx.close();
		}
		return new QueryResult(systemID, sensorStates);
	}

	public void write(EmitterSystemState data) throws FailedToWriteToDatastoreException {
		Transaction tx = graphDb.beginTx();
		try {
			// Create new system if one does not currently exist
			Node system = graphDb.createNode(GPIGCLabel.SYSTEM);
			system.setProperty("system_id", data.getSystemID());
			system.setProperty("last_write", new Date().getTime());

			// Add system_id to index
			graphDb.index().forNodes("systems").add(system, "system_id", system.getProperty("system_id"));

			for (String sensorID : data.getSensorReadings().keySet()) {
				Node sensor = graphDb.createNode(GPIGCLabel.SENSOR);
				sensor.setProperty("sensor_id", sensorID);
				system.createRelationshipTo(sensor, DynamicRelationshipType.withName("OWNS"));
				Node value = graphDb.createNode(GPIGCLabel.VALUE);
				value.setProperty("value", data.getSensorReadings().get(sensorID));
				value.setProperty("measured_at", data.getTimeStamp().getTime());
				sensor.createRelationshipTo(value, DynamicRelationshipType.withName("SENSOR"));
			}
			
			tx.success();
		} catch (Exception e) {
		    tx.failure();
		    e.printStackTrace();
		} finally {
			tx.close();
		}
	}

	public void write(List<EmitterSystemState> data) throws FailedToWriteToDatastoreException {
		for (EmitterSystemState systemState : data) {
			write(systemState);
		}
	}
	
	public void shutDownDatabase() {
		graphDb.shutdown();
	}

	private void createDb() {
		clearDb();
		System.out.println("creating neo4j instance in " + DB_PATH);
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		registerShutdownHook(graphDb);
	}

	private void clearDb() {
		try {
			FileUtils.deleteRecursively(new File(DB_PATH));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	private enum GPIGCLabel implements Label {
		SYSTEM, SENSOR, VALUE;
	}
}