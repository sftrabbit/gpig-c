package uk.co.gpigc.androidapp.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gpigc.proto.Protos.SystemData;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class DataPusher extends AsyncTask<Void, Void, Boolean> {

	public static final String CORE_IP_KEY = "CoreIP";
	protected static int CORE_PORT = 8000;
	private final String systemID;
	private final Map<String, String> data;
	private final Context context;
	private String coreIPAddress;
	private boolean silent;

	public DataPusher(Context context, String systemID,
			Map<String, String> data, String coreIPAddress, boolean silent) {
		this.context = context;
		this.systemID = systemID;
		this.data = data;
		this.coreIPAddress = coreIPAddress;
		this.silent = silent;
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		List<SystemData.Datum> systemDataDatums = new ArrayList<SystemData.Datum>();
		for (String key : data.keySet()) {
			systemDataDatums.add(SystemData.Datum.newBuilder().setKey(key)
					.setValue(data.get(key)).build());
		}
		try {
			final DataSender sender = new DataSender(coreIPAddress,
					DataPusher.CORE_PORT);
			sender.send(SystemData.newBuilder().setSystemId(systemID)
					.setTimestamp(new Date().getTime())
					.addAllDatum(systemDataDatums).build());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void onPostExecute(Boolean done) {
		if (!silent && done) {
			Toast.makeText(context, "Sent data", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "An error occurred. Check Core IP?", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
