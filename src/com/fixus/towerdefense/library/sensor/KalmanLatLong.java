package com.fixus.towerdefense.library.sensor;

import java.math.BigDecimal;

public class KalmanLatLong {
	private static final float MIN_ACCURACY = 1;

	private float fMetersPerSecond;
	private long lLastTimestamp;
	private double dLastLatitude;
	private double dLastLongitude;
	//result precision
	private int iPrecision;
	private float fVariance;
	/**
	 * 
	 * @param fMetersPerSecond - trzeba dac 3 metry albo cos kolo tego
	 * @param iPrecision - mowi jaka dokladnosc po przecinku, powinna byc dla dlugosci i szerokosci
	 */
	public KalmanLatLong(float fMetersPerSecond,int iPrecision) {
		this.fMetersPerSecond = fMetersPerSecond;
		this.iPrecision = iPrecision;
		fVariance = -1;
	}

	public long getTimestamp() {
		return lLastTimestamp;
	}

	public double getLatitude() {
		return new BigDecimal(dLastLatitude).setScale(iPrecision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getLongitude() {
		return new BigDecimal(dLastLongitude).setScale(iPrecision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public float getAccuracy() {
		return (float) Math.sqrt(fVariance);
	}
	/**
	 * Filtr Kalmana
	 * 
	 * @param dNewLatitude nowa szerokosc
	 * @param dNewLongitudenowa dlugosc
	 * @param pomiar standardowego odchylenia danego czeku
	 * @param czas pomiaru
	 */
	public void Process(double dNewLatitude, double dNewLongitude,
			float fAccuracy, long lTimeStampInMilliseconds) {
		/*
		 * Sprawdzenie, czy wartosc odchylenia pomiaru jest 
		 * wieksza niz zalozona minimalna wartosc (1 m)
		 */
		if (fAccuracy < MIN_ACCURACY){
			fAccuracy = MIN_ACCURACY;
		}	
		if (fVariance < 0) {
			// brak inicjalizacj czyli mamy pierwsze odpalenie
			this.lLastTimestamp = lTimeStampInMilliseconds;
			dLastLatitude = dNewLatitude;
			dLastLongitude = dNewLongitude;
			fVariance = fAccuracy * fAccuracy;
		} else {
			//Metoda Klamana - czesc wlasciwa
			long lTimestampDiff = lTimeStampInMilliseconds
					- this.lLastTimestamp;
			if (lTimestampDiff > 0) {
				/*
				 * warunek sprawdzajacy czy mielismy zmiane w czasie
				 * jesli taka zmiana miala miejsce, to musimy przeliczyc 
				 * wspolczynik kowariancji
				 */		
				fVariance += lTimestampDiff * fMetersPerSecond
						* fMetersPerSecond / 1000;
				this.lLastTimestamp = lTimeStampInMilliseconds;
			}

			//wzmocnienie maciezy Kalmana
			float fKalmanGain = fVariance / (fVariance + fAccuracy * fAccuracy);
			//zastosowanie wzmocnienia
			dNewLatitude += fKalmanGain * (dNewLatitude - dNewLatitude);
			dLastLongitude += fKalmanGain * (dNewLongitude - dLastLongitude);
			// nowa kowariancja (IdentityMatrix - K) * Covarariance
			fVariance = (1 - fKalmanGain) * fVariance;
		}
	}
}