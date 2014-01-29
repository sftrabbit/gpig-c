package com.gpig.server;

import java.io.IOException;
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
import com.gpig.client.SystemData;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class AppEngineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SYSTEM_ID_KEY = "SystemID";
	private static final String SENSOR_ID_KEY = "SensorID";
	private static final String CREATION_TIMESTAMP_KEY = "CreationTimestamp";
	private static final String DB_TIMESTAMP_KEY = "DatabaseTimestamp";
	private static final String VALUE_KEY = "Value";
	private static final String NUM_RECORDS_KEY = "NumRecords";
	private static final String ENTITY_KEY = "DataEntity";
	private static final String START_TIME_KEY = "StartTimeStamp";
	private static final String END_TIME_KEY = "EndTimeStamp";

	public static final SimpleDateFormat dateFormat = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		SystemData systemData;
		try {
			systemData = SystemData.parseJSON(req.getReader());
		} catch (Exception e) {
			resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST, 
					"Failed to parse JSON: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		Key systemKey = KeyFactory.createKey(SYSTEM_ID_KEY, systemData.getSystemID());
		Date dataBaseTimestamp = new Date();
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Entity> entities = new ArrayList<>();
		for(String key: systemData.getPayload().keySet()){
			Key sensorKey = KeyFactory.createKey(systemKey, SENSOR_ID_KEY,key);
			Entity entity = new Entity(ENTITY_KEY,sensorKey);
			entity.setProperty(CREATION_TIMESTAMP_KEY, systemData.getTimeStamp());
			entity.setProperty(DB_TIMESTAMP_KEY,dataBaseTimestamp);
			entity.setProperty(VALUE_KEY,systemData.getPayload().get(key));
		}
		datastoreService.put(entities);
	}

	@Override
	protected void doGet(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException,
			java.io.IOException{
		String systemID = req.getParameter(SYSTEM_ID_KEY);
		if(systemID != null){
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		//Want data for whole system
		Key systemIDKey = KeyFactory.createKey(SYSTEM_ID_KEY, systemID);
		Query query = getQueryWithRequest(req, systemIDKey);
		List<Entity> results;
		if(req.getParameter(NUM_RECORDS_KEY) != null){
			results = queryWithNumRecords(Integer.parseInt(
					req.getParameter(NUM_RECORDS_KEY)), query, datastoreService);
		}else if(req.getParameter(START_TIME_KEY) != null && 
				req.getParameter(END_TIME_KEY) !=null){
			results = queryWithLimits(req.getParameter(START_TIME_KEY),
					req.getParameter(END_TIME_KEY), query, datastoreService);
		}else{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		writeResponse(systemID, resp, results);
		}else{
			resp.getWriter().println("No System");
		}
	}

	private void writeResponse(String systemID, HttpServletResponse resp, List<Entity> results) throws IOException {
		for(Entity result: results){
			resp.getWriter().println(result);
		}
	}

	private List<Entity> queryWithLimits(String startDate, String endDate,
			Query query, DatastoreService datastoreService) {
		Collection<Filter> subFilters = new ArrayList<>();
		subFilters.add(new FilterPredicate(CREATION_TIMESTAMP_KEY, FilterOperator.GREATER_THAN_OR_EQUAL, startDate));
		subFilters.add(new FilterPredicate(CREATION_TIMESTAMP_KEY, FilterOperator.LESS_THAN_OR_EQUAL, endDate));
		CompositeFilter filter = new CompositeFilter(CompositeFilterOperator.AND, subFilters);
		query.setFilter(filter);
		return  datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
	}

	private Query getQueryWithRequest(HttpServletRequest req, Key systemIDKey) {
		String sensorID = req.getParameter(SENSOR_ID_KEY);	
		if(sensorID == null){
			return new Query(ENTITY_KEY,systemIDKey).addSort(CREATION_TIMESTAMP_KEY, Query.SortDirection.DESCENDING);
		}else{
			Key sensorKey = KeyFactory.createKey(systemIDKey,SENSOR_ID_KEY, sensorID);
			return new Query(ENTITY_KEY,sensorKey).addSort(CREATION_TIMESTAMP_KEY, Query.SortDirection.DESCENDING);
		}
	}

	private List<Entity> queryWithNumRecords(int numRecords,
			Query query, DatastoreService datastoreService) {
		return  datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(numRecords));
	}
}
