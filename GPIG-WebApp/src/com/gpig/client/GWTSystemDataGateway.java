/**
 * 
 */
package com.gpig.client;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.gpig.server.AppEngineServlet;

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
	public List<SystemData> readMostRecent(String systemID, int numRecords) 
			throws FailedToReadFromDatastoreException {
		
//		HttpClient client = new DefaultHttpClient();
//		HttpGet get = new HttpGet(dbServletUri);
//		HttpParams params = new BasicHttpParams();
//		
//		params.setParameter(AppEngineServlet.SYSTEM_ID_KEY, systemID);
//		params.setIntParameter(AppEngineServlet.NUM_RECORDS_KEY, numRecords);
//		get.setParams(params);
//		
//		HttpResponse response;
//		try {
//			response = client.execute(get);
//		} catch (IOException e) {
//			throw new FailedToReadFromDatastoreException(e.getMessage());
//		}
//		
//		System.out.println("Response: " + response);
//		
//		// TODO Return result by parsing response
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gpig.client.SystemDataGateway#readBetween(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<SystemData> readBetween(String systemID, Date start, Date end) 
			throws FailedToReadFromDatastoreException {
		
//		HttpClient client = new DefaultHttpClient();
//		HttpGet get = new HttpGet(dbServletUri);
//		HttpParams params = new BasicHttpParams();
//		
//		
//		params.setParameter(AppEngineServlet.SYSTEM_ID_KEY, systemID);
//		params.setParameter(
//				AppEngineServlet.START_TIME_KEY, 
//				DATE_FORMAT.format(start));
//		params.setParameter(
//				AppEngineServlet.END_TIME_KEY, 
//				DATE_FORMAT.format(end));
//		get.setParams(params);
//		
//		HttpResponse response;
//		try {
//			response = client.execute(get);
//		} catch (IOException e) {
//			throw new FailedToReadFromDatastoreException(e.getMessage());
//		}
//		
//		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gpig.client.SystemDataGateway#write(com.gpig.client.SystemData)
	 */
	@Override
	public void write(SystemData data) 
			throws FailedToWriteToDatastoreException  {
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(dbServletUri);
		
		// TODO Set data to write
		
		HttpResponse response;
		try {
			response = client.execute(post);
		} catch (IOException e) {
			throw new FailedToWriteToDatastoreException(e.getMessage());
		}
		
		// TODO Parse response
	}

}
