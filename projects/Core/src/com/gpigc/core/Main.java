package com.gpigc.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;

import com.gpigc.core.view.CoreGUI;
import com.gpigc.core.view.StandardMessageGenerator;
import com.gpigc.core.view.TextAreaStream;

public class Main {

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
	
	/**
	 * This is a pain
	 */
	static class StartButtonListener implements ActionListener {

		private Core core;
		private CoreGUI view;

		public StartButtonListener(CoreGUI view) {
			this.view = view;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!running) {
				try {
					core = new Core(view.getConfigFilePath());
					core.getDataInputServer().start();
					view.getBtnStart().setText("Stop");
					view.getBtnSelectConfigFile().setEnabled(false);
					StandardMessageGenerator.coreRunning();
				} catch (IOException | ReflectiveOperationException e1) {
					StandardMessageGenerator.failedToSetup();
					e1.printStackTrace();
				}
				running = true;
			} else {
				core.getDataInputServer().stopserver();
				view.getBtnStart().setText("Start");
				StandardMessageGenerator.coreStopped();
				running = false;
				view.getBtnSelectConfigFile().setEnabled(true);
			}
		}

	}


}
