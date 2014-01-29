package com.gpig.server;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.gpig.client.SystemData;

public class AppEngineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SYSTEM_ID_KEY = "SystemID";
	private static final String SENSOR_ID_KEY = "SensorID";
	private static final String CREATION_TIMESTAMP_KEY = "CreationTimestamp";
	private static final String DB_TIMESTAMP_KEY = "DatabaseTimestamp";
	private static final String VALUE_KEY = "Value";
	private static final String ENTITY_KEY = "DataEntity";
	public static final SimpleDateFormat dateFormat = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		SystemData systemData = SystemData.parseJSON(req.getReader());
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
		String sensorID = req.getParameter(SENSOR_ID_KEY);
		
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		//Want data for whole system
		if(sensorID == null){
			
		}
	}
	
	
}
