package com.fixus.towerdefense.game;

import java.util.LinkedList;
import java.util.List;

import android.location.Location;

import com.fixus.towerdefense.tools.ObjectPosition;
import com.fixus.towerdefense.tools.PersonPosition;
import com.fixus.towerdefense.tools.PhonePosition;

public class GameStatus {
	public static final String TAG = "TD_GAMESTATUS";
	
	private static int NUMBER_OF_POINTS_TO_FIND = -1;

	public static double radius;
	
	public static double horizontalViewAngle = 90.0;
	
	public static double moveOffset = 5;
	
	// próg dystansu.
	public static double distanceLimit = 15;
	
	// próg dystansu. obiekt będzie widziany na dystansie +/- próg
	public static double distanceOffset = 4;
	
	private static List<Location> RANDOM_POINTS_ON_MAP = new LinkedList<Location>();
	
	public static PhonePosition phone = null;
	
	public static PersonPosition player = null;
	
	public static ObjectPosition currentPositon = null;
	
	public static int getRadiusInMeters() {
		//Log.d(TAG, "radius: " + radius);
		return (int)radius * 1000;
	}
	
	public static void addLocation(Location oNewLocation){
		RANDOM_POINTS_ON_MAP.add(oNewLocation);
	}
	
	public static List<Location> getLocationsList(){
		return RANDOM_POINTS_ON_MAP;
	}
	
	public static int getNUMBER_OF_POINTS_TO_FIND() {
		return NUMBER_OF_POINTS_TO_FIND;
	}

	public static void setNUMBER_OF_POINTS_TO_FIND(int nUMBER_OF_POINTS_TO_FIND) {
		if(NUMBER_OF_POINTS_TO_FIND ==  -1){
			NUMBER_OF_POINTS_TO_FIND = nUMBER_OF_POINTS_TO_FIND;
		}
	}
}
