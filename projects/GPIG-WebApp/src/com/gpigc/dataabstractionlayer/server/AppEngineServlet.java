package com.gpigc.dataabstractionlayer.server;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import static com.gpigc.dataabstractionlayer.server.DatabaseField.*;

/**
 * Interacts with the App Engine datastore to read and write data based on
 * instructions sent over HTTP from the rest of the HUMS
 * 
 * @author GPIGC
 */
public class AppEngineServlet extends HttpServlet {

	private static final long serialVersionUID = -5913676594563624612L;

	/**
	 * Called by the server to handle a POST request.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		List<EmitterSystemState> systemState;

		// Attempt to parse the request body
		try {
			systemState = parseJSON(req.getReader());
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Failed to parse JSON: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		DatastoreService datastoreService = DatastoreServiceFactory
				.getDatastoreService();
		
		List<Entity> allEntities = new ArrayList<>();
		
		for (EmitterSystemState state : systemState) {

			Date dataBaseTimestamp = new Date();
			resp.getWriter().println(
					state.getSystemID() + "    " + state.getTimeStamp()
							+ "       " + state.getSensorReadings());
			List<Entity> stateEntities = createEntities(state,
					dataBaseTimestamp);
			allEntities.addAll(stateEntities);
			resp.getWriter().println(stateEntities);
		}
		datastoreService.put(allEntities);
		resp.setStatus(HttpServletResponse.SC_CREATED);
	}

	public List<EmitterSystemState> parseJSON(Reader reader) 
			throws IOException, JsonParseException {
		System.out.println("Reader = " + reader);
		List<EmitterSystemState> systemState = new ArrayList<>();
		JsonFactory f = new JsonFactory();
		JsonParser parser = f.createParser(reader);
		parser.nextToken(); // Returns a start of object token
		System.out.println("Start of object = " + parser.getCurrentToken());
		parser.nextToken(); // States key
		System.out.println("Sensors key = " + parser.getCurrentToken());
		while (parser.nextToken() != JsonToken.END_ARRAY) {
			System.out.println(">>>>> Last token for state start = " + parser.getCurrentToken());
			systemState.add(EmitterSystemState.readTokens(parser));
			System.out.println("Last token for state end = " + parser.getCurrentToken());
		}
		parser.close();
		return systemState;
	}

	private ArrayList<Entity> createEntities(EmitterSystemState systemData,
			Date dataBaseTimestamp) {
		ArrayList<Entity> entities = new ArrayList<>();

		// Top Level Key: For the System
		Key systemKey = KeyFactory.createKey(SYSTEM_ID.getKey(),
				systemData.getSystemID());
		for (String key : systemData.getSensorReadings().keySet()) {
			// Second Level Key: For this Sensor
			Key sensorKey = KeyFactory.createKey(systemKey, SENSOR_ID.getKey(),
					key);
			Entity entity = new Entity(ENTITY.getKey(), sensorKey);
			entity.setProperty(CREATION_TIMESTAMP.getKey(), systemData
					.getTimeStamp().getTime());
			entity.setProperty(DB_TIMESTAMP.getKey(),
					dataBaseTimestamp.getTime());
			entity.setProperty(VALUE.getKey(), systemData.getSensorReadings().get(key));
			entities.add(entity);
		}
		return entities;
	}

	/**
	 * Called by the server to handle a GET request.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		String systemID = req.getParameter(SYSTEM_ID.getKey());

		if (systemID != null) {
			// Get the Datastore
			DatastoreService datastoreService = DatastoreServiceFactory
					.getDatastoreService();

			// Top level Entity Key
			Key systemIDKey = KeyFactory
					.createKey(SYSTEM_ID.getKey(), systemID);

			// Create either a sensor or system query
			Query query = getQueryWithRequest(req, systemIDKey);

			List<Entity> results;
			if (req.getParameter(NUM_RECORDS.getKey()) != null) {
				int numRecords = Integer.parseInt(req.getParameter(NUM_RECORDS
						.getKey()));
				results = queryWithNumRecords(numRecords, query,
						datastoreService);
			} else if (req.getParameter(START_TIME.getKey()) != null
					&& req.getParameter(END_TIME.getKey()) != null) {
				long startTime = Long.parseLong(req.getParameter(START_TIME
						.getKey()));
				long endTime = Long.parseLong(req.getParameter(END_TIME
						.getKey()));
				results = queryWithLimits(startTime, endTime, query,
						datastoreService);
			} else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			writeResponse(systemID, resp, results, req.getParameter("callback"));
			resp.setStatus(HttpServletResponse.SC_OK);
		} else {
			resp.getWriter().println("No System");
		}
	}

	/*
	 * Writes the HTTPResponse for a GET request
	 */
	private void writeResponse(String systemID, HttpServletResponse resp,
			List<Entity> results, String jscallback) throws IOException {

		if (jscallback != null) {
			resp.getWriter().println(jscallback + "(");
		}

		ArrayList<SensorState> sensorData = new ArrayList<>();
		for (Entity result : results) {
			String sensorID = result.getKey().getParent().getName();
			sensorData.add(new SensorState(sensorID, new Date(Long
					.parseLong(result.getProperty(CREATION_TIMESTAMP.getKey())
							.toString())), new Date(Long.parseLong(result
					.getProperty(DB_TIMESTAMP.getKey()).toString())), result
					.getProperty(VALUE.getKey()).toString()));
		}
		QueryResult queryResult = new QueryResult(systemID, sensorData);
		resp.getWriter().println(queryResult.toJSON());

		if (jscallback != null) {
			resp.getWriter().println(");");
		}
	}

	/*
	 * Queries the Datastore for DataEnties whose CreationTimestamp is 
	 * between the given limits
	 */
	private List<Entity> queryWithLimits(long startTime, long endTime,
			Query query, DatastoreService datastoreService) {
		Collection<Filter> subFilters = new ArrayList<>();
		subFilters.add(new FilterPredicate(CREATION_TIMESTAMP.getKey(),
				FilterOperator.GREATER_THAN_OR_EQUAL, startTime));
		subFilters.add(new FilterPredicate(CREATION_TIMESTAMP.getKey(),
				FilterOperator.LESS_THAN_OR_EQUAL, endTime));
		CompositeFilter filter = new CompositeFilter(
				CompositeFilterOperator.AND, subFilters);
		query.setFilter(filter);
		return datastoreService.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
	}

	/*
	 * Creates a Query from a ServletRequest, identifying whether we are querying
	 * a particular sensor or  a particular system
	 */
	private Query getQueryWithRequest(HttpServletRequest req, Key systemIDKey) {
		String sensorID = req.getParameter(SENSOR_ID.getKey());
		if (sensorID == null) { //Querying the Whole System.
			return new Query(ENTITY.getKey(), systemIDKey)
					.addSort(CREATION_TIMESTAMP.getKey(),
							Query.SortDirection.DESCENDING);
		} else { //Querying a Sensor
			Key sensorKey = KeyFactory.createKey(systemIDKey,
					SENSOR_ID.getKey(), sensorID);
			return new Query(ENTITY.getKey(), sensorKey)
					.addSort(CREATION_TIMESTAMP.getKey(),
							Query.SortDirection.DESCENDING);
		}
	}

	/*
	 * Queries the Datastore for latest 'numRecords' entries, the 
	 * query parameter already defines whether this for a whole system, or 
	 * a single sensor.
	 */
	private List<Entity> queryWithNumRecords(int numRecords, Query query,
			DatastoreService datastoreService) {
		return datastoreService.prepare(query).asList(
				FetchOptions.Builder.withLimit(numRecords));
	}
}
