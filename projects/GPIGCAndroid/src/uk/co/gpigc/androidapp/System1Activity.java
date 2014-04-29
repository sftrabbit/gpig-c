package uk.co.gpigc.androidapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import uk.co.gpigc.androidapp.comms.DataPusher;
import uk.co.gpigc.androidapp.comms.DataSender;
import uk.co.gpigc.gpigcandroid.R;
import uk.co.gpigc.gpigcandroid.R.id;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class System1Activity extends Activity {

	private static final String OIL_SENSOR_TEXT = "Oil Pressure: ";
	private static final String REVS_SENSOR_TEXT = "Revs: ";
	private static final String ENGINE_SENSOR_TEXT = "Engine Temp: ";
	private static final String SYSTEM_ID = "CarSystem";
	private static final String OIL_SENSOR_ID = "OIL";
	private static final String REVS_SENSOR_ID = "REVS";
	private static final String ENGINE_SENSOR_ID = "TEMP";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system1);

		//Get Text Views
		final TextView oilTextView = (TextView) findViewById(R.id.oilTextView);
		final TextView revsTextView = (TextView) findViewById(R.id.revsTextView);
		final TextView tempTextView = (TextView) findViewById(R.id.engineTextView);

		setUpSeekBars(oilTextView,revsTextView,tempTextView);
	}

	private void setUpSeekBars(TextView oilTextView, TextView revsTextView,
			TextView tempTextView) {
		//Setup SeekBars
		final BarWrapper oilSeek = (BarWrapper) findViewById(R.id.oilSeek);
		oilSeek.setValidBounds(30, 70);
		final BarWrapper  revsSeek = (BarWrapper) findViewById(R.id.revsSeek);
		revsSeek.setValidBounds(800, 6000);
		final BarWrapper tempSeek = (BarWrapper) findViewById(R.id.engineTempSeek);
		tempSeek.setValidBounds(40, 100);

		revsSeek.setOnSeekBarChangeListener(new SeekBarListener(revsSeek,revsTextView, REVS_SENSOR_TEXT, "RPM"));
		oilSeek.setOnSeekBarChangeListener(new SeekBarListener(oilSeek,oilTextView, OIL_SENSOR_TEXT, "PSI"));
		tempSeek.setOnSeekBarChangeListener(new SeekBarListener(tempSeek,tempTextView, ENGINE_SENSOR_TEXT, "\u00b0C"));

		//Button
		final Button pushButton = (Button) findViewById(R.id.pushButton);
		pushButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String,String> data = new HashMap<String, String>();
				data.put(OIL_SENSOR_ID, oilSeek.getProgress()+"");
				data.put(REVS_SENSOR_ID, revsSeek.getProgress()+"");
				data.put(ENGINE_SENSOR_ID, tempSeek.getProgress()+"");
				DataPusher pusher = new DataPusher(getApplicationContext(), SYSTEM_ID, data);
				pusher.execute();
			}
		});


	}

	private void updateText(TextView view, String text){
		view.setText(text);
	}


	class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

		private final TextView view;
		private final String nameText;
		private final String units;
		private final BarWrapper bar;

		public SeekBarListener(BarWrapper bar, TextView view, String nameText, String units){
			this.bar = bar;
			this.view = view;
			this.nameText = nameText;
			this.units = units;
			updateText(view, nameText + bar.getProgress() + " ("+units+")");
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			updateText(view, nameText + bar.getProgress() + " ("+units+")");
			if(progress < bar.getLower() || progress > bar.getHigher()){
				view.setTextColor(getResources().getColor(R.color.purple));
			}else{
				view.setTextColor(getResources().getColor(R.color.pink));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			updateText(view, nameText + seekBar.getProgress() + " ("+units+")");
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			updateText(view, nameText + seekBar.getProgress() + " ("+units+")");
		}

	}
}

