package uk.co.gpigc.androidapp.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gpigc.proto.Protos;
import com.gpigc.proto.Protos.SystemData;

import android.os.AsyncTask;
import android.util.Log;


public class DataPusher extends AsyncTask<Void,Void,Boolean> {


	protected static String CORE_HOST = "10.240.188.64"; //TODO me no understand uni network
	protected static int CORE_PORT = 8000;
	private final String systemID;
	private final Map<String, String> data;

	public DataPusher(String systemID, Map<String, String> data){
		this.systemID = systemID;
		this.data = data;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d("gpig","Protos:   " + Protos.class);
		Log.d("gpig","SystemDatum:   " + SystemData.class);
		Log.d("gpig","Datum:   " + SystemData.Datum.class);
		
		List<SystemData.Datum> systemDataDatums = new ArrayList<SystemData.Datum>();
		for(String key : data.keySet()){
			systemDataDatums.add(SystemData.Datum.newBuilder()
					.setKey(key)
					.setValue(data.get(key))
					.build());		
		}
		try {
			final DataSender sender = new DataSender(DataPusher.CORE_HOST, DataPusher.CORE_PORT);
			sender.send(SystemData.newBuilder().setSystemId(systemID)
					.setTimestamp(new Date().getTime()).addAllDatum(systemDataDatums).build());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}

	protected void onPostExecute(Boolean done){
		if(done)
			Log.d("gpig","Done Sending Data");
		else
			Log.d("gpig","Error Sending Data");

	}
}
