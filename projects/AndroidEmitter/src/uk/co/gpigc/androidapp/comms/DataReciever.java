package uk.co.gpigc.androidapp.comms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import uk.co.gpigc.androidapp.PhoneSystemActivity;
import android.os.AsyncTask;
import android.util.Log;



public class DataReciever extends AsyncTask<Void,Void,Void> {


	protected static int CORE_PORT = 8001;
	private Map<String, String> recievedData;
	private PhoneSystemActivity activity;

	public DataReciever(PhoneSystemActivity activity){
		this.activity = activity;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("gpigc", "Doing ------------ gpigc");
		try {
			ServerSocket server = new ServerSocket(8001);
			Socket s = server.accept();
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			recievedData = (Map<String,String>) in.readObject();
			server.close();
		} catch (IOException e) {
			Log.d("gpigc", "Could not Update 1");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.d("gpigc", "Could not Update 2");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void params){
		if(recievedData != null){
			activity.update(recievedData);
			Log.d("gpigc", "Data Recieved");
		}else{
			Log.d("gpigc", "Data NOT Recieved");
		}
	}
}
