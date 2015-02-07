package com.fixus.td.sensors;

import java.math.BigDecimal;
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
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		this.cSensorList = new HashMap<Integer,Sensor>();
		this.cSensorLastValues = new HashMap<Integer, float[]>();
	}
	/**
	 * Dodaj obsluge wybranego sensora
	 * 
	 * @param iSensorId - Static int z klasy Sensor np. Sensor.TYPE_ACCELEROMETER
	 */
	public void addSensor(Integer iSensorId){
		/*
		 * Dodac jakies sprawdzanie, czy na danym urzadzeniu w ogole dany sensor istnieje
		 * 
		 * Pytanie, co zrobic jak nie istnieje? propozycja jest wywalenie jakiegos wyjatku
		 * po czym wyswietlenie info, ze na tym urzadzeniu nie pograsz sobie w ta gierke
		 */
		Sensor oSensor = sensorManager.getDefaultSensor(iSensorId);
		sensorManager.registerListener(this, oSensor, SensorManager.SENSOR_DELAY_UI);
		cSensorList.put(iSensorId, oSensor);
	}
	/**
	 * Zwraca tablice rezultatow dla danego sensora
	 * 
	 * Uwaga! Zwraca O,O,O jak dany sensor nie zostal obsluzony!
	 * 
	 * @param iSensorId - Static int z klasy Sensor np. Sensor.TYPE_ACCELEROMETER
	 * @return Tablica flaot z rezultatami danego sensora
	 */
	public float[] getLastMatrix(Integer iSensorId){
		return cSensorLastValues.containsKey(iSensorId) ? cSensorLastValues.get(iSensorId) : new float[]{0,0,0};
	}
	/**
	 * Zwraca tablice rezultatow dla danego sensora z dokladnoscia do przeslanej ilosci miejsc po przecinku
	 * 
	 * Uwaga! Zwraca O,O,O jak dany sensor nie zostal obsluzony!
	 * 
	 * @param iSensorId - Static int z klasy Sensor np. Sensor.TYPE_ACCELEROMETER
	 * @param iPrecision - liczba miejsc po przecinku
	 * @return Tablica flaot z rezultatami danego sensora
	 */
	public float[] getLastMatrix(Integer iSensorId, int iPrecision){
		float[] arrayToReturn;
		
		if(cSensorLastValues.containsKey(iSensorId)){
			arrayToReturn = cSensorLastValues.get(iSensorId);
			//zmieniamy precyzje
			for(int i = 0; i < arrayToReturn.length;++i){
				arrayToReturn[i] = new BigDecimal(arrayToReturn[i]).setScale(iPrecision, BigDecimal.ROUND_HALF_UP).floatValue();
			}
		}else{
			arrayToReturn = new float[]{0,0,0};
		}
		
		return arrayToReturn;
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
		//zaaktualizuj dane na temat ostatnich wartosci w mapie
		cSensorLastValues.put(event.sensor.getType(), event.values);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
}

