package com.gpigc.core.notification.engine;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.view.StandardMessageGenerator;

/**
 * SMS engine providing a way of sending a text message to the specified recipient
 * 
 * @author GPIGC
 */
public class SMSNotificationEngine extends NotificationEngine {


	public SMSNotificationEngine(List<ClientSystem> registeredSystems, final int COOL_DOWN_SECS) {
		super(registeredSystems, COOL_DOWN_SECS);
	}

	/* (non-Javadoc)
	 * @see com.gpigc.core.notification.NotificationEngine#send(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean send(DataEvent event) {
		if(!getRecentlySent()){
			try {
				String recipient = event.getData().get(Parameter.RECIPIENT);
				String subject = event.getData().get(Parameter.SUBJECT);
				String message = event.getData().get(Parameter.MESSAGE);

				if (recipient.substring(0, 1).equals("0")) 
					recipient = recipient.substring(1);
				int size = recipient.length();
				for (int i = 0; i < size; i++) {
					if (!Character.isDigit(recipient.charAt(i))) {
						return false;
					}
				}

				String body = subject + ": " + message;
				//if (body.length() > 160) body = body.substring(0, 160); // trim entire message to a single text message

				URL url = new URL("https://api.twilio.com/2010-04-01/Accounts/AC848eaa752727344e60afdbe24b96cb49/Messages.json");

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");

				String userpass = "AC848eaa752727344e60afdbe24b96cb49" + ":" + "c4bf474d1d3dc6b582dd81dfa8086a1f";
				String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
				conn.setRequestProperty("Authorization", basicAuth);

				String urlParameters = "From=%2B441743562709&To=%2B44" + recipient + "&Body=" + URLEncoder.encode(body, "UTF-8");
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();

				setRecentlySent();
				StandardMessageGenerator.notificationGenerated(name, event.getSystem().getID());
				/*
				System.out.println("Response Code : " + conn.getResponseCode());
				InputStream stream;
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				    stream = conn.getInputStream();
				} else {
					stream = conn.getErrorStream();
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(stream));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();		 
				System.out.println(response.toString());
				 */
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
