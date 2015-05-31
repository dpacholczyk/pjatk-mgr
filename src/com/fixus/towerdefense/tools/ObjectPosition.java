package com.fixus.towerdefense.tools;

import com.fixus.towerdefense.game.GameStatus;

import android.location.Location;

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
		if(distance <= (GameStatus.distanceLimit + GameStatus.distanceOffset) && distance >= (GameStatus.distanceLimit - GameStatus.distanceOffset)) {
			return true;
		} else {
			return false;
		}
	}
	
	public float countObjectPosition(double personAzimuth, double viewAngle) {
		float moveOffset = (float)(viewAngle/40	);
		float newPosition = (float)( ((this.azimuth - personAzimuth) / moveOffset) );
		// kolejność odejmowania jest ważna. 
		// jeśli będzie zła kolejność to kierunek przesunięcie będzie odwrotny 
		return newPosition;
	}
}
