package com.gpigc.dataemitter.monitors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPMonitor {
	private URL url;

	public HTTPMonitor(URL url) {
		this.url = url;
	}

	public long getResponseTime() {
		System.out.println("Getting reponse time.");
		long start = System.currentTimeMillis();

		System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 Safari/537.36");

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);

			// Read to end of response, but discard
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			while ((rd.readLine()) != null) {}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		long stop = System.currentTimeMillis();

		return stop - start;
	}
}
