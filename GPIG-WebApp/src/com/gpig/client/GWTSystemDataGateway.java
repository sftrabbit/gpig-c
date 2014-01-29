/**
 * 
 */
package com.gpig.client;

import java.util.Date;
import java.util.List;

/**
 * An implementation of SystemDataGateway that uses a Google App Engine 
 * datastore
 * 
 * @author Tom Davies
 */
public class GWTSystemDataGateway implements SystemDataGateway {

	/* (non-Javadoc)
	 * @see com.gpig.client.SystemDataGateway#readMostRecent(java.lang.String, int)
	 */
	@Override
	public List<SystemData> readMostRecent(String systemID, int numRecords) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gpig.client.SystemDataGateway#readBetween(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<SystemData> readBetween(String systemID, Date start, Date end) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gpig.client.SystemDataGateway#write(com.gpig.client.SystemData)
	 */
	@Override
	public void write(SystemData data) {
		// TODO Auto-generated method stub

	}

}
