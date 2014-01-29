/**
 * 
 */
package com.gpig.client;

import java.util.Date;
import java.util.List;

/**
 * The interface to any datastore
 * 
 * @author Tom Davies
 */
public interface SystemDataGateway {
	
	/**
	 * @param systemID A systemID
	 * @param numRecords The maximum number of records to return
	 * @return The numRecords most recent records associated with the given
	 * systemID
	 */
	public List<SystemData> readMostRecent(String systemID, int numRecords);
	
	/**
	 * @param systemID A systemID
	 * @param start The earliest point in the time period
	 * @param end The latest point in the time period
	 * @return All records for a given systemID within the given time period
	 */
	public List<SystemData> readBetween(String systemID, Date start, Date end);
	
	/**
	 * Writes the given data to the datastore
	 * 
	 * @param data The data to be written
	 * @throws FailedToWriteToDatastoreException When we couldn't write to the
	 * database
	 */
	public void write(SystemData data) throws FailedToWriteToDatastoreException;

}
