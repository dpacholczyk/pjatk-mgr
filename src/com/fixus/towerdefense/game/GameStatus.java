package com.fixus.towerdefense.game;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class GameStatus {
	public static int points = 3;
	
	public static double radius = 5.0;
	
	public static List<Location> randomedPoints = new ArrayList<Location>();
	
	public static int getRadiusInMeters() {
		return (int)radius * 1000;
	}
}
