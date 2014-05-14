package uk.co.gpigc.emitter.responsetime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gpigc.proto.Protos.SystemData;

import uk.co.gpigc.emitter.DataCollector;

public class ResponseTimeCollector implements DataCollector {
	protected URL url;

	public ResponseTimeCollector() {
		try {
			url = new URL("https://www.thalesgroup.com/en");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<SystemData> collect() throws Exception {
		SystemData data = SystemData
				.newBuilder()
				.setSystemId("HTTPResponseTime")
				.setTimestamp(new Date().getTime())
				.addDatum(
						SystemData.Datum.newBuilder().setKey("ResponseTime")
								.setValue(String.valueOf(getResponseTime()))
								.build()).build();

		ArrayList<SystemData> dataList = new ArrayList<SystemData>();
		dataList.add(data);
		return dataList;
	}

	private long getResponseTime() {
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
