package uk.co.gpigc.androidapp.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gpigc.proto.Protos;
import com.gpigc.proto.Protos.SystemData;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class DataPusher extends AsyncTask<Void,Void,Boolean> {


	protected static String CORE_HOST = "192.168.44.179"; //TODO whatever ip
	protected static int CORE_PORT = 8000;
	private final String systemID;
	private final Map<String, String> data;
	private final Context context;

	public DataPusher(Context context, String systemID, Map<String, String> data){
		this.context = context;
		this.systemID = systemID;
		this.data = data;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
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
			Toast.makeText(context, "Pushed", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, "An Error Occurred", Toast.LENGTH_LONG).show();
	}
}
