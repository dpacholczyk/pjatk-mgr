package com.fixus.td.sensors;

import com.fixus.towerdefense.StarterActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.widget.TextView;

public class Orientation extends OurSensorManager{	
	@SuppressWarnings("deprecation")
	public Orientation(Context context) {
		super(context, Sensor.TYPE_ORIENTATION);
	}
	
	@SuppressWarnings("deprecation")
	public Orientation(Context context, TextView textX, TextView textY,TextView textZ) {
		super(context, Sensor.TYPE_ORIENTATION,textX,textY,textZ);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor mySensor = event.sensor;		
	    if (mySensor.getType() == Sensor.TYPE_ORIENTATION) {
	    	//na razie tylko ustawiamy wartosci na output
	    	
	    	if(printToField) {
	    		this.texts[0].setText("Heading: " + event.values[0]);
		    	this.texts[1].setText("Pitch: " + event.values[1]);
		    	this.texts[2].setText("Roll: " + event.values[2]);
	    	}
	    	
	    	setLastX(event.values[0]);
	    	setLastY(event.values[1]);
	    	setLastZ(event.values[2]);
	    	
	    	if(debug) {
		    	Log.d(SENSOR_TAG, "(Orientatnion) Heading: " + event.values[0]);
		    	Log.d(SENSOR_TAG, "(Orientatnion) Pitch: " + event.values[1]);
		    	Log.d(SENSOR_TAG, "(Orientatnion) Roll: " + event.values[2]);
	    	}
	    }	
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}

}