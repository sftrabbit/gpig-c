package com.gpigc.core.storage.engine;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.tempodb.Client;
import com.tempodb.ClientBuilder;
import com.tempodb.Credentials;
import com.tempodb.Cursor;
import com.tempodb.DataPoint;
import com.tempodb.Database;
import com.tempodb.Result;
import com.tempodb.Series;
import com.tempodb.WriteRequest;

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
		client = new ClientBuilder().database(database).credentials(credentials).host(host).scheme(HTTPS).build();

		client.deleteAllSeries();
		
	}

	public QueryResult readMostRecent(String systemID, String sensorID, int numRecords) throws FailedToReadFromDatastoreException {
		String key = systemID + "." + sensorID;
		Series series;

		try {
			series = client.getSeries(key).getValue();
		} catch (Exception e) {
			throw new FailedToReadFromDatastoreException(e.toString());
		}

		Interval interval = new Interval(new DateTime(0), new DateTime());
		
		
		Iterator<DataPoint> result = client.readDataPoints(series, interval).iterator();
		List<SensorState> sensorStates = new ArrayList<SensorState>();
		while(result.hasNext()) {
			DataPoint dataPoint = result.next();
			SensorState sensorState = new SensorState(sensorID, new Date(), dataPoint.getTimestamp().toDate(), dataPoint.getValue().toString());
			sensorStates.add(sensorState);
		}
		
		int sublistStartIndex = 0;
		if((sensorStates.size() - numRecords) >= 0) {
			sublistStartIndex = sensorStates.size() - numRecords;
		}
		
		return new QueryResult(systemID, sensorStates.subList(sublistStartIndex, sensorStates.size()));
	}

	public QueryResult readBetween(String systemID, String sensorID, Date start, Date end) throws FailedToReadFromDatastoreException {
		Interval interval = new Interval(new DateTime(start), new DateTime(end));
		String key = systemID + "." + sensorID;
		Series series;

		try {
			series = client.getSeries(key).getValue();
		} catch (Exception e) {
			throw new FailedToReadFromDatastoreException(e.toString());
		}

		Cursor<DataPoint> result = client.readDataPoints(series, interval);
		Iterator<DataPoint> multiPoints = result.iterator();
		List<SensorState> sensorStates = new ArrayList<SensorState>();
		while (multiPoints.hasNext()) {
			DataPoint dataPoint = multiPoints.next();
			SensorState sensorState = new SensorState(sensorID, new Date(), dataPoint.getTimestamp().toDate(), dataPoint.getValue().toString());
			sensorStates.add(sensorState);
		}

		return new QueryResult(systemID, sensorStates);
	}

	public void write(EmitterSystemState data) throws FailedToWriteToDatastoreException {
		Result<Series> result = null;
		Set<String> tags = new HashSet<String>();
		tags.add(data.getSystemID());
		Map<String, String> sensorReadings = data.getSensorReadings();

		for (String sensorID : sensorReadings.keySet()) {
			String key = data.getSystemID() + "." + sensorID;
			result = client.getSeries(key);
			WriteRequest writeRequest = new WriteRequest();
			DataPoint dataPoint = new DataPoint();
			dataPoint.setTimestamp(new DateTime(data.getTimeStamp()));
			dataPoint.setValue(Integer.parseInt(sensorReadings.get(sensorID)));
			
			if (result.getValue() == null) {
				Series series = new Series(key);
				series.setTags(tags);
				series.setName(key);
				writeRequest.add(series, dataPoint);
			} else {
				Series series = result.getValue();
				series.setName(key);
				writeRequest.add(series, dataPoint);
			}
			Result<Void> writeResult = client.writeDataPoints(writeRequest);
			if (writeResult.getCode() != HttpStatus.SC_OK) {
				throw new FailedToWriteToDatastoreException("Failed to write EmitterSystemState to TempoDB: " + result.getMessage());
			}
		}
	}

	public void write(List<EmitterSystemState> data) throws FailedToWriteToDatastoreException {
		for (EmitterSystemState systemState : data) {
			write(systemState);
		}
	}
}