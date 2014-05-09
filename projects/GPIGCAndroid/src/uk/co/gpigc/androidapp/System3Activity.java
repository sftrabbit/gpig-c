package uk.co.gpigc.androidapp;

import org.opencv.android.OpenCVLoader;

import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.os.Bundle;

public class System3Activity extends Activity {
	
	static {
	    startOpenCV();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_face);
	}

	private static void startOpenCV() {
		if (!OpenCVLoader.initDebug()) {
	        // Handle initialization error
	    }
	}
}

