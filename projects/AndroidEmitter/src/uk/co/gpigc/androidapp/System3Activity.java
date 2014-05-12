package uk.co.gpigc.androidapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;

import uk.co.gpigc.androidapp.faces.Face;
import uk.co.gpigc.androidapp.faces.FaceDetector;
import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class System3Activity extends Activity implements Camera.PreviewCallback, SurfaceHolder.Callback {

	private static int REAR_FACING_CAMERA = 0;

	private FrameLayout cameraContainer;
	private SurfaceView previewSurface;
	private Camera camera;
	private FaceDetector faceDetector = new FaceDetector();
	private Button saveButton;
	private boolean save;
	
	static {
		System.loadLibrary("opencv_java");
		System.loadLibrary("faces");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Foo", "onCreate");

		setContentView(R.layout.activity_system_face);
		cameraContainer = (FrameLayout) findViewById(R.id.frameLayoutLiveView);

		saveButton = (Button) findViewById(R.id.buttonSave);

		previewSurface = new SurfaceView(this);
		previewSurface.getHolder().addCallback(this);
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
		
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {});
		
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
		if (save) {
			save = false;

			Camera.Size previewSize = camera.getParameters().getPreviewSize();
			byte[][] rgbImage = NV21toRGB(data, previewSize.width,
					previewSize.height);

			Mat faceData = new Mat();
			computeFaceData(rgbImage, previewSize.width, previewSize.height,
					faceData.getNativeObjAddr());

			Log.d("Foo", this.getApplicationContext().getFilesDir().toString());
			File file = new File(this.getApplicationContext().getFilesDir(),
					"faces.csv");
			try {
				PrintStream fileStream = new PrintStream(new FileOutputStream(
						file, true));
				for (int i = 0; i < faceData.size().width; i++) {
					float[] faceDataValue = new float[1];
					faceData.get(0, i, faceDataValue);
					fileStream.print(faceDataValue[0]);
					if (i != faceData.size().width - 1) {
						fileStream.print(",");
					}
				}
				fileStream.println();
				fileStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			saveButton.setText("Save Data");
			saveButton.setEnabled(true);
		}
	}

	private static native void computeFaceData(byte[][] image, int width,
			int height, long outputMatAddress);

	private static byte[][] NV21toRGB(byte[] data, int width, int height) {
		int size = width * height;
		int offset = size;
		byte[][] pixels = new byte[size][3];
		int u, v, y1, y2, y3, y4;

		// i along Y and the final pixels
		// k along pixels U and V
		for (int i = 0, k = 0; i < size; i += 2, k += 2) {
			y1 = data[i] & 0xff;
			y2 = data[i + 1] & 0xff;
			y3 = data[width + i] & 0xff;
			y4 = data[width + i + 1] & 0xff;

			v = data[offset + k] & 0xff;
			u = data[offset + k + 1] & 0xff;
			v = v - 128;
			u = u - 128;

			pixels[i] = YUVtoRGB(y1, u, v);
			pixels[i + 1] = YUVtoRGB(y2, u, v);
			pixels[width + i] = YUVtoRGB(y3, u, v);
			pixels[width + i + 1] = YUVtoRGB(y4, u, v);

			if (i != 0 && (i + 2) % width == 0)
				i += width;
		}

		return pixels;
	}

	private static byte[] YUVtoRGB(int y, int u, int v) {
		byte[] pixelValue = new byte[3];
		int r = y + (int) (1.772f * v);
		int g = y - (int) (0.344f * v + 0.714f * u);
		int b = y + (int) (1.402f * u);
		pixelValue[0] = (byte) (r > 255 ? 255 : r < 0 ? 0 : r);
		pixelValue[1] = (byte) (g > 255 ? 255 : g < 0 ? 0 : g);
		pixelValue[2] = (byte) (b > 255 ? 255 : b < 0 ? 0 : b);
		return pixelValue;
	}
}
