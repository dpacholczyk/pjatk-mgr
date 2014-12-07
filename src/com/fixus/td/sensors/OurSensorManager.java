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
	protected Float lastX,lastY,lastZ;
	
	public OurSensorManager(Context context,int sensorType,TextView... texts){
		this.sensorType = sensorType;
		this.texts = texts;
		startMe(context);
	}
	private void startMe(Context context){
		/*
		 * Dodac jakies sprawdzanie, czy na danym urzadzeniu w ogole dany sensor istnieje
		 * 
		 * Pytanie, co zrobic jak nie istnieje? propozycja jest wywalenie jakiegos wyjatku
		 * po czym wyswietlenie info, ze na tym urzadzeniu nie pograsz sobie w ta gierke
		 */
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(sensorType);
	    sensorManager.registerListener(this, sensor , SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onPause() {
	    super.onPause();
		 // W trakcie wstrzymania aplikacji zatrzymuje pobieranie aktualizowanych danych w celu
		 // zaoszczêdzenia energii
	    sensorManager.unregisterListener(this);
	}

	protected void onResume() {
	    super.onResume();
	    //Po przywrocenie wznawiamy aktualizaowanie danych
	    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}	
	
	//gettery	
	protected SensorManager getSensorManager(){
		return this.sensorManager;
	}
	protected Sensor getSensor(){
		return this.sensor;
	}
	public Float getLastX() {
		return lastX;
	}
	public void setLastX(Float lastX) {
		this.lastX = lastX;
	}
	public Float getLastY() {
		return lastY;
	}
	public void setLastY(Float lastY) {
		this.lastY = lastY;
	}
	public Float getLastZ() {
		return lastZ;
	}
	public void setLastZ(Float lastZ) {
		this.lastZ = lastZ;
	}
}

