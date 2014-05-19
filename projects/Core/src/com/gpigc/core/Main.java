package com.gpigc.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

import com.gpigc.core.Config.ConfigException;
import com.gpigc.core.view.CoreShell;
import com.gpigc.core.view.StandardMessageGenerator;

public class Main {

	public static final String CONFIG_NAME = "gpigc";
	public static boolean running = false;
	private static CoreShell shell;
	private static Config config;

	public static void main(String args[]) throws Exception {
		setUpConfig();
		setUpServer();
		setUpGui();
	}
	
	private static void setUpConfig() {
		try {
			String defaultConfigDirectory = FileUtils
				.getExpandedFilePath("res/config");
			config = new Config(CONFIG_NAME, defaultConfigDirectory);
		} catch(ConfigException e) {
			System.err.println("Unable to initialise configuration directory");
			config = new Config();
		}
	}

	private static void setUpGui() {
		try {
			Display display = Display.getDefault();
			shell = new CoreShell(display, config);
			shell.setSize(700, 400);
			shell.setMinimumSize(400, 400);
			shell.open();
			shell.layout();

			// Redirect SysOut
			OutputStream out = new OutputStream() {
				@Override
				public void write(final int b) throws IOException {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							if (!shell.getConsoleTextView().isDisposed()) {
								shell.getConsoleTextView().append(
										Character.toString((char) b));
							}
						}
					});
				}
			};
			System.setOut(new PrintStream(out));

			// Set Up The Action Listener
			shell.getStartButton().addSelectionListener(
					new StartButtonListener(shell));

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setUpServer() throws Exception {
		Server server = new Server(65123);
		HandlerList handlers = new HandlerList();

		final URL warUrl = new File(FileUtils.getExpandedFilePath("res/server"))
				.getCanonicalFile().toURI().toURL();
		final String warUrlString = warUrl.toExternalForm();
		WebAppContext webApp = new WebAppContext(warUrlString, "/");

		handlers.setHandlers(new Handler[] { webApp, new DefaultHandler() });

		server.setHandler(handlers);

		server.start();
		// server.join();
	}

	/**
	 * This is a pain
	 */
	static class StartButtonListener implements SelectionListener {

		private Core core;
		private CoreShell shell;

		public StartButtonListener(CoreShell shell) {
			this.shell = shell;
		}

		public void run() {
			if (!running) {

				try {
					System.setProperty("gpigc.configfile",
							shell.getConfigFilePath());
					
					core = new Core(shell.getConfigFilePath(), config);
					core.getDataInputServer().start();
					shell.getConfigButton().setEnabled(false);
					StandardMessageGenerator.coreRunning();
					running = true;
				} catch (IOException | ReflectiveOperationException
						| InterruptedException e1) {
					StandardMessageGenerator.failedToSetup();
					e1.printStackTrace();
				}
			} else {
				core.getDataInputServer().stopserver();
				shell.getConfigButton().setEnabled(true);
				StandardMessageGenerator.coreStopped();
				running = false;
			}
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			run();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			run();
		}

	}

}
