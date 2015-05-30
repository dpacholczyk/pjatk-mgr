package com.fixus.towerdefense.tools;

import com.fixus.towerdefense.game.GameStatus;

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
		if(topLimit >= 360) {
			topLimit -= 360;
		}
		if(bottomLimit < 0) {
			bottomLimit += 360;
		}
		
//		Log.d("SEEN", "personAzimuth: " + personAzimuth);
//		Log.d("SEEN", "objectAzimuth: " + this.azimuth);
//		Log.d("SEEN", "topLimit: " + topLimit);
//		Log.d("SEEN", "bottomLimit: " + bottomLimit);
		
		// warunek który sprawdza czy zakresy nie są w w przeciwstawnych ćwiartkach. Jesli tak to należy sprawdzić dwa zakresy
		if(topLimit >= 0 && (bottomLimit >= 270 && bottomLimit < 360)) {
			if((this.azimuth >= 0 && this.azimuth < topLimit) || (this.azimuth < 360 && this.azimuth >= bottomLimit)) {
				return true;
			}
		}
		if(this.azimuth <= topLimit && this.azimuth > bottomLimit) {
			return true;
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
