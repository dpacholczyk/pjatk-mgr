package com.fixus.td.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.widget.TextView;

public class Accelerometer extends OurSensorManager{
	public Accelerometer(Context context) {
		super(context,Sensor.TYPE_ACCELEROMETER);
	}
	
	public Accelerometer(Context context, TextView textX, TextView textY,TextView textZ){
		super(context,Sensor.TYPE_ACCELEROMETER,textX,textY,textZ);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor mySensor = event.sensor;		
	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    	//na razie tylko ustawiamy wartosci na output
	    	if(printToField) {
	    		try{
		    		this.texts[0].setText(event.values[0]+"");
			    	this.texts[1].setText(event.values[1]+"");
			    	this.texts[2].setText(event.values[2]+"");
	    		}catch(Exception e){
	    			Log.d("debuggowanie", "Wywalil sie blad w onSensorChanged");
	    		}
	    	}
	    	setLastX(event.values[0]);
	    	setLastY(event.values[1]);
	    	setLastZ(event.values[2]);
	    	
	    	Log.d("debuggowanie", getLastX().toString());
	    	Log.d("debuggowanie", getLastY().toString());
	    	Log.d("debuggowanie", getLastZ().toString());
	    	
	    	if(debug) {
		    	Log.d(SENSOR_TAG, "(Accelerometer) Heading: " + event.values[0]);
		    	Log.d(SENSOR_TAG, "(Accelerometer) Pitch: " + event.values[1]);
		    	Log.d(SENSOR_TAG, "(Accelerometer) Roll: " + event.values[2]);
	    	}
	    }	
	}

	@Override
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}

}
