package uk.co.gpigc.androidapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

		//Setup SeekBars
		final SeekBar oilSeek = (SeekBar) findViewById(R.id.oilSeek);
		final SeekBar revsSeek = (SeekBar) findViewById(R.id.revsSeek);
		final SeekBar tempSeek = (SeekBar) findViewById(R.id.engineTempSeek);
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
				DataPusher pusher = new DataPusher(SYSTEM_ID, data);
				pusher.execute();
			}
		});
		
	}

	private void updateText(TextView view, String text){
		view.setText(text);
	}


	class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

		private final TextView view;
		private String nameText;
		private String units;

		public SeekBarListener(SeekBar bar, TextView view, String nameText, String units){
			this.view = view;
			this.nameText = nameText;
			this.units = units;
			updateText(view, nameText + bar.getProgress() + " ("+units+")");
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			updateText(view, nameText + seekBar.getProgress() + " ("+units+")");
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

