package com.gpigc.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.gpigc.dataabstractionlayer.client.GWTSystemDataGateway;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.datainput.DataInputServer;
import com.gpigc.core.notification.NotificationGenerator;
import com.gpigc.core.view.CoreGUI;
import com.gpigc.core.view.TextAreaStream;

public class Core {
	public final static String APPENGINE_SERVLET_URI = "http://gpigc-webapp.appspot.com/gpigc-webapp";
	private static final String CONFIG_FILE_PATH = "config/RegisteredSystems.config";

	/* Use this one if other instance is kaput */
	// public final static String APPENGINE_SERVLET_URI =
	// "http://gpigc-beta.appspot.com/gpigc-webapp";

	public static boolean running = false;

	public static void main(String args[]) throws ReflectiveOperationException,
			IOException {

		SystemDataGateway datastore = getDatastore(APPENGINE_SERVLET_URI);

		List<ClientSystem> systemsToMonitor = getSystems();
		if (datastore != null) {
			NotificationGenerator notificationGenerator;
			notificationGenerator = new NotificationGenerator(systemsToMonitor);
			// Create the other engines
			AnalysisController analysisController = new AnalysisController(
					datastore, notificationGenerator, systemsToMonitor);
			final CoreGUI view = new CoreGUI();
			setUpGui(view, analysisController, datastore);
			view.setVisible(true);
		} else {
			// Abort
			System.exit(0);
		}
	}

	private static void setUpGui(final CoreGUI view,
			AnalysisController analysisController, SystemDataGateway datastore) {
		//Redirect SysOut
		TextAreaStream textOut = new TextAreaStream(view.getConsoleArea());
		PrintStream outStream = new PrintStream(textOut, true);
		System.setOut(outStream);
		//Set Up The Action Listener
		view.getBtnStart().addActionListener(
				new StartButtonListener(view, analysisController, datastore));
	}

	
	private static List<ClientSystem> getSystems() throws IOException {
		ConfigParser parser = new ConfigParser();
		return parser.parse(new File(CONFIG_FILE_PATH));
	}
	
	/**
	 * Get a GWT datastore;
	 * @param servletUri
	 * @return
	 */
	private static SystemDataGateway getDatastore(String servletUri) {
		try {
			return new GWTSystemDataGateway(new URI(servletUri));
		} catch (URISyntaxException e) {
			System.err.println("Could not initialise datastore: "
					+ e.getMessage());
			return null;
		}
	}

	
	/**
	 * This is a pain
	 */
	static class StartButtonListener implements ActionListener {

		private DataInputServer dis;
		private CoreGUI view;
		private AnalysisController analysisController;
		private SystemDataGateway datastore;

		public StartButtonListener(CoreGUI view,
				AnalysisController analysisController,
				SystemDataGateway datastore) {
			this.view = view;
			this.analysisController = analysisController;
			this.datastore = datastore;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!running) {
				dis = new DataInputServer(analysisController, datastore);
				dis.start();
				view.getBtnStart().setText("Stop");
				System.out.println("System Running");
				running = true;
			} else {
				dis.stopserver();
				view.getBtnStart().setText("Start");
				System.out.println("System Stopped");
				running = false;
			}
		}

	}
}
