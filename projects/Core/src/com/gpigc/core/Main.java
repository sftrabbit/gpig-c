package com.gpigc.core;

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
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.WebAppContext;

import com.gpigc.core.view.CoreShell;
import com.gpigc.core.view.StandardMessageGenerator;

public class Main {

	public static boolean running = false;
	private static CoreShell shell;

	public static void main(String args[]) throws Exception {
		setUpServer();
		setUpGui();
	}

	private static void setUpGui() {
		try {
			Display display = Display.getDefault();
			shell = new CoreShell(display);
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
		org.eclipse.jetty.util.log.Log.setLog(new NullLogger()); // this seemingly does nothing, when it should do something

		Server server = new Server(80);
		HandlerList handlers = new HandlerList();

		final URL warUrl = new URL(new URL("file:"), "./bin/server");
		final String warUrlString = warUrl.toExternalForm();
		WebAppContext webApp = new WebAppContext(warUrlString, "/");

		handlers.setHandlers(new Handler[] { webApp, new DefaultHandler() });

		server.setHandler(handlers);

		server.start();
		//server.join();
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
					core = new Core(shell.getConfigFilePath());
					core.getDataInputServer().start();
					shell.getConfigButton().setEnabled(false);
					StandardMessageGenerator.coreRunning();
				} catch (IOException | ReflectiveOperationException
						| InterruptedException e1) {
					StandardMessageGenerator.failedToSetup();
					e1.printStackTrace();
				}
				running = true;
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

	// NullLogger
	static class NullLogger implements Logger {
	    @Override public String getName() { return "no"; }
	    @Override public void warn(String msg, Object... args) { }
	    @Override public void warn(Throwable thrown) { }
	    @Override public void warn(String msg, Throwable thrown) { }
	    @Override public void info(String msg, Object... args) { }
	    @Override public void info(Throwable thrown) { }
	    @Override public void info(String msg, Throwable thrown) { }
	    @Override public boolean isDebugEnabled() { return false; }
	    @Override public void setDebugEnabled(boolean enabled) { }
	    @Override public void debug(String msg, Object... args) { }
	    @Override public void debug(Throwable thrown) { }
	    @Override public void debug(String msg, Throwable thrown) { }
	    @Override public Logger getLogger(String name) { return this; }
	    @Override public void ignore(Throwable ignored) { }
		@Override public void debug(String arg0, long arg1) { }
	}

}
