package com.fixus.td.sensors;

import com.fixus.td.popup.SettingsPopUp;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPS extends Service implements LocationListener {
	private final static String POP_TITLE = "GPS Settings";
	private final static String POP_MSG = "GPS is not enabled. Do you want open settings menu?";
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // 5 sec
	/*
	 * Jeszcze nie uzywane!! Trzeba obsluzyc te minimalne wartosci, jesli chcemy
	 * a uwazam ze chyba warto
	 */
	
	private final Context mContext;

	private boolean canGetLocation;
	private String myBestProvider;
	private Location location;
	private double latitude;
	private double longitude;
	protected LocationManager locationManager;

	public GPS(Context context) {
		this.mContext = context;
		init();
	}

	private void init() {
		locationManager = (LocationManager) mContext
				.getSystemService(LOCATION_SERVICE);
		// sprawdzmy, czy jest dostepny GPS_PROVIDER albo NETWORK_PROBIDER
		this.canGetLocation = 
				locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		//pobieramy pierwsza lokalizacje(a dokladnie wysylamy request, w celu jej uzyskania)
		getLocation();
	}

	public Location getLocation() {
		try {
			if (this.canGetLocation) {
				Criteria criteria = new Criteria();
				myBestProvider = locationManager.getBestProvider(criteria,
						false);
				reqeustForUpdate();
			}else{
				showSettingsPopUp();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	private void reqeustForUpdate() {
		locationManager.requestLocationUpdates(myBestProvider,
				MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

		if (locationManager != null) {
			location = locationManager.getLastKnownLocation(myBestProvider);
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		}
	}

	public double getLatitude() {
		return (location != null) ? latitude = location.getLatitude()
				: latitude;
	}

	public double getLongitude() {
		return (location != null) ? longitude = location.getLongitude()
				: longitude;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public void showSettingsPopUp() {
		SettingsPopUp tmp = new SettingsPopUp(mContext);
		tmp.showPopUp(POP_TITLE, POP_MSG,
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	}

	@Override
	public void onLocationChanged(Location location) {
		/*
		 * Mozna tu dodac sprawdzanie minimalnego przemieszczenia, 
		 * albo cos takiego jesli chcemy
		 */
		this.location = location;
	}	

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		//hmmm nie wiem, czy to nam do czegos potrzebne szczerze mowiac...
	}

	@Override
	public void onProviderDisabled(String provider) {
		showSettingsPopUp();
	}

	public void onResume() {
		getLocation();//reqeustForUpdate();
	}

	public void onPause() {
		locationManager.removeUpdates(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
