package com.fixus.towerdefense.game;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.util.Log;

public class GameStatus {
	public static final String TAG = "TD_GAMESTATUS";
	
	public static int points;
	
	public static double radius;
	
	public static List<Location> randomedPoints = new ArrayList<Location>();
	
	public static int getRadiusInMeters() {
		Log.d(TAG, "radius: " + radius);
		return (int)radius * 1000;
	}
}
