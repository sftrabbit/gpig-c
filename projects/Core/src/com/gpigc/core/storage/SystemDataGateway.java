/**
 * 
 */
package com.gpigc.core.storage;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;

public abstract class SystemDataGateway {
	
	private final List<ClientSystem> registeredSystems;
	public final String name;

	public SystemDataGateway(List<ClientSystem> registeredSystems){
		this.registeredSystems = registeredSystems;
		this.name = this.getClass().getSimpleName();
	}
	
	/**
	 * @param systemID A systemID
	 * @param sensorID The ID of the sensor you wish to read, if null all sensors will be returned
	 * @param numRecords The maximum number of records to return
	 * @return The numRecords most recent records associated with the given
	 * systemID
	 * @throws FailedToReadFromDatastoreException
	 * @throws SQLException 
	 */
	public abstract QueryResult readMostRecent(String systemID,String sensorID, int numRecords) 
			throws FailedToReadFromDatastoreException;
	
	/**
	 * @param systemID A systemID
	 * @param sensorID The ID of the sensor you wish to read, if null all sensors will be returned
	 * @param start The earliest point in the time period
	 * @param end The latest point in the time period
	 * @return All records for a given systemID within the given time period
	 * @throws FailedToReadFromDatastoreException
	 * @throws SQLException 
	 */
	public abstract QueryResult readBetween(String systemID, String sensorID, Date start, Date end) 
			throws FailedToReadFromDatastoreException;
	
	/**
	 * Writes the given data to the datastore
	 * 
	 * @param data The data to be written
	 * @throws FailedToWriteToDatastoreException When we couldn't write to the
	 * @throws SQLException When we couldn't write
	 */
	public abstract void write(EmitterSystemState data) throws FailedToWriteToDatastoreException;

	/**
	 * Writes a batch of data to the datastore
	 * 
	 * @param data The data to be written
	 * @throws FailedToWriteToDatastoreException When we couldn't write to the
	 * @throws SQLException When we fail to write to the datastore
	 */
	public abstract void write(List<EmitterSystemState> data) throws FailedToWriteToDatastoreException;

	/**
	 * Returns a list of systems which are registered to this datastore
	 * @return systems
	 */
	public List<ClientSystem> getAssociatedSystems() {
		return registeredSystems;
	}
}
