package uk.co.gpigc.androidapp;

import java.util.List;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import uk.co.gpigc.androidapp.faces.Face;
import uk.co.gpigc.androidapp.faces.FaceDetector;
import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class System3Activity extends Activity implements CvCameraViewListener2 {
	
	private static int REAR_FACING_CAMERA = 0;
	
	private FrameLayout mFrameLayoutCamera;
	private JavaCameraView cameraView;
	private FaceDetector faceDetector;
	
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
		faceDetector = new FaceDetector();
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
		// Handle frames from camera
		//List<Face> faces = FaceDetector.findFaces(inputFrame.gray());
		
		// Return image to display (we can add overlays etc)
		return inputFrame.rgba();
	}
}

