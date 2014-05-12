package uk.co.gpigc.emitter.earthquake;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.gpigc.proto.Protos.SystemData;

import uk.co.gpigc.emitter.DataCollector;

public class EarthquakeCollector implements DataCollector {

	private URL url;
	private long lastEarthquakeTime;

	public EarthquakeCollector() throws MalformedURLException {
		url = new URL(
				"http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
		lastEarthquakeTime = new Date().getTime();
	}

	@Override
	public List<SystemData> collect() {
		ArrayList<SystemData> dataList = new ArrayList<SystemData>();
		
		List<Earthquake> earthquakes = this.getNewEarthquakes();
		if (earthquakes.size() > 0) {
			for (Earthquake earthquake : earthquakes) {
				SystemData.Datum earthquakeDatum = SystemData.Datum.newBuilder()
						.setKey("EQ")
						.setValue(earthquake.toCsv())
						.build();
				SystemData data = SystemData.newBuilder().setSystemId("2")
						.setTimestamp(earthquake.getTime())
						.addDatum(earthquakeDatum)
						.build();
				dataList.add(data);
			}
		}
		
		return dataList;
	}

	public List<Earthquake> getNewEarthquakes() {
		System.out.println("Getting new earthquakes");

		List<Earthquake> earthquakes = new ArrayList<Earthquake>();

		HttpURLConnection connection = null;
		try {
			System.setProperty(
					"http.agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 Safari/537.36");

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();

			JSONObject featureCollection = (JSONObject) JSONValue
					.parse(response.toString());
			JSONArray features = (JSONArray) featureCollection.get("features");
			long maxTime = 0;
			for (Object featureObject : features) {
				JSONObject feature = (JSONObject) featureObject;

				JSONObject properties = (JSONObject) feature.get("properties");
				long time = (Long) properties.get("time");
				Object magObject = properties.get("mag");
				double mag;
				if (magObject instanceof Long) {
					mag = ((Long) magObject).doubleValue();
				} else {
					mag = (Double) magObject;
				}

				if (time > maxTime) {
					maxTime = time;
				}

				JSONObject geometry = (JSONObject) feature.get("geometry");
				JSONArray coordinates = (JSONArray) geometry.get("coordinates");
				double latitude = (Double) coordinates.get(1);
				double longitude = (Double) coordinates.get(0);

				if (time > lastEarthquakeTime) {
					Earthquake earthquake = new Earthquake(time, mag, latitude,
							longitude);
					earthquakes.add(earthquake);
					System.out.println(time);
				}
			}
			lastEarthquakeTime = maxTime;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return earthquakes;
	}

	public class Earthquake {
		private long time;
		private double magnitude;
		private double latitude;
		private double longitude;

		public Earthquake(long time, double magnitude, double latitude,
				double longitude) {
			this.time = time;
			this.magnitude = magnitude;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public long getTime() {
			return time;
		}

		public String toCsv() {
			return magnitude + "," + latitude + "," + longitude;
		}
	}

}
