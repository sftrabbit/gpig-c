package com.gpigc.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;

import com.gpigc.core.view.CoreShell;
import com.gpigc.core.view.StandardMessageGenerator;

public class Main {

	public static boolean running = false;
	private static CoreShell shell;

	public static void main(String args[]) throws ReflectiveOperationException, IOException {
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
							shell.getConsoleTextView().append(Character.toString((char) b));
						}
					});
				}
			};
			System.setOut(new PrintStream(out));

			// Set Up The Action Listener
			shell.getStartButton().addSelectionListener(new StartButtonListener(shell));

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				} catch (IOException | ReflectiveOperationException | InterruptedException e1) {
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

}
