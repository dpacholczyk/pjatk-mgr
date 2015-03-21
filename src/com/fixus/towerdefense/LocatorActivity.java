package com.fixus.towerdefense;

import java.util.Iterator;
import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.fixus.td.sensors.GPS;
import com.fixus.towerdefense.game.GameStatus;
import com.fixus.towerdefense.tools.MapPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
			this.googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			
			if(this.googleMap != null) {
				this.googleMap.setMyLocationEnabled(true);
				if(this.gps != null) {
					if(this.gps.getLocation() != null) {
						LatLng center = new LatLng(this.gps.getLocation().getLatitude(), this.gps.getLocation().getLongitude());
//						Log.d(TAG, "center: " + this.gps.getLocation().getLatitude() + ", " + this.gps.getLocation().getLongitude());
						CameraPosition cameraPosition = new CameraPosition.Builder()
					    .target(center)      // Sets the center of the map to Mountain View
					    .zoom(20)                   // Sets the zoom
					    .bearing(90)                // Sets the orientation of the camera to east
					    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
					    .build();                   // Creates a CameraPosition from the builder
						this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
//						Log.d(TAG, "obszar: " + GameStatus.getRadiusInMeters());
						// Instantiates a new CircleOptions object and defines the center and radius
						CircleOptions circleOptions = new CircleOptions()
						    .center(center)
						    .radius(GameStatus.getRadiusInMeters()); // In meters

						// Get back the mutable Circle
						Circle circle = this.googleMap.addCircle(circleOptions);
						
						/**
						 * @TODO - sprawdzić czy da się ustalić czy wskazany losowy punkt jest ulicą (dostępny)
						 * jeśli nie wylosować jeszcze raz
						 */
						if(GameStatus.randomedPoints.size() == 0) {
							this.addRandomPoints(GameStatus.points);
						} else {
							this.addRandomPoints(GameStatus.randomedPoints);
						}
					}
					
				}
			}
			
		}
	}
	
	private void addRandomPoints(int pointsCount) {
		for(int i = 0; i < pointsCount; i++) {
			Location randomPoint = MapPoint.getLocation(this.gps.getLocation(), GameStatus.getRadiusInMeters());
			
//			Log.d(TAG, "losowy punkt : " + randomPoint.getLatitude() + " | " + randomPoint.getLongitude());
			
			this.googleMap.addMarker(new MarkerOptions()
	        .position(new LatLng(randomPoint.getLatitude(), randomPoint.getLongitude()))
	        .title("Random point: " + (i + 1)));
		}
	}
	
	private void addRandomPoints(List<Location> points) {
		Iterator<Location> it = points.iterator();
		int i = 0;
		while(it.hasNext()) {
			this.googleMap.addMarker(new MarkerOptions()
	        .position(new LatLng(it.next().getLatitude(), it.next().getLongitude()))
	        .title("Random point: " + (i + 1)));
			i++;
		}
	}
    
}