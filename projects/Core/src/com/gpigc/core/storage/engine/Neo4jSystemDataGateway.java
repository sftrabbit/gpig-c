package com.gpigc.core.storage.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
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

	private static final DynamicRelationshipType SYSTEM_OWNS_SENSOR = DynamicRelationshipType.withName("OWNS");
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
			ResourceIterable<Node> system = graphDb.index().forNodes("systems").query("system_id", data.getSystemID());
			Node systemNode = null;
			if(system.iterator().hasNext() == false) {
				systemNode = graphDb.createNode(GPIGCLabel.SYSTEM);
				systemNode.setProperty("system_id", data.getSystemID());
				systemNode.setProperty("last_write", new Date().getTime());
				graphDb.index().forNodes("systems").add(systemNode, "system_id", systemNode.getProperty("system_id"));
			} else {
				systemNode = system.iterator().next();
				systemNode.setProperty("last_write", new Date().getTime());
			}
			
			
			for (String sensorID : data.getSensorReadings().keySet()) {
				updateValueNode(data, systemNode, sensorID);
			}
			
			tx.success();
		} catch (Exception e) {
		    tx.failure();
		    e.printStackTrace();
		} finally {
			tx.close();
		}
	}

	private void updateValueNode(EmitterSystemState data, Node systemNode, String sensorID) {
		Iterator<Relationship> outgoingRelationships = systemNode.getRelationships(Direction.OUTGOING).iterator();
		while(outgoingRelationships.hasNext()) {
			Node endNode = outgoingRelationships.next().getEndNode();
			if(endNode.getProperty("sensor_id").equals(sensorID)) {
				Node value = graphDb.createNode(GPIGCLabel.VALUE);
				value.setProperty("value", data.getSensorReadings().get(sensorID));
				value.setProperty("measured_at", data.getTimeStamp().getTime());
				endNode.createRelationshipTo(value, DynamicRelationshipType.withName("SENSOR"));
				return;
			}
		}
		Node sensor = graphDb.createNode(GPIGCLabel.SENSOR);	
		sensor.setProperty("sensor_id", sensorID);
		systemNode.createRelationshipTo(sensor, SYSTEM_OWNS_SENSOR);
		Node value = graphDb.createNode(GPIGCLabel.VALUE);
		value.setProperty("value", data.getSensorReadings().get(sensorID));
		value.setProperty("measured_at", data.getTimeStamp().getTime());
		sensor.createRelationshipTo(value, DynamicRelationshipType.withName("SENSOR"));
		return;
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