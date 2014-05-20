package com.gpigc.core.storage.engine;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.joda.time.Interval;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.tempodb.Client;
import com.tempodb.ClientBuilder;
import com.tempodb.Credentials;
import com.tempodb.Cursor;
import com.tempodb.Database;
import com.tempodb.Filter;
import com.tempodb.MultiDataPoint;
import com.tempodb.Result;
import com.tempodb.Series;

public class TempoDBSystemDataGateway extends SystemDataGateway {

	private static final String HTTPS = "https";
	private static final String API_URL = "api.tempo-db.com";
	private static final String API_SUPER_SECRET_KEY = "fb51e37bb1074e3b8bd7d1017aa7933f";
	private static final String API_PUBLIC_KEY = "2fdf9408df7d43168c904b13f7d0ba4b";
	private static final String DATABASE_NAME = "heroku-2fdf9408df7d43168c904b13f7d0ba4b";
	private Client client;

	public TempoDBSystemDataGateway(List<ClientSystem> registeredSystems) {
		super(registeredSystems);
		setupConnection();
	}

	private void setupConnection() {
		Database database = new Database(DATABASE_NAME);
		Credentials credentials = new Credentials(API_PUBLIC_KEY, API_SUPER_SECRET_KEY);
		InetSocketAddress host = new InetSocketAddress(API_URL, 443);
		client = new ClientBuilder()
				        .database(database)
				        .credentials(credentials)
				        .host(host)
				        .scheme(HTTPS)
				        .build();

		client.deleteAllSeries();
	}

	public QueryResult readMostRecent(String systemID, String sensorID, int numRecords) throws FailedToReadFromDatastoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryResult readBetween(String systemID, String sensorID, Date start, Date end) throws FailedToReadFromDatastoreException {
		Interval interval = new Interval(start.getTime(), end.getTime());
		Filter filter = new Filter();
		filter.addKey(systemID + "." + sensorID);
		
		Cursor<MultiDataPoint> result = client.readMultiDataPoints(filter, interval);
		Iterator<MultiDataPoint> multiPoints = result.iterator();
		
		while(multiPoints.hasNext()) {
			System.out.println("asd");
			System.out.println(multiPoints.next().toString());
		}
		return null;
	}

	public void write(EmitterSystemState data) throws FailedToWriteToDatastoreException {
		Result<Series> result = null;
        Map<String, String> sensorReadings = data.getSensorReadings();
		for(String sensorID : sensorReadings.keySet()) {
			result = client.getSeries(data.getSystemID() + "." +sensorID);
			if(result.getValue() == null) {
				Set<String> tags = new HashSet<String>();
		        tags.add(data.getSystemID());
		        tags.add(sensorID);
				Map<String, String> attributes = new HashMap<String, String>();
		        attributes.put(data.getTimeStamp().toString(), sensorReadings.get(sensorID));
		        Series series = new Series(data.getSystemID() + "." +sensorID, data.getSystemID() + "." +sensorID, tags, attributes);
				result = client.createSeries(series);
			} else {
				Series series = result.getValue();
				series.getAttributes().put(data.getTimeStamp().toString(), sensorReadings.get(sensorID));
				client.updateSeries(series);
			}
			if(result.getCode() != HttpStatus.SC_OK) {
				throw new FailedToWriteToDatastoreException("Failed to write EmitterSystemState to TempoDB: " + result.getMessage());
			}
        }
	}

	public void write(List<EmitterSystemState> data) throws FailedToWriteToDatastoreException {
		for(EmitterSystemState systemState : data) {
			write(systemState);
		}
	}
}