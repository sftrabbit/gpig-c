package uk.co.gpigc.androidapp;


import java.util.HashMap;
import java.util.Map;

import uk.co.gpigc.androidapp.comms.DataPusher;
import uk.co.gpigc.androidapp.comms.DataReciever;
import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class PhoneSystemActivity extends Activity {

	private static final String OFF = "off";
	private static final String SYSTEM_ID = "PhoneSystem";
	private static final String WIFI_ID = "WIFI";
	private static final String GPS_ID = "GPS";
	private static final String BATTERY_ID = "BAT";
	private static final String BLUETOOTH_ID = "BLUE";
	private static final String THREEG_ID = "THREE";
	private static final String DESIRED_ID = "DES";
	private static final String BATTERY_TEXT = "Current Battery Level: ";
	private static final String DESIRED_TEXT = "Desired Battery Life: ";

	private CheckBox wifiCheckBox;
	private CheckBox gpsCheckBox;
	private CheckBox threeGCheckBox;
	private CheckBox bluetoothCheckBox;


	private static final int WIFI_VALUE =6;
	private static final int GPS_VALUE =10;
	private static final int THREEG_VALUE =7;
	private static final int BLUETOOTH_VALUE =9;

	private int currentWifiValue = WIFI_VALUE;
	private int currentGpsValue = GPS_VALUE;
	private int currentThreegValue = THREEG_VALUE;
	private int currentBluetoothValue = BLUETOOTH_VALUE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system2);

		wifiCheckBox = (CheckBox) findViewById(R.id.wifi);
		gpsCheckBox = (CheckBox) findViewById(R.id.gps);
		threeGCheckBox = (CheckBox) findViewById(R.id.threeG);
		bluetoothCheckBox = (CheckBox) findViewById(R.id.bluetooth);

		TextView batteryView = (TextView) findViewById(R.id.batteryTextView);
		TextView desiredView = (TextView) findViewById(R.id.desiredTextView);

		final BarWrapper batterySeek = (BarWrapper) findViewById(R.id.batterySeek);
		batterySeek.setOnSeekBarChangeListener(new SeekBarListener(batterySeek,
				batteryView, BATTERY_TEXT, "%", this));
		batterySeek.setProgress(40);

		final BarWrapper desiredSeek = (BarWrapper) findViewById(R.id.desiredSeek);
		desiredSeek.setOnSeekBarChangeListener(new SeekBarListener(desiredSeek,
				desiredView, DESIRED_TEXT, "Hrs", this));
		desiredSeek.setProgress(5);

		Button pushButton = (Button) findViewById(R.id.pushButton);
		pushButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String, String> data = new HashMap<String, String>();
				data.put(WIFI_ID, currentWifiValue+"");
				data.put(GPS_ID, currentGpsValue+"");
				data.put(THREEG_ID, currentThreegValue+"");
				data.put(BLUETOOTH_ID, currentBluetoothValue+"");
				data.put(BATTERY_ID, batterySeek.getProgress() + "");
				data.put(DESIRED_ID, desiredSeek.getProgress() + "");
				DataPusher pusher = new DataPusher(getApplicationContext(),
						SYSTEM_ID, data,getIntent().getStringExtra(DataPusher.CORE_IP_KEY));
				pusher.execute();
				new DataReciever(PhoneSystemActivity.this).execute();
			}
		});

	}

	public void update(Map<String, String> recievedData) {
		Log.d("GPIG", recievedData.toString());
		if(recievedData.get(WIFI_ID).equalsIgnoreCase(OFF)){
			wifiCheckBox.setChecked(false);
			currentWifiValue = 0;
		}else{
			wifiCheckBox.setChecked(true);
			currentWifiValue = WIFI_VALUE;
		}

		if(recievedData.get(THREEG_ID).equalsIgnoreCase(OFF)){
			threeGCheckBox.setChecked(false);
			currentThreegValue = 0;
		}else{
			threeGCheckBox.setChecked(true);
			currentThreegValue = THREEG_VALUE;
		}		
		if(recievedData.get(BLUETOOTH_ID).equalsIgnoreCase(OFF)){
			bluetoothCheckBox.setChecked(false);
			currentBluetoothValue = 0;
		}else{
			bluetoothCheckBox.setChecked(true);
			currentBluetoothValue = BLUETOOTH_VALUE;
		}

		if(recievedData.get(GPS_ID).equalsIgnoreCase(OFF)){
			gpsCheckBox.setChecked(false);
			currentGpsValue = 0;
		}else{
			gpsCheckBox.setChecked(true);
			currentGpsValue = GPS_VALUE;
		}
	}

}
