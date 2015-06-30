package com.fixus.towerdefense.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.location.Location;
import android.util.Pair;

import com.fixus.towerdefense.tools.ObjectPosition;
import com.fixus.towerdefense.tools.PersonPosition;
import com.fixus.towerdefense.tools.PhonePosition;

public class GameStatus {
	public static final String TAG = "TD_GAMESTATUS";
	
	private static String CURRENT_UNIQUE_ID;
	
	private static int NUMBER_OF_POINTS_TO_FIND = -1;

	public static double radius;
	
	public static double horizontalViewAngle = 170.0;
	
	public static double moveOffset = 5;
	
	// próg dystansu.
	public static double distanceLimit = 15;
	
	// próg dystansu. obiekt będzie widziany na dystansie +/- próg
	public static double distanceOffset = 4;
	
	//private static List<Location> RANDOM_POINTS_ON_MAP = new LinkedList<Location>();
	private static Map<String,Pair<Location,LocationType>> RANDOM_POINTS_ON_MAP = new HashMap<String, Pair<Location,LocationType>>();
	
	public static PhonePosition phone = null;
	
	public static PersonPosition player = null;
	
	public static ObjectPosition currentPositon = null;
	
	public static boolean useDistance = false;
	
	public static int getRadiusInMeters() {
		//Log.d(TAG, "radius: " + radius);
		return (int)radius * 1000;
	}
	
	public static void addLocation(Location oNewLocation, String sUniqueID,LocationType oType){
		Pair<Location,LocationType> oData = new Pair<Location, LocationType>(oNewLocation, oType);
		RANDOM_POINTS_ON_MAP.put(sUniqueID, oData);
	}
	
	public static Map<String,Pair<Location,LocationType>> getLocationsList(){
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
	
	public static void setTargetPoint(String sUniqueId) {
		if(RANDOM_POINTS_ON_MAP.containsKey(sUniqueId)){
			
			CURRENT_UNIQUE_ID = sUniqueId;
			
			Pair<Location, LocationType> oPair;
			for(Entry<String, Pair<Location, LocationType>> oEntry : RANDOM_POINTS_ON_MAP.entrySet()){
				oPair = oEntry.getValue();
				if(oPair.second == LocationType.TARGET){
					oPair = new Pair<Location, LocationType>(oPair.first, LocationType.NOT_FOUND);
					RANDOM_POINTS_ON_MAP.put(oEntry.getKey(), oPair);
				}
			}
			Pair<Location, LocationType> oSingleLocationData = RANDOM_POINTS_ON_MAP.get(sUniqueId);
			oSingleLocationData = new Pair<Location, LocationType>(oSingleLocationData.first, LocationType.TARGET);
			RANDOM_POINTS_ON_MAP.put(sUniqueId, oSingleLocationData);
		}
	}
	
	public static void markCurrentPointAsFound() {
		if(RANDOM_POINTS_ON_MAP.containsKey(CURRENT_UNIQUE_ID)){
			Pair<Location, LocationType> oSingleLocationData = RANDOM_POINTS_ON_MAP.get(CURRENT_UNIQUE_ID);
			oSingleLocationData = new Pair<Location, LocationType>(oSingleLocationData.first, LocationType.FOUND);
			RANDOM_POINTS_ON_MAP.put(CURRENT_UNIQUE_ID, oSingleLocationData);
		}
	}
	
	public static boolean isFound(String sUniqueId) {
		if(RANDOM_POINTS_ON_MAP.containsKey(sUniqueId)){
			Pair<Location, LocationType> oSingleLocationData = RANDOM_POINTS_ON_MAP.get(sUniqueId);
			return oSingleLocationData.second == LocationType.FOUND;
		}else{
			return false;
		}
	}
	
	public static boolean isGameOver() {
		boolean bResult = true;
		
		Pair<Location, LocationType> oPair;
		if(RANDOM_POINTS_ON_MAP.size() == 0){
			return false;
		}
		for(Entry<String, Pair<Location, LocationType>> oEntry : RANDOM_POINTS_ON_MAP.entrySet()){
			oPair = oEntry.getValue();
			if(oPair.second != LocationType.FOUND){
				bResult = false;
				break;
			}
		}
		return bResult;
	}
	
}
