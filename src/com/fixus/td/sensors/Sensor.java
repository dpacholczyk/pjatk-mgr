package com.fixus.td.sensors;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.util.Log;

public class Sensor implements SensorInterface {
	protected SensorManager sensorManager = null;
	protected LocationManager locationManager = null;
	public static boolean logData = false;
	public static final String logDataTag = "TD_SENSOR";
	public static final int GPS_MODE = 1;
	public static final int ORIENTATION_MODE = 2;
	
	public Sensor(Context context, int mode, String systemService) {
		switch(mode) {
			case 1:
				this.locationManager = (LocationManager) this.managerCreator(context, systemService, mode);
				break;
			case 2:
				this.sensorManager = (SensorManager) this.managerCreator(context, systemService, mode);
				break;
		}
	}
	
	public Sensor(Context context, int mode, String systemService, boolean debug) {
		logData = debug;
		switch(mode) {
			case 1:
				this.locationManager = (LocationManager) this.managerCreator(context, systemService, mode);
				break;
			case 2:
				this.sensorManager = (SensorManager) this.managerCreator(context, systemService, mode);
				break;
		}
	}

	private Object managerCreator(Context context, String systemService, int mode) {
		return context.getSystemService(systemService);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
