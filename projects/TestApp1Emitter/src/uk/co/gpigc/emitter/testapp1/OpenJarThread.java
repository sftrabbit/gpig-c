package uk.co.gpigc.emitter.testapp1;

public class OpenJarThread extends Thread {

	private Process proc;
	private String jarPath;

	public OpenJarThread(String jarPath) {
		this.jarPath = jarPath;
	}

	@Override
	public void run() {
		try {
			proc = Runtime.getRuntime().exec("java -jar "+jarPath);
			System.out.println("First Test App is running from: "+ jarPath);
			proc.waitFor();
			stopRunning();
			System.out.println("First Test App Stopped");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopRunning(){
		if(proc != null)
			proc.destroy();
	}

}
