/**
 * 
 */
package com.gpigc.dataabstractionlayer.client;

import java.io.IOException;
import java.io.StringWriter;
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import static com.gpigc.dataabstractionlayer.client.DataJSONAttribute.JSON_STATES;
import static com.gpigc.dataabstractionlayer.server.DatabaseField.*;

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
	public QueryResult readMostRecent(String systemID, String sensorID, int numRecords)
			throws FailedToReadFromDatastoreException {
		try {
			// Set up the get
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(SYSTEM_ID.getKey(), systemID));
			if(sensorID != null)
				params.add(new BasicNameValuePair(SENSOR_ID.getKey(), sensorID));
			
			params.add(new BasicNameValuePair(NUM_RECORDS.getKey(), numRecords + ""));
			URI uri = new URI(dbServletUri + "?"
					+ URLEncodedUtils.format(params, "utf-8"));
			HttpGet get = new HttpGet(uri);
			return getQueryResult(get);
		} catch (URISyntaxException | ParseException e) {
			throw new FailedToReadFromDatastoreException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gpig.client.SystemDataGateway#readBetween(java.lang.String,java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public QueryResult readBetween(String systemID, String sensorID, Date start, Date end)
			throws FailedToReadFromDatastoreException {
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(SYSTEM_ID.getKey(), systemID));
			params.add(new BasicNameValuePair(START_TIME.getKey(), start.getTime()
					+ ""));
			params.add(new BasicNameValuePair(END_TIME.getKey(), end.getTime() + ""));
			if(sensorID != null)
				params.add(new BasicNameValuePair(SENSOR_ID.getKey(), sensorID));
			
			URI uri = new URI(dbServletUri + "?"
					+ URLEncodedUtils.format(params, "utf-8"));
			HttpGet get = new HttpGet(uri);

			return getQueryResult(get);
		} catch (URISyntaxException | ParseException e) {
			throw new FailedToReadFromDatastoreException(e.getMessage());
		}
	}

	private QueryResult getQueryResult(HttpGet get)
			throws FailedToReadFromDatastoreException {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			response = client.execute(get);
			String responseBody = EntityUtils.toString(response.getEntity(),
					"UTF-8");
			return QueryResult.parseJSON(responseBody);
		} catch (ParseException | IOException e) {
			throw new FailedToReadFromDatastoreException(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gpig.client.SystemDataGateway#write(com.gpig.client.EmitterSystemState
	 * )
	 */
	@Override
	public void write(EmitterSystemState data)
			throws FailedToWriteToDatastoreException {
		List<EmitterSystemState> dataArray = new ArrayList<EmitterSystemState>();
		dataArray.add(data);
		write(dataArray);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gpig.client.SystemDataGateway#write(com.gpig.client.EmitterSystemState
	 * )
	 */
	public void write(List<EmitterSystemState> data)
			throws FailedToWriteToDatastoreException {
		try {
			writeJSON(createJSONArray(data));
		} catch (IOException e) {
			throw new FailedToWriteToDatastoreException(e.getMessage());
		}
	}

	/**
	 * @param data A list of emitter states
	 * @return A JSON list of emitter states
	 * @throws IOException
	 */
	public String createJSONArray(List<EmitterSystemState> data) throws IOException {
		StringWriter writer = new StringWriter();
		JsonFactory factory = new JsonFactory();
		JsonGenerator gen = factory.createGenerator(writer);
		gen.writeStartObject();
		gen.writeArrayFieldStart(JSON_STATES.getKey());
		for (EmitterSystemState state : data) {
			gen.writeRawValue(state.toJSON());
		}
		gen.writeEndArray();
		gen.writeEndObject();
		gen.close();
		return writer.toString();
	}

	private void writeJSON(String json)
			throws FailedToWriteToDatastoreException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(dbServletUri);
		StringEntity entity = new StringEntity(json);
		post.setEntity(entity);
		HttpResponse response;
		response = client.execute(post);
		if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_CREATED)
			throw new FailedToWriteToDatastoreException(
					"Failed to Write to DB, Response was "
							+ response.getStatusLine().getStatusCode());
	}

}
