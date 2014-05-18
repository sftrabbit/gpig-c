package uk.co.gpigc.emitter.testapp2;

import java.io.File;

public class CommandThread extends Thread {

	private Process proc;
	private String command;

	public CommandThread(String command, boolean setExecutable) {
		this.command = command;
		
		if (setExecutable) {
			File executableFile = new File(command);
			executableFile.setExecutable(true);
		}
	}

	@Override
	public void run() {
		try {
			proc = Runtime.getRuntime().exec(this.command);
			System.out.println("Running command: " + this.command);
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopRunning(){
		if(proc != null)
			proc.destroy();
		System.out.println("Command stopped");
	}

}
