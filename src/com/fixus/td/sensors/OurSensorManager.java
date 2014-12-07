package com.fixus.td.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public abstract class OurSensorManager extends Activity implements SensorEventListener{
	private SensorManager sensorManager;
	private Sensor sensor;
	private int sensorType;
	protected TextView[] texts;
	
	public OurSensorManager(Context context,int sensorType,TextView... texts){
		this.sensorType = sensorType;
		this.texts = texts;
		startMe(context);
	}
	public void startMe(Context context){
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(sensorType);
	    sensorManager.registerListener(this, sensor , SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onPause() {
	    super.onPause();
	    sensorManager.unregisterListener(this);
	}

	protected void onResume() {
	    super.onResume();
	    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}	
	
	//gettery	
	protected SensorManager getSensorManager(){
		return this.sensorManager;
	}
	protected Sensor getSensor(){
		return this.sensor;
	}
	
}

