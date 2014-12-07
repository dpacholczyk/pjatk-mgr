package com.fixus.td.sensors;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GPS extends Sensor {
	private double latitude;
	private double longitude;
	private double altitude;
	
	private TextView latView = null;
	private TextView lonView = null;
	private TextView altView = null;
	
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			altitude = location.getAltitude();
			
			if(Sensor.logData) {
				Log.d(Sensor.logDataTag, "lat: " + latitude);
				Log.d(Sensor.logDataTag, "lon: " + longitude);
				Log.d(Sensor.logDataTag, "alt: " + altitude);
			}
			
			if(latView != null) {
				latView.setText(String.valueOf(latitude));
			}
			if(lonView != null) {
				lonView.setText(String.valueOf(longitude));
			}
			if(altView != null) {
				altView.setText(String.valueOf(altitude));
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public GPS(Context context, int mode, String systemService) {
		super(context, mode, systemService);
		// TODO Auto-generated constructor stub
	}
	
	public GPS(Context context, int mode, String systemService, boolean debug) {
		super(context, mode, systemService, debug);
	}

	public GPS(Context context, int mode, String systemService, TextView lat, TextView lon, TextView alt) {
		super(context, mode, systemService);
		this.latView = lat;
		this.lonView = lon;
		this.altView = alt;
	}	
	
	/**
	 * @TODO
	 * parametry powinny byæ konfigurowalne
	 * ustaliæ czy wystarczy z poziomu kodu czy user te¿ powinien musieæ
	 * kontrola czy gps odpalony
	 */
	public void run() {
		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 2, locationListener);
	}

}
