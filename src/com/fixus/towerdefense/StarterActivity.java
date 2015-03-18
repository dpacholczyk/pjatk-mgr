//package com.fixus.towerdefense;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.text.DecimalFormat;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
//import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
//import org.opencv.android.JavaCameraView;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Point;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//
//import rajawali.RajawaliActivity;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.hardware.Sensor;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.RotateAnimation;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.fixus.td.sensors.GPS;
//import com.fixus.td.sensors.OurSensorManager2;
//import com.fixus.towerdefense.tools.Compas;
//import com.fixus.towerdefense.tools.ObjectPosition;
//import com.fixus.towerdefense.tools.PersonPosition;
//import com.fixus.towerdefense.tools.TestRenderer;
////github.com/dpacholczyk/pjatk-mgr.git
//
//
//public class StarterActivity extends RajawaliActivity implements CvCameraViewListener2,OnTouchListener {
//
//	private CameraBridgeViewBase openCvCameraView;
//	private CascadeClassifier cascadeClassifier;
//	private Mat mRgba;
//	private int absoluteObjectSize;
//	private PersonPosition personPosition = null;
//	private ObjectPosition objectPosition = null;
//	private Mat grayscaleImage;
//	private Mat grayscaleImage2;
//	
//	public final Context context = this;
//	
//	private OurSensorManager2 sensorManager;
//	private ImageView image;
//	private float currentDegree;
//	
//	public GPS gps;
//	public TextView headingValue;
//	public TextView pitchValue;
//	public TextView rollValue;
//	public TextView latValue;
//	public TextView lonValue;
//	public TextView altValue;
//	
//	public TestRenderer mRenderer;
//
//	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//		@Override
//		public void onManagerConnected(int status) {
//			switch (status) {
//			case LoaderCallbackInterface.SUCCESS:
//				initializeOpenCVDependencies();
//				break;
//			default:
//				super.onManagerConnected(status);
//				break;
//			}
//		}
//	};
//	
//	OnClickListener snapButtonListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			
//			
//			
//			StarterActivity.this.personPosition = new PersonPosition(StarterActivity.this.gps.getLatitude(), StarterActivity.this.gps.getLongitude());
//			
//		}
//	};
//
//	private void initializeOpenCVDependencies() {
//
//		try {
//			// Copy the resource into a temp file so OpenCV can load it
//			InputStream is = getResources().openRawResource(R.raw.cascade);
//			File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//			File mCascadeFile = new File(cascadeDir, "cascade.xml");
//			FileOutputStream os = new FileOutputStream(mCascadeFile);
//
//			byte[] buffer = new byte[4096];
//			int bytesRead;
//			while ((bytesRead = is.read(buffer)) != -1) {
//				os.write(buffer, 0, bytesRead);
//			}
//			is.close();
//			os.close();
//
//			// Load the cascade classifier
//			cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//		} catch (Exception e) {
//			Log.e("OpenCVActivity", "Error loading cascade", e);
//		}
//
//		// And we are ready to go
//		openCvCameraView.enableView();
//	}
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		//crossaim
//		image = new ImageView(this);
//		image.setImageResource(R.drawable.img_compass);
//		
//		//OurSensorManager.debug = false;
//		//OurSensorManager.printToField = false;
//
//		//myAccelSensor = new Accelerometer(this);		
//		sensorManager = new OurSensorManager2(this);
//		sensorManager.addSensor(Sensor.TYPE_ACCELEROMETER);
//		sensorManager.addSensor(Sensor.TYPE_MAGNETIC_FIELD);		
//		
//        gps = new GPS(this);
//        if(!gps.canGetLocation()){
//        	gps.showSettingsPopUp();
//        }
//        //setContentView(R.layout.activity_starter);
//		
////		setContentView(R.layout.activity_starter);
////		openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.javaCamera);
////		openCvCameraView.setVisibility(SurfaceView.VISIBLE);
////		openCvCameraView.setCvCameraViewListener(this);
//		
//		openCvCameraView = (CameraBridgeViewBase) new JavaCameraView(this, -1);
//		openCvCameraView.setCvCameraViewListener(this);	
//		
//		mLayout.addView(openCvCameraView);
//		mLayout.addView(image);
//		
//		mSurfaceView.setZOrderMediaOverlay(true);
//		setGLBackgroundTransparent(true);
//		mRenderer = new TestRenderer(this);
//		mRenderer.setSurfaceView(mSurfaceView);
//		super.setRenderer(mRenderer);
//		
//		//oś Z decyduje o tym jak blisko/daleko jest obiekt
//		mRenderer.setCameraPosition(-5, 5, 800f);
//	}
//
//	@Override
//	public void onCameraViewStarted(int width, int height) {
//		mRgba = new Mat(height, width, CvType.CV_8UC4);
//		grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
//		grayscaleImage2 = new Mat(height, width, CvType.CV_8UC1);
//
//		// The faces will be a 20% of the height of the screen
//		absoluteObjectSize = (int) (height * 0.4);
//	}
//
//	@Override
//	public void onCameraViewStopped() {
//		if (mRgba != null){
//			mRgba.release();
//		}
//		mRgba = null;
//	}
//	
//	private void kompasuj(){
//        // create a rotation animation (reverse turn degree degrees)
//        RotateAnimation ra = new RotateAnimation(
//                currentDegree, 
//                -azimuthInDegress,
//                Animation.RELATIVE_TO_SELF, 0.5f, 
//                Animation.RELATIVE_TO_SELF,
//                0.5f);
//
//        // how long the animation will take place
//        ra.setDuration(210);
//        // set the animation after the end of the reservation status
//        ra.setFillAfter(true);
//        // Start the animation
//        if(image != null){
//        	image.startAnimation(ra);
//        }
//        currentDegree = -azimuthInDegress;
//	}
//
//	private int testCounter = 0;
//	float azimuthInDegress;
//	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
////	public Mat onCameraFrame(Mat aInputFrame) {
//		DecimalFormat df = new DecimalFormat("#.##");
////		mRgba = inputFrame.rgba();
//		/*
//		 * Do wyswietlanie info z accelerometru
//		 * 
//		 */	
//		Mat aInputFrame = inputFrame.rgba();
//		
//		float azimut = Compas.getAzimut(
//				sensorManager.getLastMatrix(Sensor.TYPE_ACCELEROMETER,0),
//				sensorManager.getLastMatrix(Sensor.TYPE_MAGNETIC_FIELD,0));
//		azimuthInDegress = Compas.getAzimuthInDegress(azimut);
//		
//		Core.putText(aInputFrame,"Aziumut: " + azimut + " kat: " + azimuthInDegress
//				+ " kierunek: " + Compas.getKierunek(azimuthInDegress), new Point(0, 30),
//				Core.FONT_HERSHEY_COMPLEX, 1, new Scalar(255, 0, 0, 255), 2);
//		runOnUiThread(new Runnable(){
//            @Override
//            public void run() {
//            	kompasuj();
//            }
//		});
//		
//		
//		return aInputFrame;
//	}
//	
//	
//
//	@SuppressLint("ClickableViewAccessibility")
//	@Override
//	public void onResume() {
//		super.onResume();
//		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this,
//				mLoaderCallback);
//		openCvCameraView.setOnTouchListener(this);
//		sensorManager.onResume();
//	}
//	
//	@Override
//	public void onPause() {
//		super.onPause();
//		if (openCvCameraView != null)
//			openCvCameraView.disableView();
//		sensorManager.onResume();
//	}
//	
//	public void onDestroy() {
//		super.onDestroy();
//		if (openCvCameraView != null)
//			openCvCameraView.disableView();
//	}
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	
//	private double nX,nY,nZ;
//	private void setRotation(double rotX, double rotY, double rotZ){
//		nX = 0;
//		nY += getRotValue(rotY);
//		nZ += getRotValue(rotZ);
//		
//		
//		Log.d("rotation", "X pos: " + nZ);
//		Log.d("rotation", "Y pos: " + nY);
//
//		mRenderer.rotate3DObject(nZ, nY, nX);
//		//mRenderer.rotateCamera(nZ, nY, nX);
//	}
//	
//	private double getRotValue(double x){
//		int rValue = 0;
//		
//		if(x < -1){
//			rValue += 1;
//		}else if(x > 1){
//			rValue += -1;
//		}
//		
//		return rValue;
//	}
//
//}
