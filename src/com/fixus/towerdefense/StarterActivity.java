package com.fixus.towerdefense;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fixus.td.sensors.Accelerometer;
import com.fixus.td.sensors.GPS;
import com.fixus.td.sensors.Orientation;
import com.fixus.td.sensors.OurSensorManager;
//github.com/dpacholczyk/pjatk-mgr.git
import com.fixus.towerdefense.analyze.FrameAnalyzer;

public class StarterActivity extends Activity implements CvCameraViewListener {

	private CameraBridgeViewBase openCvCameraView;
	private CascadeClassifier cascadeClassifier;
	private Mat grayscaleImage;
	private int absoluteObjectSize;
	
	public TextView headingValue;
	public TextView pitchValue;
	public TextView rollValue;
	public TextView latValue;
	public TextView lonValue;
	public TextView altValue;

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
			InputStream is = getResources().openRawResource(R.raw.cascade);
			File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
			File mCascadeFile = new File(cascadeDir, "cascade.xml");
			FileOutputStream os = new FileOutputStream(mCascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			// Load the cascade classifier
			cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
		} catch (Exception e) {
			Log.e("OpenCVActivity", "Error loading cascade", e);
		}

		// And we are ready to go
		openCvCameraView.enableView();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_starter);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//		openCvCameraView = new JavaCameraView(this, -1);
//		openCvCameraView.enableFpsMeter();
//		setContentView(openCvCameraView);
		
		setContentView(R.layout.activity_starter);
		openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.javaCamera);
		openCvCameraView.setVisibility(SurfaceView.VISIBLE);
		openCvCameraView.setCvCameraViewListener(this);
		
		pitchValue = (TextView) findViewById(R.id.pitchLabel);
		rollValue = (TextView) findViewById(R.id.rollLabel);
		headingValue = (TextView) findViewById(R.id.headingLabel);
		

//		Sensor.logData = false;
//		Orientation orientationSensor = new Orientation(this, Sensor.ORIENTATION_MODE, SENSOR_SERVICE, headingValue, pitchValue, rollValue);
//		orientationSensor.run();

		latValue = (TextView) findViewById(R.id.lat_label);
		lonValue = (TextView) findViewById(R.id.lon_label);
		altValue = (TextView) findViewById(R.id.alt_label);
		OurSensorManager test = new Orientation(this,pitchValue,rollValue,headingValue);
		
		//GPS gpsSensor = new GPS(this, ManagerEnum.GPS_MODE, LOCATION_SERVICE, latValue, lonValue, altValue);
		//gpsSensor.run();
		GPS gps = new GPS(this);
		if(gps.canGetLocation()){
			latValue.setText("" + gps.getLatitude()); // returns latitude
			lonValue.setText("" + gps.getLongitude()); // returns longitude
		} else{
			gps.showSettingsPopUp();
		}
		
		openCvCameraView.setOnTouchListener(new OnTouchListener() {
						
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
//				Log.d("TD", "x: " + x);
//				Log.d("TD", "y: " + y);

				// corners
				// order: top-left, top-right, bottom-right, bottom-left
				Point tl = FrameAnalyzer.tl;
				Point br = FrameAnalyzer.br;
				Point tr = FrameAnalyzer.tr;
				Point bl = FrameAnalyzer.bl;

				Point taped = new Point(x, y);

				// proste spradzenie czy punkt w prostok¹cie
				Log.d("TD", "tap x: " + x);
				Log.d("TD", "tap y: " + y);
				
				Log.d("TD", "dane punktow");
				Log.d("TD", "tl: " + tl.x + " | " + tl.y);
				Log.d("TD", "tr: " + tr.x + " | " + tr.y);
				Log.d("TD", "bl: " + bl.x + " | " + bl.y);
				Log.d("TD", "br: " + br.x + " | " + br.y);
				if (taped.x >= tl.x && taped.x <= tr.x && taped.y <= bl.y
						&& taped.y >= tl.y) {
					
					ImageView iv = (ImageView) findViewById(R.id.crossaim);
					Mat dst = new Mat();
					Mat img = FrameAnalyzer.frame;
					Imgproc.cvtColor(img, dst, Imgproc.COLOR_BGR2RGBA , 4);
					Mat dst2 = new Mat(dst, new Range(0, dst.rows()/2), new Range(0, dst.cols()/2));
					Bitmap bitmap = Bitmap.createBitmap(dst2.cols(), dst2.rows(),
					Bitmap.Config.ARGB_8888);
					Utils.matToBitmap(dst2, bitmap);
					iv.setImageBitmap(bitmap);
					
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
		absoluteObjectSize = (int) (height * 0.4);
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
			cascadeClassifier.detectMultiScale(grayscaleImage, objects, 1.1, 3, 2, 
					new Size(absoluteObjectSize, absoluteObjectSize), new Size());
		}

		Rect[] dataArray = objects.toArray();
		for (int i = 0; i < dataArray.length; i++) {
			Core.rectangle(aInputFrame, dataArray[i].tl(), dataArray[i].br(),
					new Scalar(0, 255, 0, 255), 3);			

			FrameAnalyzer.tl = dataArray[i].tl();
			FrameAnalyzer.br = dataArray[i].br();			
			FrameAnalyzer.tr = new Point(dataArray[i].br().x, dataArray[i].tl().y);
			FrameAnalyzer.bl = new Point(dataArray[i].tl().x, dataArray[i].br().y);
			FrameAnalyzer.frame = aInputFrame;
			
			
		}

		return aInputFrame;
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this,
				mLoaderCallback);
		
		
	}
	
	@Override
	public void onPause() {
		
	}
}
