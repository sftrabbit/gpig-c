/**
 * 
 */
package com.gpigc.dataabstractionlayer.client;

import java.util.Date;
import java.util.List;

/**
 * The interface to any datastore
 * 
 * @author GPIGC
 */
public interface SystemDataGateway {
	
	/**
	 * @param systemID A systemID
	 * @param sensorID The ID of the sensor you wish to read, if null all sensors will be returned
	 * @param numRecords The maximum number of records to return
	 * @return The numRecords most recent records associated with the given
	 * systemID
	 * @throws FailedToReadFromDatastoreException
	 */
	public QueryResult readMostRecent(String systemID,String sensorID, int numRecords) 
			throws FailedToReadFromDatastoreException;
	
	/**
	 * @param systemID A systemID
	 * @param sensorID The ID of the sensor you wish to read, if null all sensors will be returned
	 * @param start The earliest point in the time period
	 * @param end The latest point in the time period
	 * @return All records for a given systemID within the given time period
	 * @throws FailedToReadFromDatastoreException
	 */
	public QueryResult readBetween(String systemID, String sensorID, Date start, Date end) 
			throws FailedToReadFromDatastoreException;
	
	/**
	 * Writes the given data to the datastore
	 * 
	 * @param data The data to be written
	 * @throws FailedToWriteToDatastoreException When we couldn't write to the
	 */
	public void write(EmitterSystemState data) throws FailedToWriteToDatastoreException;

	/**
	 * Writes a batch of data to the datastore
	 * 
	 * @param data The data to be written
	 * @throws FailedToWriteToDatastoreException When we couldn't write to the
	 */
	public void write(List<EmitterSystemState> data) throws FailedToWriteToDatastoreException;
}
