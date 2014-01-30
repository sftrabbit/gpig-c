/**
 * 
 */
package com.gpig.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.http.ParseException;

/**
 * The interface to any datastore
 * 
 * @author GPIGC
 */
public interface SystemDataGateway {
	
	/**
	 * @param systemID A systemID
	 * @param numRecords The maximum number of records to return
	 * @return The numRecords most recent records associated with the given
	 * systemID
	 * @throws FailedToReadFromDatastoreException 
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws URISyntaxException 
	 */
	public QueryResult readMostRecent(String systemID, int numRecords) 
			throws FailedToReadFromDatastoreException, URISyntaxException, ParseException, IOException;
	
	/**
	 * @param systemID A systemID
	 * @param start The earliest point in the time period
	 * @param end The latest point in the time period
	 * @return All records for a given systemID within the given time period
	 * @throws FailedToReadFromDatastoreException 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public QueryResult readBetween(String systemID, Date start, Date end) 
			throws FailedToReadFromDatastoreException, URISyntaxException, ParseException, IOException;
	
	/**
	 * Writes the given data to the datastore
	 * 
	 * @param data The data to be written
	 * @throws FailedToWriteToDatastoreException When we couldn't write to the
	 * database
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public void write(EmitterSystemState data) throws FailedToWriteToDatastoreException, UnsupportedEncodingException, IOException;

}
