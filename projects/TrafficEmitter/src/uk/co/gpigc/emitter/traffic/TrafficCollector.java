package uk.co.gpigc.emitter.traffic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gpigc.proto.Protos.SystemData;

import uk.co.gpigc.emitter.DataCollector;

public class TrafficCollector implements DataCollector {

	private URL url;
	private long lastEventTime;

	public static void main(String[] args) throws InterruptedException, MalformedURLException {
		TrafficCollector coll = new TrafficCollector();
		while (true) {
			coll.collect();
			Thread.sleep(60000);
		}
	}

	public TrafficCollector() throws MalformedURLException {
		url = new URL("http://hatrafficinfo.dft.gov.uk/feeds/datex/England/UnplannedEvent/content.xml");
		lastEventTime = new Date().getTime();
	}

	@Override
	public List<SystemData> collect() {
		ArrayList<SystemData> dataList = new ArrayList<SystemData>();

		List<TrafficEvent> trafficEvents = this.getNewEvents();
		if (trafficEvents.size() > 0) {
			System.out.println("New Incident");
			for (TrafficEvent trafficEvent : trafficEvents) {
				SystemData.Datum trafficDatum = SystemData.Datum.newBuilder()
						.setKey("Incident")
						.setValue(trafficEvent.toCsv())
						.build();
				SystemData data = SystemData.newBuilder().setSystemId("TrafficSystem")
						.setTimestamp(trafficEvent.getTime())
						.addDatum(trafficDatum)
						.build();
				dataList.add(data);
			}
		}

		return dataList;
	}

	private String getNodeValue(Element parent, String nodeName) {  
		return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();  
	}

	public List<TrafficEvent> getNewEvents() {
		System.out.println("Getting new traffic incidents");

		List<TrafficEvent> events = new ArrayList<TrafficEvent>();

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(url.openStream());

			NodeList situationRecords = doc.getElementsByTagName("situationRecord");

			long maxTime = 0;

			for (int i = 0; i < situationRecords.getLength(); i++) {

				Element item = (Element) situationRecords.item(i);

				String type = item.getAttribute("xsi:type");

				DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				Date dt = dtFormat.parse(getNodeValue(item, "situationRecordCreationTime").replaceAll("\\+0([0-9]){1}\\:00", "+0$100"));
				long time = dt.getTime();
				if (time > maxTime) {
					maxTime = time;
				}
				if(item.getElementsByTagName("framedPoint").item(0) != null){
					Element location = (Element) item.getElementsByTagName("framedPoint").item(0);

					Element coords = (Element) location.getElementsByTagName("pointCoordinates").item(0);
					double latitude = Double.parseDouble(getNodeValue(coords, "latitude"));
					double longitude = Double.parseDouble(getNodeValue(coords, "longitude"));
					String description = getNodeValue(location, "value");

					if (time > lastEventTime) {
						System.out.println("New Incident");
						TrafficEvent trafficEvent = new TrafficEvent(time, type, description, latitude, longitude);
						events.add(trafficEvent);
					}
				}
			}

			lastEventTime = maxTime;

		} catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
			e.printStackTrace();
		}

		return events;
	}

	public class TrafficEvent {
		private long time;
		private String type;
		private String description;
		private double latitude;
		private double longitude;

		public TrafficEvent(long time, String type, String description, double latitude,
				double longitude) {
			this.time = time;
			this.type = type;
			this.description = description;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public long getTime() {
			return time;
		}

		public String toCsv() {
			return "\"" + type + ": " + description + "\"," + latitude + "," + longitude;
		}
	}

}
