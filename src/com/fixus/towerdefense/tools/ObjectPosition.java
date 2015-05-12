package com.fixus.towerdefense.tools;

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
		Log.d(TAG, "halfAngle: " + halfAngle);
		Log.d(TAG, "topLimit before: " + topLimit);
		Log.d(TAG, "bottomLimit before: " + bottomLimit);
		if(topLimit >= 360) {
			topLimit -= 360;
		}
		if(bottomLimit < 0) {
			bottomLimit += 360;
		}
		Log.d(TAG, "topLimit after: " + topLimit);
		Log.d(TAG, "bottomLimit after: " + bottomLimit);
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
}
