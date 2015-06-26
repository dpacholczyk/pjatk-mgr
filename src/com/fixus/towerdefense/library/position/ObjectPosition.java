package com.fixus.towerdefense.library.position;

import com.fixus.towerdefense.library.configuration.Configuration;

import android.location.Location;
import android.util.Log;

public class ObjectPosition extends Position {
	public static final String TAG = "TD_OBJECTPOSITION";
	
	private double azimuth = 0;
	
	public ObjectPosition() {
		
	}
	
	public ObjectPosition(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	public ObjectPosition(Location location) {
		super(location);
	}	
	
	public void setAzimut(double azimuth) {
		this.azimuth = azimuth;
	}
	
	public double getAzimuth() {
		return this.azimuth;
	}
	
	public boolean isSeen(double personAzimuth, double viewAngle) {
		double halfAngle = viewAngle / 2;
		double topLimit = personAzimuth + halfAngle;
		double bottomLimit = personAzimuth - halfAngle;
		
		/*
		 * Dzialamy w zakresie <0,360> bo this.azimuth moze miec tylko takie wartosci
		 */
		if(topLimit >= 360) {
			/*
			 * Dlatego jesli gorna granica przekorczy wartosc 360 stopni
			 * musimy ja "przekrecic o 360 stopni(przez odj�cie 360 stopni)
			 */
			topLimit -= 360;
		}
		if(bottomLimit < 0) {
			/*
			 * Analogicznie z dolna granica z tym, �e je�li ona jest mniejsza ni� 0
			 * to musimy jej doda� 360 stopni
			 */
			bottomLimit += 360;
		}
		
		/*
		 * Dwa glowne przypadki:
		 * 
		 * - dolna granica liczbowo jest wieksza od gornej
		 * - gorna granica liczbowo wieksza od dolnej
		 */
		if(bottomLimit > topLimit){
			/*
			 * Przypadek 1 dolna granica liczbowo jest wieksza od gornej. 
			 * I tu znowu sa dwie mozliwosci poprawne:
			 */
			if(this.azimuth >= bottomLimit){
				/*
				 * this.azimuth jest wiekszy/rowny od dolnej granicy czyli jest z zakresu
				 * <bottomLimit, 360>. Wtedy widzimy obiekt
				 */
				return true;
			}
			if(this.azimuth <= topLimit){
				/*
				 * Drugi przypadek gdy widzimy obiekt jest gdy this.azimuth jest mniejszy/rowny
				 * gornej granicy, czyli jest w wartosci przedzialu
				 * <0,topLimit>
				 */
				return true;
			}
		}else{
			/*
			 * Przypadek 2. Prawda jest tylko wtedy gdy this.azimuth jest w przedziale
			 * <bottomLimit,topLimit>
			 */
			if(this.azimuth >= bottomLimit && this.azimuth <= topLimit){
				return true;
			}
			
		}
		return false;
	}

	public boolean inDistance(double distance) {
		if(distance <= (Configuration.distanceLimit + Configuration.distanceOffset) && distance >= (Configuration.distanceLimit - Configuration.distanceOffset)) {
			return true;
		} else {
			return false;
		}
	}
	
	public float countObjectPosition(double personAzimuth, double viewAngle) {
		float moveOffset = (float)Configuration.horizontalViewAngle / 80;
		// -(azimuthInDegress - directionInDegress)
		boolean changeSide = false;
		Log.d("COUNT_OBJECT", "personAzimuth_old: " + personAzimuth);
		Log.d("COUNT_OBJECT", "this.azimuth_old: " + this.azimuth);
		float newAzimuth = (float)this.azimuth;
		if((personAzimuth + Configuration.horizontalViewAngle/2) < this.azimuth) {
			personAzimuth += 360;
			changeSide = true;
		} else if((this.azimuth + Configuration.horizontalViewAngle/2) < personAzimuth) {
			newAzimuth += 360;
//			changeSide = false;
		}

		Log.d("COUNT_OBJECT", "personAzimuth_new: " + personAzimuth);
		Log.d("COUNT_OBJECT", "this.azimuth_old: " + newAzimuth);
		Log.d("COUNT_OBJECT", "change: " + changeSide);
		float diffrence = changeSide ? (float)((personAzimuth - newAzimuth) * -1) : (float)(personAzimuth - newAzimuth);
		Log.d("COUNT_OBJECT", "diff: " + diffrence);
		float newPosition = (float)( -diffrence ) / moveOffset;
		Log.d("COUNT_OBJECT", "new1: " + newPosition);
		// kolejność odejmowania jest ważna. 
		// jeśli będzie zła kolejność to kierunek przesunięcie będzie odwrotny 
		return newPosition;
	}
}
