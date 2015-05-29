package com.fixus.towerdefense.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.location.Location;

public class MapPoint {
	public static final String TAG = "TD_MAPPOINT";
	public static final double METERS_IN_DEGREES = 111300;
	
	public static Location getLocation(Location location, int radius) {
		Random random = new Random();
		// promień podany w kątach
		double r = radius / METERS_IN_DEGREES;
//		Log.d(TAG, "radius: " + radius);
//		Log.d(TAG, "r: " + r);
//		Log.d(TAG, "metry w stopniu: " + METERS_IN_DEGREES);
		double x0 = location.getLongitude();
		double y0 = location.getLatitude();
		// czy brak 1 w zbiorze nie jest problemem ?
		double u = random.nextDouble();
		double v = random.nextDouble();
		double w = r * Math.sqrt(u);
		double t = 2 * Math.PI * v;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		
//		Log.d(TAG, "u: " + u);
//		Log.d(TAG, "v: " + v);
		
		// uwzględnienie zmniejszenie dystansu wschód - zachód
//		double xPrim = x / Math.cos(y0);
		double xPrim = x;
		
		// nowa lokalizacja
		Location randomLocation = new Location("Random location in radius " + radius);
		randomLocation.setLongitude(xPrim + x0);
		randomLocation.setLatitude(y + y0);
		
		return randomLocation;
	}
	
	public static List<Location> generatePoints(Location location, int radius, int pointsCount) {
		List<Location> locations = new ArrayList<Location>();
//		Log.d(TAG, "Points count: " + pointsCount);
		for(int i = 0; i < pointsCount; i++) {
//			Log.d(TAG, "Losuje punkt " + i);
			locations.add(getLocation(location, radius));
		}
		
		return locations;
	}
}
