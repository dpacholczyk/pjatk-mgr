package com.fixus.towerdefense;

import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.fixus.td.sensors.GPS;
import com.fixus.towerdefense.game.GameStatus;
import com.fixus.towerdefense.tools.MapPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
					    .zoom(10)                   // Sets the zoom
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
						//Circle circle = 
						this.googleMap.addCircle(circleOptions);
						
//						Log.d(TAG, "ilosc randomowych: " + GameStatus.randomedPoints.size());
//						Log.d(TAG, "game points: " + GameStatus.points);
						
						/**
						 * @TODO - sprawdzić czy da się ustalić czy wskazany losowy punkt jest ulicą (dostępny)
						 * jeśli nie wylosować jeszcze raz
						 */
						if(GameStatus.getLocationsList().size() != 0) {
//							Log.d(TAG, "Biore z size");
							this.addRandomPoints(GameStatus.getLocationsList());
						} else {
							Log.d(TAG, "Biorę z points");
							this.addRandomPoints(GameStatus.getNUMBER_OF_POINTS_TO_FIND());
//							
						}
						this.googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
							
							@Override
							public boolean onMarkerClick(Marker arg0) {
								Log.d(TAG, arg0.getTitle() + " | " + arg0.getPosition().toString());
								
								return false;
							}
						});
						this.googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
							
							@Override
							public void onInfoWindowClick(Marker arg0) {
								Log.d(TAG, "info: " + arg0.getTitle() + " | " + arg0.getPosition().toString());
								Intent intent = new Intent(LocatorActivity.this, RadarActivity.class);
							    intent.putExtra("selectedLat", arg0.getPosition().latitude);
							    intent.putExtra("selectedLng", arg0.getPosition().longitude);
							    startActivity(intent);								
							}
						});

					}
					
				}
			}
			
		}
	}
	
	private void addRandomPoints(int pointsCount) {
		for(int i = 0; i < pointsCount; i++) {
			Location randomPoint = MapPoint.getLocation(this.gps.getLocation(), GameStatus.getRadiusInMeters());
			
			//Log.d(TAG, "Dodaje punkt: " + i);
			//Log.d(TAG, "losowy punkt : " + randomPoint.getLatitude() + " | " + randomPoint.getLongitude());
			GameStatus.addLocation(randomPoint);
			this.googleMap.addMarker(new MarkerOptions()
	        .position(new LatLng(randomPoint.getLatitude(), randomPoint.getLongitude()))
	        .title("Random point: " + (i + 1)));
		}
	}
	
	private void addRandomPoints(List<Location> points) {
		int i = 0;
		for(Location location : points) {
			Log.d(TAG, "Dodaje punkt: " + i);
			this.googleMap.addMarker(new MarkerOptions()
	        .position(new LatLng(location.getLatitude(), location.getLongitude()))
	        .title("Random point: " + (i + 1)));
			i++;
		}
	}
    
}