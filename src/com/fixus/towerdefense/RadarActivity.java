package com.fixus.towerdefense;

import java.nio.ByteBuffer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fixus.td.sensors.GPS;
import com.fixus.td.sensors.OurSensorManager2;
import com.fixus.towerdefense.camera.CameraPreview;
import com.fixus.towerdefense.camera.CameraTool;
import com.fixus.towerdefense.game.GameStatus;
import com.fixus.towerdefense.tools.Compas;
import com.fixus.towerdefense.tools.MapPoint;
import com.fixus.towerdefense.tools.ObjectPosition;
import com.fixus.towerdefense.tools.PhonePosition;
import com.google.android.gms.maps.model.LatLng;
import com.jme3.app.AndroidHarness;
import com.jme3.renderer.android.TextureUtil;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import com.jme3.texture.Image;

public class RadarActivity extends AndroidHarness {
	private static final String TAG = "TD_RADARACTIVITY";
	
	private static RadarActivity oTest;
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private CameraTool cTools;
	private boolean pixelFormatConversionNeeded = true;
	private boolean stopPreview;
	private boolean showModel = false;
	private byte[] mPreviewBufferRGB565 = null;
	private OurSensorManager2 sensorManager;
	private float currentDegree;
	private int lastAzimuth = 0;
	private float lastFullAzimuth = 0f;
	private float lastFullRoll = 0f;
	private ImageView compassNeedle;
	
	protected LatLng selectedPosition = null;
	protected float rollAvg = 0f;
	protected int rollAvgCounter = 32;
	
	public Image cameraJMEImageRGB565;
	public java.nio.ByteBuffer mPreviewByteBufferRGB565;
	public int mPreviewWidth;
	public int mPreviewHeight;
	public GPS gps;
	public float azimuthInDegress;
	public float azimut;
	
	private final Camera.PreviewCallback mCameraCallback = new Camera.PreviewCallback() {
		int i = 0;
		double angle = 0;
		public void onPreviewFrame(byte[] data, Camera c) {
			if (c != null && stopPreview == false) {
				azimut = Compas.getAzimut(
						sensorManager.getLastMatrix(Sensor.TYPE_ACCELEROMETER,0),
						sensorManager.getLastMatrix(Sensor.TYPE_MAGNETIC_FIELD,0)
				);
				azimuthInDegress = Compas.getAzimuthInDegress(azimut, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
				double azimuthInDegress2 = Compas.getAzimuthInDegress(azimut, false);
				Log.d(TAG, "Azymut: " + azimuthInDegress2);

				
				
				//tu ustawiamy nasza lokazliazacje
				Location fromLocation = new Location("");
				if(gps != null && gps.getLocation() != null){
					fromLocation = gps.getLocation();
				}else{
					//52.133340, 20.666227
					fromLocation.setLatitude(52.108837);
					fromLocation.setLongitude(21.043263);
				}
				float[] tmpMatrix = sensorManager.getLastMatrix(Sensor.TYPE_ACCELEROMETER, 5);
//				Log.d(TAG, "SENSOR: " + tmpMatrix[0] +  " | " + tmpMatrix[1] + " | " + tmpMatrix[2]);
//				PhonePosition phone = new PhonePosition();
				
				/**
				 * @TODO
				 * przenieść do PhonePosition do osobnej metody
				 */
				if(!PhonePosition.calibrated) {
					if(i == 0) {
						new AlertDialog.Builder(RadarActivity.this)
					    .setTitle("Kalibracja")
					    .setMessage("Rozpoczynam kalibrację...trzymaj telefon w pozycji wyjściowej")
					    .setCancelable(true)
					    .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			                }
					     })
					     .show();
					}
					if(i < RadarActivity.this.rollAvgCounter) {
						RadarActivity.this.rollAvg += tmpMatrix[2];
					} else {
						RadarActivity.this.rollAvg /= i;
						PhonePosition phone = new PhonePosition();
						phone.calibration(RadarActivity.this.rollAvg);
						new AlertDialog.Builder(RadarActivity.this)
					    .setTitle("Kalibracja")
					    .setMessage("Kalibracja zakończona: " + RadarActivity.this.rollAvg)
					    .setCancelable(true)
					    .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			                }
					     })
					     .show();
						
						GameStatus.phone = phone;
					}
				}				

				//tu jest lokalizacja do ktorej zmierzamy
				Location targetLocation = new Location("");
				//52.132052, 20.644810
				//52.127363, 20.671718
//			    targetLocation.setLatitude(52.132052);
//			    targetLocation.setLongitude(20.644810);
			    //a to ustawi odpowiednio strzalke
				if(RadarActivity.this.selectedPosition != null) {
					targetLocation.setLatitude(RadarActivity.this.selectedPosition.latitude);
					targetLocation.setLongitude(RadarActivity.this.selectedPosition.longitude);
					drawCompassToPoint(fromLocation, targetLocation);
					
					/**
					 * @TODO
					 * to oczywiśćie tmp. obiekt nie może być tworzony w każdej klatce
					 */
					ObjectPosition object = new ObjectPosition();
					object.setAzimut(getAzimuthData(azimuthInDegress2, fromLocation, targetLocation));
					boolean show = object.isSeen(azimuthInDegress2, GameStatus.horizontalViewAngle);
					if ((com.fixus.towerdefense.model.SuperimposeJME) app != null) {
						((com.fixus.towerdefense.model.SuperimposeJME) app).toogleObject(show);
					}					

				}
				
				mPreviewByteBufferRGB565.clear();
				// Perform processing on the camera preview data.
				if (pixelFormatConversionNeeded) {
					yCbCrToRGB565(data, mPreviewWidth, mPreviewHeight,
							mPreviewBufferRGB565);
					mPreviewByteBufferRGB565.put(mPreviewBufferRGB565);
				} else {
					mPreviewByteBufferRGB565.put(data);
				}
				cameraJMEImageRGB565.setData(mPreviewByteBufferRGB565);
				if ((com.fixus.towerdefense.model.SuperimposeJME) app != null) {
					((com.fixus.towerdefense.model.SuperimposeJME) app).setVideoBGTexture(cameraJMEImageRGB565);
				}
				
				i++;
				
				/*if(i % 10 == 0 && GameStatus.randomedPoints.size() > 0 && gps != null && gps.getLocation() != null) {
					Log.d(TAG, "Sprawdzam czy patrze na punkt");
//					Iterator<Location> it = GameStatus.randomedPoints.iterator();
//					while(it.hasNext()) {
//						
//					}
					Location target = GameStatus.randomedPoints.get(0);
					Log.d(TAG, "Pozycja target: " + target.getLongitude() + " | " + target.getLatitude());
					float bearing2 = gps.getLocation().bearingTo(target);
				    Log.d(TAG, "Bearing: " + bearing2);
				    Log.d(TAG, "kompas: " + azimuthInDegress);
				    Log.d(TAG,"Rotacja| Aziumut: " + azimut + " kat: " + azimuthInDegress
				     + " kierunek: " + Compas.getDirection(azimuthInDegress, 15));
				} 
//				else {
//					Log.d(TAG, GameStatus.randomedPoints.size() + "");
//					Log.d(TAG, gps.toString());
//				}
				
//				if(PhonePosition.checkIfFlat(sensorManager.getLastMatrix(Sensor.TYPE_ACCELEROMETER,0)[0], 3)) {
				*/
				
				if(PhonePosition.checkIfFlat(sensorManager.getLastMatrix(Sensor.TYPE_ACCELEROMETER,0)[0], 0)) {
					stopPreview = true;
					Intent i = new Intent(RadarActivity.this, LocatorActivity.class);
					startActivity(i);	
				}
				
				
				if(lastAzimuth != azimuthInDegress) {
					if ((com.fixus.towerdefense.model.SuperimposeJME) app != null) {
						Log.d(TAG, "TEST: " + tmpMatrix[2]);
						if(GameStatus.phone != null && GameStatus.phone.calibrated) {
							/**
							 * trzeba przekazać róznicę między ostatnim odczytem roll a odczytem z danej klatki
							 * wynika to z faktu, że metoda rotująca kamerę nie rotuje do danego stopnia a rotuje o zadaną wartość w każdej klatce
							 * należy uwzględnić znak, aby rotacja mogła odbyć się w obu kierunkach
							 */
							float currentRollRotation = tmpMatrix[2] - RadarActivity.this.lastFullRoll;
//							((com.fixus.towerdefense.model.SuperimposeJME) app).rotate(currentRollRotation, azimuthInDegress-lastFullAzimuth, 0f);
							((com.fixus.towerdefense.model.SuperimposeJME) app).rotate(0f, azimuthInDegress-lastFullAzimuth, 0f);
//							((com.fixus.towerdefense.model.SuperimposeJME) app).rotateCamera(currentRollRotation, 0f, 0f);
						}
					}
					lastAzimuth = (int)azimuthInDegress;
					lastFullAzimuth = azimuthInDegress;
				}

			}
		}
	};
	
	private void drawCompassToPoint(Location fromLocation,Location targetLocation){
	    if(fromLocation != null && targetLocation != null){
			//wyliczamy kierunek miedzy punktem poczatkowym
	    	//a punktem docelowym, niezaleznym od miejsca w ktore patrzymy
	    	float directionInDegress = (float)angleFromCoordinate(
		    		fromLocation.getLatitude(),
		    		fromLocation.getLongitude(),
		    		targetLocation.getLatitude(),
		    		targetLocation.getLongitude()
		    		);
		    //teraz obracamy strzalke kompasu o odpowiedni kat
	    	//wyliczony z Wskazanie kompasu minus kat wyliczony powyzej
	    	//Dlatego, ze igla ma miec obrot o 0 stopni jesli wskazujemy
	    	//w pkt docelowy. Inaczej ma sie obracac w odpowiednia strone
		    rotateNeedle(-(azimuthInDegress - directionInDegress));
		    /*Log.d(TAG, "kat1: " + dupa);
		    Log.d(TAG, "kat2: " + azimuthInDegress);
		    Log.d(TAG, "kat3: " + (azimuthInDegress - dupa));
		    Log.d(TAG, "kat4: " + (-(azimuthInDegress - dupa)));*/
	    }
	}
	
	private double getAzimuthData(double azimuth, Location fromLocation, Location targetLocation) {
		float directionInDegress = (float)angleFromCoordinate(
	    		fromLocation.getLatitude(),
	    		fromLocation.getLongitude(),
	    		targetLocation.getLatitude(),
	    		targetLocation.getLongitude()
	    		);
		return (-(azimuth - directionInDegress));
	}
	
	/*
	 * Wyliczenie kata pomiedzy docelowa lokazlizacja, a naszym obecnym pkt
	 */
	private double angleFromCoordinate(double lat1, double long1, double lat2,
	        double long2) {

	    double dLon = (long2 - long1);

	    double y = Math.sin(dLon) * Math.cos(lat2);
	    double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
	            * Math.cos(lat2) * Math.cos(dLon);

	    double brng = Math.atan2(y, x);

	    brng = Math.toDegrees(brng);
	    brng = (brng + 360) % 360;
	    brng = 360 - brng;

	    return brng;
	}
	
	private void rotateNeedle(float degress){
		//tu jest tylko rotacja obrazka o zadany kat
	    Bitmap myImg = BitmapFactory.decodeResource(getResources(), R.drawable.compass_needle1);

	    Matrix matrix = new Matrix();
	    matrix.postRotate(degress);

	    Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
	            matrix, true);

	    compassNeedle.setImageBitmap(rotated);
	}

	public RadarActivity() {
		if(oTest == null){
			oTest = this;
		}
		// Set the application class to run
		// appClass = "mygame.Main";
		appClass = "com.fixus.towerdefense.model.SuperimposeJME";
		// Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
		eglConfigType = ConfigType.BEST;
//		eglConfigType = ConfigType.FASTEST;
		
		// Exit Dialog title & message
		exitDialogTitle = "Wyjść?";
		exitDialogMessage = "Naciśnik TAK";
		// Enable verbose logging
		eglConfigVerboseLogging = false;
		// Choose screen orientation
		screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		mouseEventsInvertX = true;
		// Invert the MouseEvents Y (default = true)
		mouseEventsInvertY = true;
		TextureUtil.ENABLE_COMPRESSION = false;
	}
	
	LinearLayout l;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Log.d(TAG, "onCreate");
	    //obieranie intencji - obecnie bez zadnych ustawien
  		Intent intent = getIntent();
  		if(intent.hasExtra(Second.RANGE)) {
  	  		GameStatus.radius = (double)intent.getIntExtra(Second.RANGE, 2);
  		}
  		if(intent.hasExtra(Second.POINTS)) {
  	  		GameStatus.points = intent.getIntExtra(Second.POINTS, 0);
  		}
  		if(intent.hasExtra("selectedLat")) {
  			double tmpLat = intent.getDoubleExtra("selectedLat", 0);
  			double tmpLng = intent.getDoubleExtra("selectedLng", 0);
  			this.selectedPosition = new LatLng(tmpLat, tmpLng);
  		}
		this.cTools = new CameraTool();
		
		sensorManager = new OurSensorManager2(this);
		sensorManager.addSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.addSensor(Sensor.TYPE_MAGNETIC_FIELD);	
		
	    gps = new GPS(this);
	    if(!gps.canGetLocation()){
	    	gps.showSettingsPopUp();
	    }
	    if(this.gps != null) {
			if(this.gps.getLocation() != null) {
				GameStatus.randomedPoints = MapPoint.generatePoints(this.gps.getLocation(), GameStatus.getRadiusInMeters(), GameStatus.points);
			}
	    }
	    
	    l = (LinearLayout) findViewById(R.layout.activity_radar);
	}
	
	@Override
    public void onResume() {
		super.onResume();    	
    	this.stopPreview = false;
		Log.d(TAG, "onResume");

		this.mCamera = this.cTools.getCameraInstance();
		this.mCamera = this.cTools.initializeCameraParameters(this.mCamera);
		this.preparePreviewCallbackBuffer();
		
		if (mCamera == null) {
			Log.e(TAG, "Camera not available");
		} else {
			// Create our Preview view and set it as the content of our
			// activity.
			this.mPreview = new CameraPreview(this, mCamera, mCameraCallback);
			// We do not want to display the Camera Preview view at startup - so
			// we resize it to 1x1 pixel.
//			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
//			addContentView(this.mPreview, lp);			
			
			if(compassNeedle == null){
				compassNeedle = new ImageView(this);
				compassNeedle.setImageResource(R.drawable.compass_needle1);
			    addContentView(compassNeedle, new ViewGroup.LayoutParams(300, 500));
			    
				ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
				addContentView(this.mPreview, lp);			
			}
		}
	}
	
	public void preparePreviewCallbackBuffer() {		
		int pformat;
		pformat = mCamera.getParameters().getPreviewFormat();
		Log.e(TAG, "PREVIEW format: " + pformat);
		// Get pixel format information to compute buffer size.
		PixelFormat info = new PixelFormat();
		PixelFormat.getPixelFormatInfo(pformat, info);		
		// The actual preview width and height.
		// They can differ from the requested width mDesiredCameraPreviewWidth
		mPreviewWidth = mCamera.getParameters().getPreviewSize().width;
		mPreviewHeight = mCamera.getParameters().getPreviewSize().height;
		int bufferSizeRGB565 = mPreviewWidth * mPreviewHeight * 2 + 4096;
		//Delete buffer before creating a new one.
		mPreviewBufferRGB565 = null;		
		mPreviewBufferRGB565 = new byte[bufferSizeRGB565];
		mPreviewByteBufferRGB565 = ByteBuffer.allocateDirect(mPreviewBufferRGB565.length);
		cameraJMEImageRGB565 = new Image(Image.Format.RGB565, mPreviewWidth,
				mPreviewHeight, mPreviewByteBufferRGB565);
		
//		return cameraJMEImageRGB565;
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		this.stopPreview = true;
		super.onPause();		
		if(this.mCamera != null) {
			this.mCamera.setPreviewCallback(null);
			this.mCamera.release();
			this.mCamera = null;
		}
		// remove the SurfaceView
//		ViewGroup parent = (ViewGroup) mPreview.getParent(); 
//		parent.removeView(mPreview);
	}

	@Override
	protected void onDestroy() {
		this.stopPreview = true;
		super.onDestroy();
		if(this.mCamera != null) {
			this.mCamera.setPreviewCallback(null);
			this.mCamera.release();
			this.mCamera = null;
		}
		Log.d(TAG, "onDestroy");
	}
	
	public static void yCbCrToRGB565(byte[] yuvs, int width, int height,
			byte[] rgbs) {

		// the end of the luminance data
		final int lumEnd = width * height;
		// points to the next luminance value pair
		int lumPtr = 0;
		// points to the next chromiance value pair
		int chrPtr = lumEnd;
		// points to the next byte output pair of RGB565 value
		int outPtr = 0;
		// the end of the current luminance scanline
		int lineEnd = width;

		while (true) {

			// skip back to the start of the chromiance values when necessary
			if (lumPtr == lineEnd) {
				if (lumPtr == lumEnd)
					break; // we've reached the end
				// division here is a bit expensive, but's only done once per
				// scanline
				chrPtr = lumEnd + ((lumPtr >> 1) / width) * width;
				lineEnd += width;
			}

			// read the luminance and chromiance values
			final int Y1 = yuvs[lumPtr++] & 0xff;
			final int Y2 = yuvs[lumPtr++] & 0xff;
			final int Cr = (yuvs[chrPtr++] & 0xff) - 128;
			final int Cb = (yuvs[chrPtr++] & 0xff) - 128;
			int R, G, B;

			// generate first RGB components
			B = Y1 + ((454 * Cb) >> 8);
			if (B < 0)
				B = 0;
			else if (B > 255)
				B = 255;
			G = Y1 - ((88 * Cb + 183 * Cr) >> 8);
			if (G < 0)
				G = 0;
			else if (G > 255)
				G = 255;
			R = Y1 + ((359 * Cr) >> 8);
			if (R < 0)
				R = 0;
			else if (R > 255)
				R = 255;
			// NOTE: this assume little-endian encoding
			rgbs[outPtr++] = (byte) (((G & 0x3c) << 3) | (B >> 3));
			rgbs[outPtr++] = (byte) ((R & 0xf8) | (G >> 5));

			// generate second RGB components
			B = Y2 + ((454 * Cb) >> 8);
			if (B < 0)
				B = 0;
			else if (B > 255)
				B = 255;
			G = Y2 - ((88 * Cb + 183 * Cr) >> 8);
			if (G < 0)
				G = 0;
			else if (G > 255)
				G = 255;
			R = Y2 + ((359 * Cr) >> 8);
			if (R < 0)
				R = 0;
			else if (R > 255)
				R = 255;
			// NOTE: this assume little-endian encoding
			rgbs[outPtr++] = (byte) (((G & 0x3c) << 3) | (B >> 3));
			rgbs[outPtr++] = (byte) ((R & 0xf8) | (G >> 5));
		}
	}
	
	public static void messageDialog(final String text){
		oTest.runOnUiThread(new Runnable() {
			  public void run() {
			    Toast.makeText(oTest, "Interakcja z obiektem: " + text, Toast.LENGTH_SHORT).show();
			  }
		});
	}
}
