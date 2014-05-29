package uk.co.gpigc.androidapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FaceSystemActivity extends Activity implements Camera.PreviewCallback,
SurfaceHolder.Callback {

	private static int REAR_FACING_CAMERA = 0;

	private static final String SYSTEM_ID = "FaceSystem";
	private static final String FACE_ID = "Face";

	private FrameLayout cameraContainer;
	private SurfaceView previewSurface;
	private Camera camera;
	private Button saveButton;
	private boolean save;

	private long lastUpdate = 0;
	private static final long WAIT_MILLIS = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Foo", "onCreate");

		setContentView(R.layout.activity_system_face);
		cameraContainer = (FrameLayout) findViewById(R.id.frameLayoutLiveView);

		saveButton = (Button) findViewById(R.id.buttonSave);

		previewSurface = new SurfaceView(this);
		previewSurface.getHolder().addCallback(this);
		lastUpdate = System.currentTimeMillis() - WAIT_MILLIS;
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("Foo", "onPause");
		cameraContainer.removeView(previewSurface);
		camera.setPreviewCallback(null);
		camera.release();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("Foo", "onResume");
		camera = Camera.open();
		camera.setDisplayOrientation(90);
		camera.setPreviewCallback(this);

		cameraContainer.addView(previewSurface);
	}

	public void saveData(View v) {
		save = true;
		saveButton.setText("Saving...");
		saveButton.setEnabled(false);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("Foo", "surfaceCreated");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d("Foo", "surfaceChanged");
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			camera.setPreviewCallback(this);
		} catch (IOException e) {
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (System.currentTimeMillis()-lastUpdate < WAIT_MILLIS) {
			System.out.println("Too soon - skipping analysis");
			return;
		}
		System.out.println("Analysing");
		// Convert to JPG
		Size previewSize = camera.getParameters().getPreviewSize(); 
		YuvImage yuvimage=new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);
		byte[] jdata = baos.toByteArray();

		// Convert to Bitmap
		Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
		
		FaceDetector fd = new FaceDetector(bmp.getWidth(), bmp.getHeight(), 1);
		FaceDetector.Face[] faces = new FaceDetector.Face[1];
		fd.findFaces(bmp, faces);
		FaceDetector.Face face = faces[0];
		if (face != null) {
			PointF faceCentre = new PointF();
			face.getMidPoint(faceCentre);
			String msg = "Face at "+faceCentre+" with confidence "+face.confidence();
			System.out.println(msg);
			Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
			lastUpdate = System.currentTimeMillis();
		} else {
			System.out.println("No face found");
		}
	}
}
