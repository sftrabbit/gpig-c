/**
 * 
 */
package com.gpig.client;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * An implementation of SystemDataGateway that uses a Google App Engine 
 * datastore
 * 
 * @author Tom Davies
 */
public class GWTSystemDataGateway implements SystemDataGateway {
	
	/**
	 * The location of the servlet that handles the database
	 */
	private URI dbServletUri;

	/**
	 * @param dbServletUri The location of the servlet that handles the database
	 */
	public GWTSystemDataGateway(URI dbServletUri) {
		this.dbServletUri = dbServletUri;
	}

	/* (non-Javadoc)
	 * @see com.gpig.client.SystemDataGateway#readMostRecent(java.lang.String, int)
	 */
	@Override
	public List<SystemData> readMostRecent(String systemID, int numRecords) {
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(dbServletUri);
		
		
		
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
