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
	public void onSensorChanged(SensorEvent event) {
		Sensor mySensor = event.sensor;		
	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    	//na razie tylko ustawiamy wartosci na output
	    	this.texts[0].setText(event.values[0]+"");
	    	this.texts[1].setText(event.values[1]+"");
	    	this.texts[2].setText(event.values[2]+"");
	    	setLastX(event.values[0]);
	    	setLastY(event.values[1]);
	    	setLastZ(event.values[2]);
	    }	
	}

	@Override
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}

}
