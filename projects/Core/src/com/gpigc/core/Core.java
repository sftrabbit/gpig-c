package com.gpigc.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.datainput.DataInputServer;
import com.gpigc.core.notification.NotificationGenerator;
import com.gpigc.core.storage.GWTSystemDataGateway;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.core.view.CoreGUI;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.core.view.TextAreaStream;

public class Core {
	public final static String APPENGINE_SERVLET_URI = "http://gpigc-webapp.appspot.com/gpigc-webapp";

	/* Use this one if other instance is kaput */
	// public final static String APPENGINE_SERVLET_URI =
	// "http://gpigc-beta.appspot.com/gpigc-webapp";

	public static boolean running = false;

	private static CoreGUI view;

	public static void main(String args[]) throws ReflectiveOperationException,
			IOException {
		view = new CoreGUI();
		setUpGui(view);
		view.setVisible(true);
	}

	private static void setUpGui(final CoreGUI view) {
		// Redirect SysOut
		TextAreaStream textOut = new TextAreaStream(view.getConsoleArea());
		PrintStream outStream = new PrintStream(textOut, true);
		System.setOut(outStream);

		// Set Up The Action Listener
		view.getBtnStart().addActionListener(new StartButtonListener(view));
	}
	
	private static List<ClientSystem> getSystems() throws IOException {
		ConfigParser parser = new ConfigParser();
		return parser.parse(new File(view.getConfigFilePath()));
	}

	private static SystemDataGateway getDatastore(String servletUri) {
		try {
			return new GWTSystemDataGateway(new URI(servletUri));
		} catch (URISyntaxException e) {
			System.err.println("Could not initialise datastore: " + e.getMessage());
			return null;
		}
	}

	private static DataInputServer setUpControllers() throws IOException,
			ReflectiveOperationException {

		SystemDataGateway datastore = getDatastore(APPENGINE_SERVLET_URI);
		List<ClientSystem> systemsToMonitor = getSystems();

		if (datastore != null) {
			NotificationGenerator notificationGenerator = new NotificationGenerator(
					systemsToMonitor);
			// Create the other engines
			AnalysisController analysisController = new AnalysisController(
					datastore, notificationGenerator, systemsToMonitor);

			return new DataInputServer(analysisController, datastore);
		} else {
			StandardMessageGenerator.failedToSetup();
		}
		return null;
	}

	
	
	
	/**
	 * This is a pain
	 */
	static class StartButtonListener implements ActionListener {

		private DataInputServer dis;
		private CoreGUI view;

		public StartButtonListener(CoreGUI view) {
			this.view = view;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!running) {
				try {
					dis = setUpControllers();
					dis.start();
					view.getBtnStart().setText("Stop");
					view.getBtnSelectConfigFile().setEnabled(false);
					StandardMessageGenerator.coreRunning();
				} catch (IOException | ReflectiveOperationException e1) {
					StandardMessageGenerator.failedToSetup();
					e1.printStackTrace();
				}
				running = true;
			} else {
				dis.stopserver();
				view.getBtnStart().setText("Start");
				StandardMessageGenerator.coreStopped();
				running = false;
				view.getBtnSelectConfigFile().setEnabled(true);
			}
		}

	}
}
