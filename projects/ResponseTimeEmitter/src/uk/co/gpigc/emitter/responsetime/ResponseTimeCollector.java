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

public class ResponseTimeCollector extends DataCollector {
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
		System.out.println("Getting reponse time");
		long duration = -1;

		System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 Safari/537.36");

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);

			// Time how long it takes to open the response handle. As we don't care about connection set-up time (which is language/library dependent), this is a reasonable metric to use.
                        long start = System.currentTimeMillis();
			InputStream is = connection.getInputStream();
                        duration = System.currentTimeMillis() - start;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return duration;
	}

}
