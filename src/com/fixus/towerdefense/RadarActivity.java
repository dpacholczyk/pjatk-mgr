package com.fixus.towerdefense;

import java.nio.ByteBuffer;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.fixus.td.sensors.GPS;
import com.fixus.td.sensors.OurSensorManager2;
import com.fixus.towerdefense.camera.CameraPreview;
import com.fixus.towerdefense.camera.CameraTool;
import com.fixus.towerdefense.tools.Compas;
import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import com.jme3.texture.Image;

public class RadarActivity extends AndroidHarness {
	private static final String TAG = "TD_TestActvity";
	private Camera mCamera;
	private CameraPreview mPreview;
	private CameraTool cTools;
	private boolean pixelFormatConversionNeeded = true;
	private boolean stopPreview;
	private boolean showModel = false;
	private byte[] mPreviewBufferRGB565 = null;
	
	private OurSensorManager2 sensorManager;
	public GPS gps;
	private float currentDegree;
	float azimuthInDegress;


	private int lastAzimuth = 0;
	private float lastFullAzimuth = 0f;
	
	// Implement the interface for getting copies of preview frames
	private final Camera.PreviewCallback mCameraCallback = new Camera.PreviewCallback() {
		int i = 0;
		double angle = 0;
		public void onPreviewFrame(byte[] data, Camera c) {
			if (c != null && stopPreview == false) {
				i++;
				
//				Log.d(TAG, "-------------------");
//				Log.d(TAG, RadarActivity.this.gps.getLatitude() + " | " + RadarActivity.this.gps.getLongitude());
				float azimut = Compas.getAzimut(
						sensorManager.getLastMatrix(Sensor.TYPE_ACCELEROMETER,0),
						sensorManager.getLastMatrix(Sensor.TYPE_MAGNETIC_FIELD,0));
				azimuthInDegress = Compas.getAzimuthInDegress(azimut);

				Log.d(TAG,"Rotacja| Aziumut: " + azimut + " kat: " + azimuthInDegress
						+ " kierunek: " + Compas.getKierunek(azimuthInDegress, 15));

//				if(lastAzimuth != (int)azimuthInDegress) {
				if(lastAzimuth != azimuthInDegress) {

					Log.d(TAG, "Rotacja| różnica: " + (azimuthInDegress-lastFullAzimuth));
					if ((com.fixus.towerdefense.model.SuperimposeJME) app != null) {
						((com.fixus.towerdefense.model.SuperimposeJME) app).rotate(0f, (azimuthInDegress-lastFullAzimuth), 0f);
					}

					lastAzimuth = (int)azimuthInDegress;
					lastFullAzimuth = azimuthInDegress;
					
					Log.d(TAG, "-------------------");
				}
				
//				Location fromLocation = new Location("");
//				fromLocation.setLatitude(52.224432);
//				fromLocation.setLongitude(20.993049);
//				
//				Location targetLocation = new Location("");//provider name is unecessary
//			    targetLocation.setLatitude(52.224077);//your coords of course
//			    targetLocation.setLongitude(20.993757);

			    /**
			     * przedziały
			     * 0 - północ
			     * -90 : 0 - północnyc zachód
			     * -90 - zachód
			     * -90 : -180 - południowy zachód
			     * -180/180 - południe
			     * 180 : 90 - południowy wschód
			     * 90 - wschód
			     * 90 : 0 - północny wschód
			     * 
			     */
			    
			    // na polnoc oscyluje wokol 0
//			    targetLocation.setLatitude(52.225431);//your coords of course
//			    targetLocation.setLongitude(20.993114);

			    // na zachód oscyluje wokoł -90
//			    targetLocation.setLatitude(52.224353);//your coords of course
//			    targetLocation.setLongitude(20.991440);

			    // na wschold oscyluje wokol 90
//			    targetLocation.setLatitude(52.224432);//your coords of course
//			    targetLocation.setLongitude(20.994358);

			    // na poludniu oscyluje wokol 180
//			    targetLocation.setLatitude(52.223998);//your coords of course
//			    targetLocation.setLongitude(20.993049);

			    
//			    float distanceInMeters =  targetLocation.distanceTo(fromLocation);
//			    Log.d(TAG, "Odleglosc: " + distanceInMeters);
//			    
//				float bearing2 = fromLocation.bearingTo(targetLocation);
//			    Log.d(TAG, "Bearing 2: " + bearing2);

				
				// poniżej rózne opcje manipulowania animacja
//				if(i == 150) {
//					RadarActivity.this.showModel = true;
//					if ((com.fixus.towerdefense.model.SuperimposeJME) app != null) {
////						((com.fixus.towerdefense.model.SuperimposeJME) app).mAniControl.setEnabled(false);
////						((com.fixus.towerdefense.model.SuperimposeJME) app).mAniChannel.setSpeed(10f);
//						((com.fixus.towerdefense.model.SuperimposeJME) app).getRootNode().detachAllChildren();
//					}
//				}
				
				String interested = Compas.SOUTH;
				if(Compas.checkIfDirection(interested, azimuthInDegress, 10)) {
					RadarActivity.this.showModel = true;
				} else {
					RadarActivity.this.showModel = false;
				}
				
//				if(!RadarActivity.this.showModel) {
//					if ((com.fixus.towerdefense.model.SuperimposeJME) app != null) {
//						if(((com.fixus.towerdefense.model.SuperimposeJME) app).mAniControl != null) {
//							((com.fixus.towerdefense.model.SuperimposeJME) app).mAniControl.setEnabled(false);
//						}						
//					}
//				} else {
//					if ((com.fixus.towerdefense.model.SuperimposeJME) app != null) {
//						if(((com.fixus.towerdefense.model.SuperimposeJME) app).mAniControl != null) {
//							((com.fixus.towerdefense.model.SuperimposeJME) app).mAniControl.setEnabled(true);
//						}						
//					}
//				}
				
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
					((com.fixus.towerdefense.model.SuperimposeJME) app)
							.setVideoBGTexture(cameraJMEImageRGB565);
				}
			}
		}
	};

	
	public Image cameraJMEImageRGB565;
	public java.nio.ByteBuffer mPreviewByteBufferRGB565;
	public int mPreviewWidth;
	public int mPreviewHeight;

	public RadarActivity() {
		// Set the application class to run
		// appClass = "mygame.Main";
		appClass = "com.fixus.towerdefense.model.SuperimposeJME";
		// Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
//		eglConfigType = ConfigType.BEST;
		eglConfigType = ConfigType.FASTEST;
		
		// Exit Dialog title & message
		exitDialogTitle = "Exit?";
		exitDialogMessage = "Press Yes";
		// Enable verbose logging
		eglConfigVerboseLogging = false;
		// Choose screen orientation
		screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		mouseEventsInvertX = true;
		// Invert the MouseEvents Y (default = true)
		mouseEventsInvertY = true;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		

		sensorManager = new OurSensorManager2(this);
		sensorManager.addSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.addSensor(Sensor.TYPE_MAGNETIC_FIELD);		
		
	    gps = new GPS(this);
	    if(!gps.canGetLocation()){
	    	gps.showSettingsPopUp();
	    }

	}
	
	@Override
    public void onResume() {
		super.onResume();    	
    	this.stopPreview = false;
		this.cTools = new CameraTool();
		
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
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
			addContentView(this.mPreview, lp);			
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
		this.stopPreview = true;
		super.onPause();		
		// Make sure to release the camera immediately on pause.
		this.cTools.releaseCamera(this.mCamera);
		// remove the SurfaceView
//		ViewGroup parent = (ViewGroup) mPreview.getParent(); 
//		parent.removeView(mPreview);
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
	
}
