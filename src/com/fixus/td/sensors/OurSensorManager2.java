package com.fixus.td.sensors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.fixus.td.popup.SettingsPopUp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Pair;

@SuppressLint("UseSparseArrays")
public class OurSensorManager2 implements SensorEventListener{
	private static final String POPUP_TITLE = "Required sensor isn't available";
	private SensorManager sensorManager;
	private int iSensorWorkMode;
	private Set<Integer> cAvailableSensors;
	private Context oContext;
	/*
	 * Integer jest id sensora z enuma np Sensor.TYPE_ACCELEROMETER
	 * 	Wartosci mapy jest para:
	 *	- sensor sam w sobie i kolejna para
	 * 		-limit danych dla mediany
	 * 		-precyzja w ktorej dane sa zapisane
	 */
	private Map<Integer,Pair<Sensor,Pair<Integer,Integer>>> cSensorList;
	/*
	 * Integer jest id sensora z enuma np Sensor.TYPE_ACCELEROMETER
	 * Wartosci mapy jest kolejka, z ostatnimi wynikami. Dlugosc kolejki
	 * zalezy od wartosci limitera z obiektu cSensorList
	 */
	private Map<Integer,Queue<Float[]>> cSensorLastValues;
	/**
	 * Podstawowy konstruktor. Ustawia tryb czulosci sensorow na wartosc 
	 * domyslna.
	 * 
	 * @param context context z klasy activity
	 */
	public OurSensorManager2(Context context) {
		this(context, SensorManager.SENSOR_DELAY_GAME);
	}
	/**
	 * Konstrukto umozliwiajacy dodatkowo ustawienia w jakim trybie
	 * czulosci powinny dzialac sensory
	 * 
	 * @param context context z klasy activity
	 * @param iSensorWorkMode wartosc z klasy SensorManager np. SensorManager.SENSOR_DELAY_GAME
	 */
	public OurSensorManager2(Context context,int iSensorWorkMode) {
		this.iSensorWorkMode = iSensorWorkMode;
		this.oContext = context;
		startMe();
	}
	
	private void startMe(){
		this.sensorManager = (SensorManager)this.oContext.getSystemService(Context.SENSOR_SERVICE);
		this.cSensorList = new HashMap<Integer,Pair<Sensor,Pair<Integer,Integer>>>();
		this.cSensorLastValues = new HashMap<Integer, Queue<Float[]>>();
		this.cAvailableSensors = new HashSet<Integer>();
		
		for(Sensor sSingelSensor : this.sensorManager.getSensorList(Sensor.TYPE_ALL)){
			this.cAvailableSensors.add(sSingelSensor.getType());
		}
	}
	/**
	 * Dodaj obsluge wybranego sensora
	 * 
	 * @param iSensorId - Static int z klasy Sensor np. Sensor.TYPE_ACCELEROMETER
	 * @param iNumberOfResultsForMedian - liczba ostatnich wynikow z ktorych ma byc pobierana mediana
	 * @param iPrecision - liczba miejsc po przecinku dla rezultatow
	 */
	public void addSensor(Integer iSensorId, Integer iNumberOfResultsForMedian,int iPrecision){
		if(this.cAvailableSensors.contains(iSensorId)){
			Sensor oSensor = sensorManager.getDefaultSensor(iSensorId);
			//rejestracja dangeo sensora
			sensorManager.registerListener(this, oSensor, iSensorWorkMode);
			//utworzenie pary limiterow (limit liczby ostatnich wynikow, precyzje przechowywanych wynikow)
			Pair<Integer,Integer> oPairLimiters = new Pair<Integer,Integer>(iNumberOfResultsForMedian,iPrecision);
			//id sensora i jego limitery
			Pair<Sensor,Pair<Integer,Integer>> oSingelSensor = new Pair<Sensor,Pair<Integer,Integer>>(oSensor, oPairLimiters);
			cSensorList.put(iSensorId, oSingelSensor);
		}else{
			SettingsPopUp oTmp = new SettingsPopUp(this.oContext);
			oTmp.showSimplePopup(POPUP_TITLE, POPUP_TITLE);
		}
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
			//majac listy osatnich wynikow kolejno dla x,y,z wyliczamy z nich mediane
			//poczym zwracamy tablice 3 median
			arrayToReturn.add(getMedian(cOfX.toArray(new Float[0]), 0));
			arrayToReturn.add(getMedian(cOfY.toArray(new Float[0]), 0));
			arrayToReturn.add(getMedian(cOfZ.toArray(new Float[0]), 0));
		}else{
			//zadany sensor nieistnieje
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
				sensorManager.registerListener(this, oSingeSensorWithLimiters.first, iSensorWorkMode);
			}
		}
	}	
	
	public void onDestroy() {
	    this.onPause();
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
		//pobranie/utworzenie kolejki osatnich wynikow
		Queue<Float[]> oQue = cSensorLastValues.containsKey(event.sensor.getType()) ? 
				cSensorLastValues.get(event.sensor.getType()):
				new LinkedList<Float[]>();
		//pobranie informacji odnosnie nowych wartosci danego sensora		
		Float[] oLastValues = getFloatMatrixWithPrecision(event.values, iPrecison);
		//jesli kolejka ma juz rozmiar wiekszy albo rowny docelowemu rozmiarowi
		//trzeba usunac elemetn tak by maksymalny rozmiar kolejki zostal zachowany
		if(oQue.size() >= iNumberOfResultsForMedian){
			oQue.poll();
		}
		
		oQue.add(oLastValues);
		
		cSensorLastValues.put(event.sensor.getType(), oQue);
	}
	/**
	 * Zmienia precyzje float, do podanej liczby miejsc po przecinku
	 * 
	 * @param oLastResult Oryginalna tablica z floatami
	 * @param iPrecision Liczba miejsc po przecinku
	 * @return
	 */
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
	/**
	 * Liczy mediane dla tablicy floatow i zwraca ja z odpowiednia precyzja
	 * 
	 * @param oTmp
	 * @param iPrecision
	 * @return
	 */
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
	
	/**
	 * Zamienia tablice obiektow typu Float na prymitywy typo float
	 * 
	 * @param oList
	 * @return
	 */
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

