package com.fixus.td.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.widget.TextView;

public class Accelerometer extends OurSensorManager{
	public Accelerometer(Context context, TextView textX, TextView textY,TextView textZ){
		super(context,Sensor.TYPE_ACCELEROMETER,textX,textY,textZ);
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;		
	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    	this.texts[0].setText(sensorEvent.values[0]+"");
	    	this.texts[1].setText(sensorEvent.values[1]+"");
	    	this.texts[2].setText(sensorEvent.values[2]+"");
	    }	
	}

	@Override
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}

}
