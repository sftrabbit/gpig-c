package com.gpigc.dataemitter.monitors;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeMonitor {

	private URL url;

	public EarthquakeMonitor() throws MalformedURLException {
		url = new URL(
				"http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
	}

	public List<Earthquake> getNewEarthquakes() {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return new ArrayList<Earthquake>();
	}

	public class Earthquake {

	}

}