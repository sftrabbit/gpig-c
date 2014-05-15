package uk.co.gpigc.emitterlauncher;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

public class OpenJarThread extends Thread {

	private Process proc;
	private String jarPath;
	private Button button;
	private StyledText console;

	public OpenJarThread(String jarPath, Button button, StyledText console) {
		this.jarPath = jarPath;
		this.button = button;
		this.console = console;
	}

	@Override
	public void run() {
		try {
			proc = Runtime.getRuntime().exec("java -jar "+jarPath);
			redirectOutput(proc);
			proc.waitFor();
			stopRunning();
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					button.setSelection(false);
					EmitterShell.changeIcon(button);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void redirectOutput(final Process proc) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final Scanner s = new Scanner(proc.getInputStream());
				while(s.hasNextLine()){
					writeLine(s.nextLine());
				}
			}
		}).start();
	}

	protected void writeLine(final String nextLine) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
					console.append(nextLine);
					console.append("\n");
			}

		});
	}

	public void stopRunning(){
		if(proc != null)
			proc.destroy();
	}

}
