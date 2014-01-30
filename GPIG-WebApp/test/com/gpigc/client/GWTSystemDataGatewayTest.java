package com.gpigc.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.ParseException;
import org.junit.Test;

import com.gpig.client.FailedToReadFromDatastoreException;
import com.gpig.client.GWTSystemDataGateway;

public class GWTSystemDataGatewayTest {

	
	
	private static final String testSystemID = "TestSystemID";
	
	@Test
	public void test() throws FailedToReadFromDatastoreException, URISyntaxException, ParseException, IOException {
		GWTSystemDataGateway gateway = new GWTSystemDataGateway(new URI("http://gpigc-webapp.appspot.com/gpigc-webapp"));
		gateway.readMostRecent(testSystemID, 10);
	}

}
