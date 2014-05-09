package com.gpigc.core.notification.engine;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.view.StandardMessageGenerator;

public class PhoneAppNotificationEngine extends NotificationEngine {
	
	private static final String OFF = "off";
	private static final String ON = "on";
	private static final String GPS = "GPS";
	private static final String BLUE = "BLUE";
	private static final String THREE = "THREE";
	private static final String WIFI = "WIFI";
	
	public static final int PORT = 8001;


	public PhoneAppNotificationEngine(List<ClientSystem> registeredSystems,
			int COOLDOWN) {
		super(registeredSystems, COOLDOWN);
	}

	@Override
	public boolean send(DataEvent event) {
		if(event.getSystem().getParameters().containsKey(Parameter.PHONE_IP)){
			String phoneIP = event.getSystem().getParameters().get(Parameter.PHONE_IP);
			try {
				if(Double.parseDouble(event.getData().get("Value")) ==1){
					event.getData().put(WIFI,ON);
					event.getData().put(THREE,OFF);
					event.getData().put(BLUE,OFF);
					event.getData().put(GPS,OFF);
				}else{
					event.getData().put(WIFI,ON);
					event.getData().put(THREE,ON);
					event.getData().put(BLUE,ON);
					event.getData().put(GPS,ON);
				}
				Socket s = new Socket(phoneIP, PORT);
				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject(event.getData());
				out.flush();
				s.close();
				StandardMessageGenerator.notificationGenerated(name, event.getSystem().getID());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			StandardMessageGenerator.phoneIPNotSpecified();
			return false;
		}
	}

}
