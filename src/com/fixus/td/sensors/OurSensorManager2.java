package com.fixus.td.sensors;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

@SuppressLint("UseSparseArrays")
public class OurSensorManager2 implements SensorEventListener{
	private SensorManager sensorManager;
	private Map<Integer,Sensor> cSensorList;
	private Map<Integer,float[]> cSensorLastValues;

	public OurSensorManager2(Context context) {
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
		this.cSensorList = new HashMap<Integer,Sensor>();
		this.cSensorLastValues = new HashMap<Integer, float[]>();
	}
	
	public void addSensor(Integer iSensorId){
		Sensor oSensor = sensorManager.getDefaultSensor(iSensorId);
		sensorManager.registerListener(this, oSensor, SensorManager.SENSOR_DELAY_UI);
		cSensorList.put(iSensorId, oSensor);
	}
	
	public float[] getLastMatrix(Integer iSensorId){
		return cSensorLastValues.containsKey(iSensorId) ? cSensorLastValues.get(iSensorId) : new float[]{0,0,0};
	}
	
	public void onPause() {
		 // W trakcie wstrzymania aplikacji zatrzymuje pobieranie aktualizowanych danych w celu
		 // zaoszczêdzenia energii
	    sensorManager.unregisterListener(this);
	}
	
	public void onResume() {
	    //Po przywrocenie wznawiamy aktualizaowanie danych
		if(!cSensorList.isEmpty()){
			for(Sensor singleSensor : cSensorList.values()){
				sensorManager.registerListener(this, singleSensor, SensorManager.SENSOR_DELAY_UI);
			}
		}
	}	
	
	//gettery	
	protected SensorManager getSensorManager(){
		return this.sensorManager;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		cSensorLastValues.put(event.sensor.getType(), event.values);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
}

