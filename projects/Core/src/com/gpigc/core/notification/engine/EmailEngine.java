package com.gpigc.core.notification.engine;

import org.apache.commons.mail.*;

public class EmailEngine {

	/*public void main(String args[]) throws EmailException {
		SendEmail("sftrabbit@gmail.com", "GPIG-C test", "Hello!");
	}*/

	public void send(String recepient, String subject, String message) throws EmailException {
		SimpleEmail email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("gpigc.alerts@gmail.com", "59QEF-wKsaZUw^d"));
		email.setSSLOnConnect(true);
		email.setFrom("gpigc.alerts@gmail.com");
		email.setSubject(subject);
		email.setMsg(message);
		email.addTo(recepient);
		email.send();
	}
}
