package com.fixus.td.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;

public class DavidSensor extends Activity implements SensorInterface {
	public static boolean logData = false;
	public static final String logDataTag = "TD_SENSOR";
	public static final Context TYPE_ACCELEROMETER = null;
	protected SensorManager sensorManager = null;
	protected LocationManager locationManager = null;
	private final ManagerEnum mode;
		
	public DavidSensor(Context context, ManagerEnum mode, String systemService) {
		this.mode = mode;
		switch(mode) {
			case GPS_MODE:
				this.locationManager = (LocationManager) this.managerCreator(context, systemService);
				break;
			case ORIENTATION_MODE:
				this.sensorManager = (SensorManager) this.managerCreator(context, systemService);
				break;
		}
	}

	private Object managerCreator(Context context, String systemService) {
		return context.getSystemService(systemService);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
