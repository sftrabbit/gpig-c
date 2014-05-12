package uk.co.gpigc.emitterlauncher;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

public class OpenJarThread extends Thread {

	private Process proc;
	private String jarPath;
	private Button button;

	public OpenJarThread(String jarPath, Button button) {
		this.jarPath = jarPath;
		this.button = button;
	}

	@Override
	public void run() {
		try {
			proc = Runtime.getRuntime().exec("java -jar "+jarPath);
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


	public void stopRunning(){
		if(proc != null)
			proc.destroy();
	}

}
