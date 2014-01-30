/**
 * 
 */
package com.gpig.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import static com.gpig.server.DatabaseField.*;
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
	public QueryResult readMostRecent(String systemID, int numRecords) 
			throws FailedToReadFromDatastoreException, URISyntaxException, 
			ParseException, IOException {
		
		//Set up the get
		HttpClient client 	= new DefaultHttpClient();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair( SYSTEM_ID.getKey(), systemID) );
		params.add(new BasicNameValuePair( NUM_RECORDS.getKey(), numRecords+""));
		URI uri = new URI(dbServletUri + "?" + URLEncodedUtils.format(params,"utf-8"));
		HttpGet get = new HttpGet(uri);
		HttpResponse response;
		try {
			response = client.execute(get);
		} catch (IOException e) {
			throw new FailedToReadFromDatastoreException(e.getMessage());
		}
		String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
		System.out.println("Response Body: " + responseBody);
		
		
		return QueryResult.parseJSON(responseBody);
	}

	/* (non-Javadoc)
	 * @see com.gpig.client.SystemDataGateway#readBetween(java.lang.String,java.util.Date, java.util.Date)
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
