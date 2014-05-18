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
			System.out.println("Jar has started: " + jarPath);
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopRunning(){
		if(proc != null)
			proc.destroy();
		System.out.println("Jar has stopped: " + jarPath);
	}

}
