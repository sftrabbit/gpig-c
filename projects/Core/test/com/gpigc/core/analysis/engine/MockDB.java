package com.gpigc.core.analysis.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class MockDB implements SystemDataGateway {

	
	public QueryResult readMostRecent(String systemID,String sensorID, int numRecords) 
			throws FailedToReadFromDatastoreException{
		return new QueryResult(systemID, getRecords(sensorID,numRecords));
	}
	

	public QueryResult readBetween(String systemID, String sensorID, Date start, Date end) 
			throws FailedToReadFromDatastoreException{
		
		return new QueryResult(systemID,getRecords(sensorID,5));
	}

	private List<SensorState> getRecords(String sensorID,int num) {
		List<SensorState> records = new ArrayList<>();
		for(int i = 0; i < num; i++){
			records.add(new SensorState(sensorID, new Date(), new Date(), i+""));
		}
		return records;
	}


	public void write(EmitterSystemState data) throws FailedToWriteToDatastoreException{
	}
	
	public void write(List<EmitterSystemState> data) throws FailedToWriteToDatastoreException{
	}

}
