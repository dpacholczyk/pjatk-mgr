package com.fixus.td.sensors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Pair;

@SuppressLint("UseSparseArrays")
public class OurSensorManager2 implements SensorEventListener{
	private SensorManager sensorManager;
	/*
	 * Integer jest id sensora z enuma np Sensor.TYPE_ACCELEROMETER
	 * Druga Paira to:
	 * 
	 * sensor sam w sobie i kolejna Paira
	 * -limit danych dla mediany
	 * -precyzja w ktorej dane sa zapisane
	 */
	private Map<Integer,Pair<Sensor,Pair<Integer,Integer>>> cSensorList;
	private Map<Integer,Queue<Float[]>> cSensorLastValues;

	public OurSensorManager2(Context context) {
		startMe(context);
	}
	
	private void startMe(Context context){
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		this.cSensorList = new HashMap<Integer,Pair<Sensor,Pair<Integer,Integer>>>();
		this.cSensorLastValues = new HashMap<Integer, Queue<Float[]>>();
	}
	/**
	 * Dodaj obsluge wybranego sensora
	 * 
	 * @param iSensorId - Static int z klasy Sensor np. Sensor.TYPE_ACCELEROMETER
	 * @param iNumberOfResultsForMedian - liczba ostatnich wynikow z ktorych ma byc pobierana mediana
	 * @param iPrecision - liczba miejsc po przecinku dla rezultatow
	 */
	public void addSensor(Integer iSensorId, Integer iNumberOfResultsForMedian,int iPrecision){
		/*
		 * Dodac jakies sprawdzanie, czy na danym urzadzeniu w ogole dany sensor istnieje
		 * 
		 * Pytanie, co zrobic jak nie istnieje? propozycja jest wywalenie jakiegos wyjatku
		 * po czym wyswietlenie info, ze na tym urzadzeniu nie pograsz sobie w ta gierke
		 */
		Sensor oSensor = sensorManager.getDefaultSensor(iSensorId);
		sensorManager.registerListener(this, oSensor, SensorManager.SENSOR_DELAY_UI);
		Pair<Integer,Integer> oPairLimiters = new Pair<Integer,Integer>(iNumberOfResultsForMedian,iPrecision);
		Pair<Sensor,Pair<Integer,Integer>> oSingelSensor = new Pair<Sensor,Pair<Integer,Integer>>(oSensor, oPairLimiters);
		cSensorList.put(iSensorId, oSingelSensor);
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
		List<Float> arrayToReturn = new ArrayList<Float>();
		
		if(cSensorLastValues.containsKey(iSensorId)){
			Queue<Float[]> oQue = cSensorLastValues.get(iSensorId);
			//maksymalnie sa trzy wartosci x,y,z wiec bedziemy liczyc mediane dla 3 elementow
			List<Float> cOfX = new ArrayList<Float>();
			List<Float> cOfY = new ArrayList<Float>();
			List<Float> cOfZ = new ArrayList<Float>();
			for(Float[] aResults : oQue){
				if(aResults.length >= 1){
					//dodaj X
					cOfX.add(aResults[0]);
				}
				if(aResults.length >= 2){
					//dodaj Y
					cOfY.add(aResults[1]);
				}
				
				if(aResults.length >= 3){
					//dodaj Z
					cOfZ.add(aResults[2]);
				}
			}
			arrayToReturn.add(getMedian(cOfX.toArray(new Float[0]), 0));
			arrayToReturn.add(getMedian(cOfY.toArray(new Float[0]), 0));
			arrayToReturn.add(getMedian(cOfZ.toArray(new Float[0]), 0));
		}else{
			arrayToReturn.add(0.0f);
			arrayToReturn.add(0.0f);
			arrayToReturn.add(0.0f);
		}
		
		return fromFloatArrayToPrimitiveArray(arrayToReturn);
	}
	
	public void onPause() {
		 // W trakcie wstrzymania aplikacji zatrzymuje pobieranie aktualizowanych danych w celu
		 // zaoszczêdzenia energii
	    sensorManager.unregisterListener(this);
	}
	
	public void onResume() {
	    //Po przywrocenie wznawiamy aktualizaowanie danych
		if(!cSensorList.isEmpty()){
			for(Pair <Sensor,Pair<Integer,Integer>> oSingeSensorWithLimiters : cSensorList.values()){
				sensorManager.registerListener(this, oSingeSensorWithLimiters.first, SensorManager.SENSOR_DELAY_UI);
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
		//limit danych do mediany
		Integer iNumberOfResultsForMedian;
		Integer iPrecison = 1;
		//pobierz informacje odnosnie limitów i id sensora
		Pair<Sensor,Pair<Integer,Integer>> oSingelSensor = cSensorList.get(event.sensor.getType());
		/*
		 * Para zawiera:
		 * - liczbe ostatnich wynikow trzymanych na potrzeby mediany
		 * - precyzje ich przechowywania
		 */
		Pair<Integer,Integer> oSensorLimiters = oSingelSensor.second;
		iNumberOfResultsForMedian = oSensorLimiters.first;
		
		Queue<Float[]> oQue = cSensorLastValues.containsKey(event.sensor.getType()) ? 
				cSensorLastValues.get(event.sensor.getType()):
				new LinkedList<Float[]>();
		Float[] oLastValues = getFloatMatrixWithPrecision(event.values, iPrecison);
		
		if(oQue.size() >= iNumberOfResultsForMedian){
			oQue.poll();
		}
		
		oQue.add(oLastValues);
		
		cSensorLastValues.put(event.sensor.getType(), oQue);
	}
	
	private Float[] getFloatMatrixWithPrecision(float[] oLastResult, int iPrecision){
		List<Float> arrayToReturn = new ArrayList<Float>();
		for(int i = 0; i < oLastResult.length;++i){
			//zmieniamy precyzje
			arrayToReturn.add(
					new BigDecimal(oLastResult[i]).setScale(iPrecision, BigDecimal.ROUND_HALF_UP).floatValue()
					);
		}	
		return arrayToReturn.toArray(new Float[0]);
	}
	
	private Float getMedian(Float[] oTmp,int iPrecision){
		if(oTmp.length == 0){
		 return 0.0f;
		}else{
			Arrays.sort(oTmp);
	
			double oMedian;
			if (oTmp.length % 2 == 0)
			    oMedian = ((double)oTmp[oTmp.length/2] + (double)oTmp[oTmp.length/2 - 1])/2;
			else
			    oMedian = (double) oTmp[oTmp.length/2];
			
			return new BigDecimal(oMedian).setScale(iPrecision, BigDecimal.ROUND_HALF_UP).floatValue();
		}
	}
	
	private float[] fromFloatArrayToPrimitiveArray(List<Float> oList){
	   	float[] oReturn = new float[oList.size()];
	   	int i = 0;
	    for (Float n : oList) {
	        oReturn[i++] = n;
	    }
	    return oReturn;
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
}

