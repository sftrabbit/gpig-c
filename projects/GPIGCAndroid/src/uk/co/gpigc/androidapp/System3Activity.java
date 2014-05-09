package uk.co.gpigc.androidapp;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class System3Activity extends Activity implements CvCameraViewListener2 {
	
	private static int REAR_FACING_CAMERA = 0;
	
	private FrameLayout mFrameLayoutCamera;
	private JavaCameraView cameraView;
	
	static {
	    startOpenCV();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_face);
		mFrameLayoutCamera = (FrameLayout) 
				findViewById(R.id.frameLayoutLiveView);
		cameraView = new JavaCameraView(
				getApplicationContext(), REAR_FACING_CAMERA);
		cameraView.setCvCameraViewListener(this);
		mFrameLayoutCamera.addView(cameraView);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		cameraView.enableView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		cameraView.disableView();
	}

	private static void startOpenCV() {
		if (!OpenCVLoader.initDebug()) {
	        // Handle initialization error
	    }
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Handle frames from camera
		// TODO Return image to display (i.e. we can add overlays etc)
		return null;
	}
}

