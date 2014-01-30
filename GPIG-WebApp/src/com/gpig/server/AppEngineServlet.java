package com.gpig.server;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.gpig.client.QueryResult;
import com.gpig.client.SystemData;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import static com.gpig.server.DatabaseField.*;

public class AppEngineServlet extends HttpServlet {

	private static final long serialVersionUID = -5913676594563624612L;
	private static final String DATE_FORMAT_STRING 	= "yyyy-MM-dd HH:mm:ss:SSS";
	private static final SimpleDateFormat DATE_FORMAT = 
			new SimpleDateFormat(DATE_FORMAT_STRING);

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		SystemData systemData;

		//Attempt to parse the request body
		try {
			systemData = SystemData.parseJSON(req.getReader());
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"Failed to parse JSON: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		//Get the Datastore
		DatastoreService datastoreService = 
				DatastoreServiceFactory.getDatastoreService();

		Date dataBaseTimestamp = new Date();
		datastoreService.put(createEntities(systemData, dataBaseTimestamp));
		resp.setStatus(HttpServletResponse.SC_CREATED);
	}


	private ArrayList<Entity> createEntities(SystemData systemData,
			Date dataBaseTimestamp) {
		ArrayList<Entity> entities 	= new ArrayList<>();

		//Top Level Key: For the System
		Key systemKey = KeyFactory.createKey(SYSTEM_ID.getKey(),
				systemData.getSystemID());

		for(String key: systemData.getPayload().keySet()){
			//Second Level Key: For this Sensor
			Key sensorKey = KeyFactory.createKey(systemKey, 
					SENSOR_ID.getKey(),key);
			Entity entity = new Entity(ENTITY.getKey(),sensorKey);
			entity.setProperty(CREATION_TIMESTAMP.getKey(),systemData.getTimeStamp().getTime());
			entity.setProperty(DB_TIMESTAMP.getKey(),dataBaseTimestamp.getTime());
			entity.setProperty(VALUE.getKey(),systemData.getPayload().get(key));
			entities.add(entity);
		}
		return entities;
	}

	@Override
	protected void doGet(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException,
			java.io.IOException{
		String systemID = req.getParameter(SYSTEM_ID.getKey());

		if(systemID != null){
			//Get the Datastore
			DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
			
			//Top level Entity Key
			Key systemIDKey = KeyFactory.createKey(SYSTEM_ID.getKey(), systemID);
			
			//Create either a sensor or system query
			Query query 	= getQueryWithRequest(req, systemIDKey);
			
			List<Entity> results;
			if(req.getParameter(NUM_RECORDS.getKey()) != null){
				int numRecords = Integer.parseInt(req.getParameter(NUM_RECORDS.getKey()));
				results = queryWithNumRecords(numRecords, query, datastoreService);
			}else if(req.getParameter(START_TIME.getKey()) != null && 
					req.getParameter(END_TIME.getKey()) !=null){
				long startTime = Long.parseLong(req.getParameter(START_TIME.getKey()));
				long endTime = Long.parseLong(req.getParameter(END_TIME.getKey()));
				results = queryWithLimits(startTime,endTime, query, datastoreService);
			}else{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			writeResponse(systemID, resp, results);
			resp.setStatus(HttpServletResponse.SC_OK);
		}else{
			resp.getWriter().println( "No System");
		}
	}

	private void writeResponse(String systemID, HttpServletResponse resp, 
			List<Entity> results) throws IOException {
		
		ArrayList<DBRecord> sensorData = new ArrayList<>();
		for(Entity result: results){
			String sensorID = result.getKey().getParent().getName();
				sensorData.add(new DBRecord(sensorID, 
						new Date(Long.parseLong(result.getProperty(CREATION_TIMESTAMP.getKey()).toString())), 
						new Date(Long.parseLong(result.getProperty(DB_TIMESTAMP.getKey()).toString())),
						result.getProperty(VALUE.getKey()).toString()));
			QueryResult queryResult = new QueryResult(systemID, sensorData);
			resp.getWriter().println(queryResult.toJSON());
		}
	}

	private List<Entity> queryWithLimits(long startTime, long endTime,
			Query query, DatastoreService datastoreService) {
		Collection<Filter> subFilters = new ArrayList<>();
		subFilters.add(new FilterPredicate(CREATION_TIMESTAMP.getKey(), FilterOperator.GREATER_THAN_OR_EQUAL, startTime));
		subFilters.add(new FilterPredicate(CREATION_TIMESTAMP.getKey(), FilterOperator.LESS_THAN_OR_EQUAL, endTime));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, subFilters);
		query.setFilter(filter);
		return  datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
	}

	private Query getQueryWithRequest(HttpServletRequest req, Key systemIDKey) {
		String sensorID = req.getParameter(SENSOR_ID.getKey());	
		if(sensorID == null){
			return new Query(ENTITY.getKey(),systemIDKey).addSort(CREATION_TIMESTAMP.getKey(), Query.SortDirection.DESCENDING);
		}else{
			Key sensorKey = KeyFactory.createKey(systemIDKey,SENSOR_ID.getKey(), sensorID);
			return new Query(ENTITY.getKey(),sensorKey).addSort(CREATION_TIMESTAMP.getKey(), Query.SortDirection.DESCENDING);
		}
	}

	private List<Entity> queryWithNumRecords(int numRecords,
			Query query, DatastoreService datastoreService) {
		return  datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(numRecords));
	}
}
