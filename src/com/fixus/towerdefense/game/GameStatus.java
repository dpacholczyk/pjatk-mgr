package com.fixus.towerdefense.game;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.util.Log;

import com.fixus.towerdefense.tools.PersonPosition;
import com.fixus.towerdefense.tools.PhonePosition;

public class GameStatus {
	public static final String TAG = "TD_GAMESTATUS";
	
	public static int points;
	
	public static double radius;
	
	public static List<Location> randomedPoints = new ArrayList<Location>();
	
	public static PhonePosition phone = null;
	
	public static PersonPosition player = null;
	
	public static int getRadiusInMeters() {
//		Log.d(TAG, "radius: " + radius);
		return (int)radius * 1000;
	}
}
