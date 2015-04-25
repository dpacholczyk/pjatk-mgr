package com.fixus.towerdefense.tools;

import android.location.Location;

public class PersonPosition extends Position {
	public PersonPosition() {
		
	}
	
	public PersonPosition(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	public PersonPosition(Location location) {
		super(location);
	}
}
