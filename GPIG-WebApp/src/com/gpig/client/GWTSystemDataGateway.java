/**
 * 
 */
package com.gpig.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import static com.gpig.server.DatabaseField.*;

/**
 * An implementation of SystemDataGateway that uses a Google App Engine
 * datastore
 * 
 * @author GPIGC
 */
public class GWTSystemDataGateway implements SystemDataGateway {

	/**
	 * The location of the servlet that handles the database
	 */
	private URI dbServletUri;

	/**
	 * @param dbServletUri
	 *            The location of the servlet that handles the database
	 */
	public GWTSystemDataGateway(URI dbServletUri) {
		this.dbServletUri = dbServletUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpig.client.SystemDataGateway#readMostRecent(java.lang.String,
	 * int)
	 */
	@Override
	public QueryResult readMostRecent(String systemID, int numRecords)
			throws FailedToReadFromDatastoreException, URISyntaxException,
			ParseException, IOException {

		// Set up the get
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(SYSTEM_ID.getKey(), systemID));
		params.add(new BasicNameValuePair(NUM_RECORDS.getKey(), numRecords + ""));
		URI uri = new URI(dbServletUri + "?"
				+ URLEncodedUtils.format(params, "utf-8"));
		HttpGet get = new HttpGet(uri);
		return getQueryResult(get);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gpig.client.SystemDataGateway#readBetween(java.lang.String,java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public QueryResult readBetween(String systemID, Date start, Date end)
			throws FailedToReadFromDatastoreException, URISyntaxException,
			ParseException, IOException {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(SYSTEM_ID.getKey(), systemID));
		params.add(new BasicNameValuePair(START_TIME.getKey(), start.getTime()
				+ ""));
		params.add(new BasicNameValuePair(END_TIME.getKey(), end.getTime() + ""));
		URI uri = new URI(dbServletUri + "?"
				+ URLEncodedUtils.format(params, "utf-8"));
		HttpGet get = new HttpGet(uri);

		return getQueryResult(get);
	}

	private QueryResult getQueryResult(HttpGet get)
			throws FailedToReadFromDatastoreException, ParseException,
			IOException {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(get);
		} catch (IOException e) {
			throw new FailedToReadFromDatastoreException(e.getMessage());
		}
		String responseBody = EntityUtils.toString(response.getEntity(),
				"UTF-8");
		System.out.println("Response Body: " + responseBody);

		return QueryResult.parseJSON(responseBody);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpig.client.SystemDataGateway#write(com.gpig.client.SystemData)
	 */
	@Override
	public void write(ReadSystemState data)
			throws FailedToWriteToDatastoreException,
			UnsupportedEncodingException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(dbServletUri);

		StringEntity entity = new StringEntity(data.toJSON());
		post.setEntity(entity);
		HttpResponse response;
		try {
			response = client.execute(post);
		} catch (IOException e) {
			throw new FailedToWriteToDatastoreException(e.getMessage());
		}
		if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_CREATED)
			throw new FailedToWriteToDatastoreException(
					"Failed to Write to DB, Response was "
							+ response.getStatusLine().getStatusCode());
	}
}
