package com.fixus.towerdefense.library.configuration;

public class Configuration {
	public static String texturePath = "Textures/basic032.jpg";
	
	public static String modelPath = "Models/chest2.obj";
	
	public static String materialPath = "Common/MatDefs/Misc/Unshaded.j3md";
	
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
	
	public static boolean blockShow = false;
}
