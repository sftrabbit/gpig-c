package uk.co.gpigc.androidapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class BarWrapper extends SeekBar {

	private int lower;
	private int higher;

	public BarWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public BarWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BarWrapper(Context context) {
		super(context);
	}

	public void setValidBounds(int lower, int higher){
		this.lower =lower;
		this.higher = higher;
		
	}

	public int getLower() {
		return lower;
	}

	public int getHigher() {
		return higher;
	}

}
