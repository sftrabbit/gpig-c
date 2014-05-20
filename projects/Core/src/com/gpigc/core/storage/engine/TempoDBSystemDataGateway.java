package com.gpigc.core.storage.engine;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.tempodb.Client;
import com.tempodb.Credentials;
import com.tempodb.Database;

public class TempoDBSystemDataGateway extends SystemDataGateway {

	private Client client;
	
	public TempoDBSystemDataGateway(List<ClientSystem> registeredSystems) {
		super(registeredSystems);
		setupConnection();
	}

	private void setupConnection() {
		Database database = new Database("heroku-2fdf9408df7d43168c904b13f7d0ba4b");
	    Credentials credentials = new Credentials("2fdf9408df7d43168c904b13f7d0ba4b", "fb51e37bb1074e3b8bd7d1017aa7933f");
	    InetSocketAddress host = new InetSocketAddress("api.tempo-db.com", 443);
	    client = new Client(database, credentials, host, "https");
	}

	public QueryResult readMostRecent(String systemID, String sensorID, int numRecords) throws FailedToReadFromDatastoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryResult readBetween(String systemID, String sensorID, Date start, Date end) throws FailedToReadFromDatastoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(EmitterSystemState data) throws FailedToWriteToDatastoreException {
		// TODO Auto-generated method stub
		
	}

	public void write(List<EmitterSystemState> data) throws FailedToWriteToDatastoreException {
		// TODO Auto-generated method stub
		
	}
}