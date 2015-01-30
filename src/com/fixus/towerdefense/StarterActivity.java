package com.fixus.towerdefense;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
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
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import rajawali.RajawaliActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fixus.td.sensors.Accelerometer;
import com.fixus.td.sensors.GPS;
import com.fixus.td.sensors.Orientation;
import com.fixus.td.sensors.OurSensorManager;
//github.com/dpacholczyk/pjatk-mgr.git
import com.fixus.towerdefense.analyze.FrameAnalyzer;
import com.fixus.towerdefense.tools.ObjectPosition;
import com.fixus.towerdefense.tools.PersonPosition;
import com.fixus.towerdefense.tools.TestRenderer;

public class StarterActivity extends RajawaliActivity implements CvCameraViewListener2,OnTouchListener {

	private CameraBridgeViewBase openCvCameraView;
	private CascadeClassifier cascadeClassifier;
	private Mat mRgba;
	private int absoluteObjectSize;
	private PersonPosition personPosition = null;
	private ObjectPosition objectPosition = null;
	
	public final Context context = this;
	
	
	private Accelerometer myAccelSensor;
	
	
	public GPS gps;
	public TextView headingValue;
	public TextView pitchValue;
	public TextView rollValue;
	public TextView latValue;
	public TextView lonValue;
	public TextView altValue;
	
	public TestRenderer mRenderer;

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
	
	OnClickListener snapButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			
			StarterActivity.this.personPosition = new PersonPosition(StarterActivity.this.gps.getLatitude(), StarterActivity.this.gps.getLongitude());
			
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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		OurSensorManager.debug = false;
		OurSensorManager.printToField = false;

		myAccelSensor = new Accelerometer(this);
		
		
		openCvCameraView = (CameraBridgeViewBase) new JavaCameraView(this, -1);
		openCvCameraView.setCvCameraViewListener(this);
		//openCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT)		
		
		mLayout.addView(openCvCameraView);
		
		mSurfaceView.setZOrderMediaOverlay(true);
		setGLBackgroundTransparent(true);
		mRenderer = new TestRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		super.setRenderer(mRenderer);
		
		mRenderer.setCameraPosition(0, 0, 8.2f);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);

		// The faces will be a 20% of the height of the screen
		absoluteObjectSize = (int) (height * 0.4);
	}

	@Override
	public void onCameraViewStopped() {
		if (mRgba != null){
			mRgba.release();
		}
		mRgba = null;
	}

	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		DecimalFormat df = new DecimalFormat("#.##");
		mRgba = inputFrame.rgba();
		
		/*
		 * Do wyswietlanie info z accelerometru
		 * 
		 * Core.putText(mRgba," x: " + myAccelSensor.getLastX()
				+ " y: " + myAccelSensor.getLastY() + " z: " + myAccelSensor.getLastZ() , new Point(0, 30),
				Core.FONT_HERSHEY_COMPLEX, 1, new Scalar(255, 0, 0, 255), 2);*/
		
		if (mRenderer.isReady()){
			setRotation(myAccelSensor.getLastX(),myAccelSensor.getLastY(),myAccelSensor.getLastZ());
		}
			
		
		return mRgba;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this,
				mLoaderCallback);
		openCvCameraView.setOnTouchListener(this);
		myAccelSensor.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (openCvCameraView != null)
			openCvCameraView.disableView();
		myAccelSensor.onPause();
	}
	
	public void onDestroy() {
		super.onDestroy();
		if (openCvCameraView != null)
			openCvCameraView.disableView();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void setRotation(double rotX, double rotY, double rotZ){
		mRenderer.set3DObjectRotate(rotX, -rotY, rotZ);
	}

}
