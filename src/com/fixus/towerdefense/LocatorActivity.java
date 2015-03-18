package com.fixus.towerdefense;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.fixus.td.sensors.GPS;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class LocatorActivity extends FragmentActivity {
    private static final String TAG = "TD_LOCATORACTIVITY";
	private GoogleMap googleMap;
	private GPS gps;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
        this.gps = new GPS(this);
        if(!this.gps.canGetLocation()){
	    	this.gps.showSettingsPopUp();
	    }
        this.setupMap();
    }
 
    @Override
    protected void onResume() {
        super.onResume();
    }
	
	private void setupMap() {
		if(this.googleMap == null) {
			Log.d(TAG, "Google map is null");
			this.googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			
			if(this.googleMap != null) {
				Log.d(TAG, "Mapa utworzona");
				this.googleMap.setMyLocationEnabled(true);
				if(this.gps != null) {
					if(this.gps.getLocation() != null) {
						LatLng center = new LatLng(this.gps.getLocation().getLatitude(), this.gps.getLocation().getLongitude());
						Log.d(TAG, "center: " + this.gps.getLocation().getLongitude() + ", " + this.gps.getLocation().getLongitude());
						CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(center)      // Sets the center of the map to Mountain View
					    .zoom(15)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
					    .build();                   // Creates a CameraPosition from the builder
						this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					}
				}
			}
			
		}
	}
    
}