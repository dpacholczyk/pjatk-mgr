package com.fixus.towerdefense;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import com.fixus.towerdefense.analyze.FrameAnalyzer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class StarterActivity extends Activity implements CvCameraViewListener {

	private CameraBridgeViewBase openCvCameraView;
	private CascadeClassifier cascadeClassifier;
	private Mat grayscaleImage;
	private int absoluteObjectSize;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				initializeOpenCVDependencies();
				break;
			default:
				super.onManagerConnected(status);
				break;
			}
		}
	};

	private void initializeOpenCVDependencies() {

		try {
			// Copy the resource into a temp file so OpenCV can load it
			InputStream is = getResources().openRawResource(
					R.raw.lbpcascade_frontalface);
			File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
			File mCascadeFile = new File(cascadeDir,
					"lbpcascade_frontalface.xml");
			FileOutputStream os = new FileOutputStream(mCascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			// Load the cascade classifier
			cascadeClassifier = new CascadeClassifier(
					mCascadeFile.getAbsolutePath());
		} catch (Exception e) {
			Log.e("OpenCVActivity", "Error loading cascade", e);
		}

		// And we are ready to go
		openCvCameraView.enableView();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		openCvCameraView = new JavaCameraView(this, -1);
		openCvCameraView.enableFpsMeter();
		setContentView(openCvCameraView);
		openCvCameraView.setCvCameraViewListener(this);
		openCvCameraView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				Log.d("TD", "x: " + x);
				Log.d("TD", "y: " + y);

				// corners
				// order: top-left, top-right, bottom-right, bottom-left
				Point tl = FrameAnalyzer.p1;
				Point br = FrameAnalyzer.p2;

				Point tr = new Point(br.x, tl.y);
				Point bl = new Point(tl.x, br.y);

				Point taped = new Point(x, y);

				// proste spradzenie czy punkt w prostok¹cie
				Log.d("TD", "dane punktow");
				Log.d("TD", "tl: " + tl.x + " | " + tl.y);
				Log.d("TD", "tr: " + tr.x + " | " + tr.y);
				Log.d("TD", "bl: " + bl.x + " | " + bl.y);
				Log.d("TD", "br: " + br.x + " | " + br.y);
				if (taped.x >= tl.x && taped.x <= tr.x && taped.y <= bl.y
						&& taped.y >= tl.y) {
					Log.d("TD", "w prostokacie");
				} else {
					Log.d("TD", "poza prostokatem");
				}

				return true;
			}
		});
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		grayscaleImage = new Mat(height, width, CvType.CV_8UC4);

		// The faces will be a 20% of the height of the screen
		absoluteObjectSize = (int) (height * 0.3);
	}

	@Override
	public void onCameraViewStopped() {
	}

	@Override
	public Mat onCameraFrame(Mat aInputFrame) {
		// return FrameAnalyzer.analyzeFrame(aInputFrame);
		// Create a grayscale image
		Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);

		MatOfRect objects = new MatOfRect();

		// Use the classifier to detect faces
		if (cascadeClassifier != null) {
			cascadeClassifier.detectMultiScale(grayscaleImage, objects, 1.1, 1,
					2, new Size(absoluteObjectSize, absoluteObjectSize),
					new Size());
		}

		Rect[] dataArray = objects.toArray();
		for (int i = 0; i < dataArray.length; i++) {
			Log.d("POSITION", "point 1: " + dataArray[i].tl());
			Log.d("POSITION", "point 2: " + dataArray[i].br());

			Core.rectangle(aInputFrame, dataArray[i].tl(), dataArray[i].br(),
					new Scalar(0, 255, 0, 255), 3);
		}

		return aInputFrame;
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this,
				mLoaderCallback);
	}
}
