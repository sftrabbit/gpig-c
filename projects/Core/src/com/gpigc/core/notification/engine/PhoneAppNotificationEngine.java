package com.gpigc.core.notification.engine;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.Parameter;
import com.gpigc.core.event.DataEvent;
import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.view.StandardMessageGenerator;

public class PhoneAppNotificationEngine extends NotificationEngine {
	
	private static final String OFF = "off";
	private static final String ON = "on";
	
	public static final int PORT = 8001;


	public PhoneAppNotificationEngine(List<ClientSystem> registeredSystems,
			int COOLDOWN) {
		super(registeredSystems, COOLDOWN);
	}

	@Override
	public boolean send(DataEvent event) {
		if(event.getSystem().getParameters().containsKey(Parameter.PHONE_IP)){
			String phoneIP = event.getSystem().getParameters().get(Parameter.PHONE_IP);
			Map<String,String> phoneData = new HashMap<>();
			try {
				if(Double.parseDouble(event.getData().get(Parameter.VALUE)) ==1){
					phoneData.put(Parameter.WIFI.toString(),ON);
					phoneData.put(Parameter.THREE.toString(),OFF);
					phoneData.put(Parameter.BLUE.toString(),OFF);
					phoneData.put(Parameter.GPS.toString(),OFF);
				}else{
					phoneData.put(Parameter.WIFI.toString(),ON);
					phoneData.put(Parameter.THREE.toString(),ON);
					phoneData.put(Parameter.BLUE.toString(),ON);
					phoneData.put(Parameter.GPS.toString(),ON);
				}
				Socket s = new Socket(phoneIP, PORT);
				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject(phoneData);
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
