package com.fixus.towerdefense.camera;

import java.nio.ByteBuffer;
import java.util.List;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;

import com.jme3.texture.Image;

public class CameraTool {
	protected static int mDesiredCameraPreviewWidth = 800;
	private static boolean pixelFormatConversionNeeded = true;
	public static int mPreviewWidth;
	public static int mPreviewHeight;
	private static byte[] mPreviewBufferRGB565 = null;
	public static Image cameraJMEImageRGB565;
	public static java.nio.ByteBuffer mPreviewByteBufferRGB565;


	public static final String TAG = "TD_CAMERATOOL";
	
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(0);
		} catch (Exception ex) {

		}

		return c;
	}

	public static void releaseCamera(Camera mCamera) {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	public static void initializeCameraParameters(Camera mCamera) {
		Camera.Parameters parameters = mCamera.getParameters();
		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
		int currentWidth = 0;
		int currentHeight = 0;
		boolean foundDesiredWidth = false;
		for (Camera.Size s : sizes) {
			if (s.width == mDesiredCameraPreviewWidth) {
				currentWidth = s.width;
				currentHeight = s.height;
				foundDesiredWidth = true;
				break;
			}
		}
		if (foundDesiredWidth) {
			parameters.setPreviewSize(currentWidth, currentHeight);
		}
		List<Integer> pixelFormats = parameters.getSupportedPreviewFormats();
		for (Integer format : pixelFormats) {
			if (format == ImageFormat.RGB_565) {
				Log.d(TAG, "Camera supports RGB_565");
				pixelFormatConversionNeeded = false;
				parameters.setPreviewFormat(format);
				break;
			}
		}
		if (pixelFormatConversionNeeded == true) {
			Log.e(TAG,
					"Camera does not support RGB565 directly. Need conversion");
		}

		mCamera.setParameters(parameters);
	}

	public static void preparePreviewCallbackBuffer(Camera mCamera) {
		mPreviewWidth = mCamera.getParameters().getPreviewSize().width;
		mPreviewHeight = mCamera.getParameters().getPreviewSize().height;
		int bufferSizeRGB565 = mPreviewWidth * mPreviewHeight * 2 + 4096;
		mPreviewBufferRGB565 = null;
		mPreviewBufferRGB565 = new byte[bufferSizeRGB565];
		mPreviewByteBufferRGB565 = ByteBuffer
				.allocateDirect(mPreviewBufferRGB565.length);
		cameraJMEImageRGB565 = new Image(Image.Format.RGB565, mPreviewWidth,
				mPreviewHeight, mPreviewByteBufferRGB565);
	}

}
