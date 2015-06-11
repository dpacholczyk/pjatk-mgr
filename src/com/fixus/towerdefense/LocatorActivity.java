package com.fixus.towerdefense;

import java.util.Map;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;

import com.fixus.td.popup.SettingsPopUp;
import com.fixus.td.sensors.GPS;
import com.fixus.towerdefense.game.GameStatus;
import com.fixus.towerdefense.game.LocationType;
import com.fixus.towerdefense.tools.MapPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocatorActivity extends FragmentActivity {
    public static final String INTENT_LAT_ID = "selectedLat";
    public static final String INTENT_LONG_ID = "selectedLng";
    
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
        if(GameStatus.isGameOver()){
        	SettingsPopUp oTmp = new SettingsPopUp(this);
        	oTmp.showPopupWithExitButton("Game is over", "Congratulation!",this);
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
								
								//ustawiamy ktora z lokalizacji, jest aktualnym targetPointem
								String sUniqueId = getUniqueId(arg0.getPosition().latitude,arg0.getPosition().longitude);
								//jesli obiekt nie jest jeszcze znaleziony to ustawiamy go jako target point i przechodzimy
								//do radar activity
								if(!GameStatus.isFound(sUniqueId)){
									GameStatus.setTargetPoint(sUniqueId);
									
									Intent intent = new Intent(LocatorActivity.this, RadarActivity.class);
								    
									intent.putExtra(INTENT_LAT_ID, arg0.getPosition().latitude);
								    intent.putExtra(INTENT_LONG_ID, arg0.getPosition().longitude);
								    
								    startActivity(intent);		
								}
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

			String sUniqeId = getUniqueId(randomPoint.getLatitude(),randomPoint.getLongitude());
			GameStatus.addLocation(randomPoint, sUniqeId ,LocationType.NOT_FOUND);
			this.googleMap.addMarker(
					getMarkerOption(randomPoint,i,LocationType.NOT_FOUND)
					);
		}
	}
	
	private void addRandomPoints(Map<String, Pair<Location, LocationType>> oPointsToCircle) {
		int i = 0;
		
		for(Pair<Location, LocationType> oSingleLocationData : oPointsToCircle.values()) {
			//Log.d(TAG, "Dodaje punkt: " + i);
			this.googleMap.addMarker(getMarkerOption(oSingleLocationData.first,i,oSingleLocationData.second));
			i++;
		}
	}
	
	private MarkerOptions getMarkerOption(Location oLocation, int iMarkerNumber, LocationType oType){
		MarkerOptions oOptions = new MarkerOptions();
		oOptions.position(new LatLng(oLocation.getLatitude(), oLocation.getLongitude()));
		oOptions.title("Random point: " + (iMarkerNumber + 1));
		/*
		 * Ustawiamy odpowiednia ikone w zaleznosci od stanu danego pkt
		 */
		switch(oType){
			case FOUND:
				oOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.success));
				break;
			case NOT_FOUND:
				oOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.treasure2));
				break;
			case TARGET:
				oOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.target2));
				break;
		}
			
		return oOptions;
	}
    
	
	private String getUniqueId(double dLat, double dLon){
		return dLat + "_" + dLon;
	}
}