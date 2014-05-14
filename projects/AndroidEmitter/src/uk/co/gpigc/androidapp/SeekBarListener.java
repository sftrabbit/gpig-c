package uk.co.gpigc.androidapp;

import uk.co.gpigc.gpigcandroid.R;
import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;


class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

	private final TextView view;
	private final String nameText;
	private final String units;
	private final BarWrapper bar;
	private Context context;

	public SeekBarListener(BarWrapper bar, TextView view, String nameText, String units, Context context){
		this.bar = bar;
		this.view = view;
		this.nameText = nameText;
		this.units = units;
		this.context = context;
		updateText(view, nameText + bar.getProgress() + " ("+units+")");
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		updateText(view, nameText + bar.getProgress() + " ("+units+")");
		if(progress < bar.getLower() || progress > bar.getHigher()){
			view.setTextColor(context.getResources().getColor(R.color.purple));
		}else{
			view.setTextColor(context.getResources().getColor(R.color.pink));
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
	
	private void updateText(TextView view, String text){
		view.setText(text);
	}
}
