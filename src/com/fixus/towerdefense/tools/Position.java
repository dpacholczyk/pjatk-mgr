package com.fixus.towerdefense.tools;

import android.location.Location;

public abstract class Position {
	protected double latitude;
	protected double longitude;
	protected double altitude;
	
	public Position() {
		
	}
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.altitude = location.getAltitude();
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLongitude() {
		return this.longitude;
	}
	
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	public double getAltitude() {
		return this.altitude;
	}
}
