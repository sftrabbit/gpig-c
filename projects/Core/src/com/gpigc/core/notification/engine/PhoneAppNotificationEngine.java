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
					event.getData().put(Parameter.WIFI,ON);
					event.getData().put(Parameter.THREE,OFF);
					event.getData().put(Parameter.BLUE,OFF);
					event.getData().put(Parameter.GPS,OFF);
				}else{
					event.getData().put(Parameter.WIFI,ON);
					event.getData().put(Parameter.THREE,ON);
					event.getData().put(Parameter.BLUE,ON);
					event.getData().put(Parameter.GPS,ON);
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
